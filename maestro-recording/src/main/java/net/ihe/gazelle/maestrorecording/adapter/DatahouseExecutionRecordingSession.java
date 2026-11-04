/*
 * Copyright 2026 IHE International.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ihe.gazelle.maestrorecording.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import net.ihe.gazelle.errorhandling.business.UnexpectedErrorBuilder;
import net.ihe.gazelle.maestro.api.business.message.StepRunFinished;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.test.Test;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestRunReport;
import net.ihe.gazelle.maestro.engine.business.ExecutionRecordingSession;
import net.ihe.gazelle.maestro.engine.business.RecordedOutputsObserver;
import net.ihe.gazelle.maestro.engine.business.TestReportRecordingException;
import net.ihe.gazelle.maestro.spi.business.recording.plan.ItemPlan;
import net.ihe.gazelle.maestro.spi.business.recording.plan.ReferencePlan;
import net.ihe.gazelle.maestrorecording.RecordingWriter;
import net.ihe.gazelle.security.business.acl.AccessControlList;

/** Serializes recording only; engine event processing remains independent. */
final class DatahouseExecutionRecordingSession implements ExecutionRecordingSession {
    private final RecordingSnapshotFactory snapshots = new RecordingSnapshotFactory();
    // One writer thread keeps step writes in completion order and off the engine's event thread.
    private final ExecutorService stepWriter = Executors.newSingleThreadExecutor(Thread.ofVirtual().factory());
    private final DatahouseTestReportRecordingService service;
    private final RecordingWriter writer;
    private final List<Test> definitions;
    private final Supplier<AccessControlList> acl;
    private final Supplier<List<Property>> inputs;
    private final String datahouseUrl;
    private final Map<RecordedStepKey, RecordedStep> recorded = new HashMap<>();
    private List<Property> inputSnapshot;
    private boolean finished;
    private RuntimeException recordingFailure;

    DatahouseExecutionRecordingSession(DatahouseTestReportRecordingService service, RecordingWriter writer,
            List<Test> definitions, Supplier<AccessControlList> acl, Supplier<List<Property>> inputs,
            String datahouseUrl) {
        this.service = service;
        this.writer = writer;
        this.definitions = List.copyOf(definitions);
        this.acl = acl;
        this.inputs = inputs;
        this.datahouseUrl = datahouseUrl;
    }

    @Override
    public void submitStep(StepRunFinished event, RecordedOutputsObserver observer) {
        // Copy now: the engine keeps mutating its report while the write is queued.
        StepRunReport stepSnapshot = snapshots.copy(event.getStepRunReport());
        try {
            stepWriter.execute(() -> {
                try {
                    StepRunFinished published = record(event, stepSnapshot);
                    if (published != null && observer != null) {
                        observer.onOutputsRecorded(published);
                    }
                } catch (RuntimeException retainedForFinish) {
                    // record() keeps the failure; finish() writes the missing outputs with the report.
                }
            });
        } catch (RejectedExecutionException alreadyFinished) {
            // The session finished; the final report already contains this step.
        }
    }

    @Override
    public StepRunFinished recordStep(StepRunFinished event) {
        return record(event, snapshots.copy(event.getStepRunReport()));
    }

    private synchronized StepRunFinished record(StepRunFinished event, StepRunReport snapshot) {
        if (finished || recordingFailure != null) return null;
        try {
            RecordedStepKey key = new RecordedStepKey(event.getTestId(), event.getStepName(), snapshot.getDateTime());
            if (recorded.containsKey(key)) return null;
            // Include earlier producers for provenance without writing a provisional parent item.
            Test definition = definitions.stream()
                    .filter(test -> Objects.equals(test.getId(), event.getTestId()))
                    .findFirst()
                    .orElseThrow(() -> new TestReportRecordingException("Unknown test in recording event"));
            TestRunReport run = new TestRunReport();
            run.setTest(new net.ihe.gazelle.maestro.api.business.testreport.Test()
                    .setId(definition.getId())
                    .setName(definition.getName()));
            run.setInputs(inputSnapshot());
            // Stored step snapshots are only read by the plan, which copies their outputs.
            List<StepRunReport> steps = new ArrayList<>();
            recorded.forEach((previous, value) -> {
                if (Objects.equals(previous.testId(), event.getTestId())) steps.add(value.report());
            });
            steps.add(snapshot);
            run.setStepRunReports(steps);
            TestReport report = new TestReport();
            report.setTestRunReports(List.of(run));
            report.setAccessControlList(acl.get());
            ItemPlan<TestReport> plan = service.buildTestReportPlan(report, acl.get(), definitions, recorded);
            List<ReferencePlan> references = writer.recordReferences(plan, acl.get());
            Set<ReferencePlan> prior = new HashSet<>();
            recorded.values().forEach(value -> prior.addAll(value.references()));
            recorded.put(key, new RecordedStep(snapshot,
                    references.stream().filter(reference -> !prior.contains(reference)).toList()));
            return new StepRunFinished(event.getSessionId(), event.getTestId(), event.getStepName(), null,
                    snapshots.copy(snapshot));
        } catch (RuntimeException error) {
            recordingFailure = error;
            throw error;
        }
    }

    private List<Property> inputSnapshot() {
        if (inputSnapshot == null) {
            inputSnapshot = snapshots.copy(new ArrayList<>(inputs.get()));
        }
        return snapshots.copy(new ArrayList<>(inputSnapshot));
    }

    @Override
    public String finish(TestReport report) {
        awaitPendingSteps();
        return finishRecording(report);
    }

    private void awaitPendingSteps() {
        stepWriter.shutdown();
        try {
            // Pending writes are bounded by the Datahouse client timeouts.
            while (!stepWriter.awaitTermination(1, TimeUnit.MINUTES)) {
                // keep waiting: finishing early would write the report without the queued steps
            }
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            stepWriter.shutdownNow();
        }
    }

    private synchronized String finishRecording(TestReport report) {
        if (finished) throw new TestReportRecordingException("Recording session already finished");
        try {
            TestReport snapshot = snapshots.copy(report);
            if (recordingFailure != null) {
                // Steps stored before the failure are reused; the others are written with the report.
                snapshot.addUnexpectedError(new UnexpectedErrorBuilder()
                        .fromThrowable(new TestReportRecordingException(
                                "Incremental recording failed; remaining outputs were written with the final report",
                                recordingFailure))
                        .build());
            }
            ItemPlan<TestReport> plan = service.buildTestReportPlan(snapshot, snapshot.getAccessControlList(),
                    definitions, recorded);
            return datahouseUrl + "/items/" + writer.record(plan, snapshot.getAccessControlList());
        } finally {
            finished = true;
            recorded.clear();
            writer.close();
        }
    }
}
