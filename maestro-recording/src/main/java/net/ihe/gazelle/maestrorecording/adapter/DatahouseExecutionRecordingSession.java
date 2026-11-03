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

import java.util.*;
import java.util.function.Supplier;
import net.ihe.gazelle.maestro.api.business.message.StepRunFinished;
import net.ihe.gazelle.maestro.api.business.test.Test;
import net.ihe.gazelle.maestro.api.business.testreport.*;
import net.ihe.gazelle.maestro.engine.business.ExecutionRecordingSession;
import net.ihe.gazelle.maestro.spi.business.recording.plan.*;
import net.ihe.gazelle.maestrorecording.RecordItemService;
import net.ihe.gazelle.security.business.acl.AccessControlList;

/** Serializes recording only; engine event processing remains independent. */
final class DatahouseExecutionRecordingSession implements ExecutionRecordingSession {
    private final RecordingSnapshotFactory snapshots = new RecordingSnapshotFactory();
    private final DatahouseTestReportRecordingService service;
    private final net.ihe.gazelle.maestrorecording.RecordingWriter writer;
    private final List<Test> definitions;
    private final Supplier<AccessControlList> acl;
    private final Supplier<List<net.ihe.gazelle.maestro.api.business.property.Property>> inputs;
    private final String datahouseUrl;
    private final Map<RecordedStepKey, RecordedStep> recorded = new HashMap<>();
    private boolean finished;
    private RuntimeException recordingFailure;

    DatahouseExecutionRecordingSession(DatahouseTestReportRecordingService service,
            net.ihe.gazelle.maestrorecording.RecordingWriter writer, List<Test> definitions,
            Supplier<AccessControlList> acl, Supplier<List<net.ihe.gazelle.maestro.api.business.property.Property>> inputs, String datahouseUrl) {
        this.service = service;
        this.writer = writer;
        this.definitions = List.copyOf(definitions);
        this.acl = acl;
        this.inputs = inputs;
        this.datahouseUrl = datahouseUrl;
    }

    @Override
    public synchronized StepRunFinished recordStep(StepRunFinished event) {
        if (finished || recordingFailure != null) return null;
        try {
            RecordedStepKey key = new RecordedStepKey(event.getTestId(), event.getStepName(), event.getStepRunReport().getDateTime());
            if (recorded.containsKey(key)) return null;
            // Include earlier producers for provenance without writing a provisional parent item.
            TestReport report = new TestReport();
            TestRunReport run = new TestRunReport();
            Test definition = definitions.stream().filter(test -> Objects.equals(test.getId(), event.getTestId()))
                    .findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown test in recording event"));
            run.setTest(new net.ihe.gazelle.maestro.api.business.testreport.Test().setId(definition.getId()).setName(definition.getName()));
            run.setInputs(snapshots.copy(new ArrayList<>(inputs.get())));
            List<StepRunReport> steps = new ArrayList<>();
            recorded.forEach((previous, value) -> {
                if (Objects.equals(previous.testId(), event.getTestId())) steps.add(snapshots.copy(value.report()));
            });
            StepRunReport snapshot = snapshots.copy(event.getStepRunReport());
            steps.add(snapshot);
            run.setStepRunReports(steps);
            report.setTestRunReports(List.of(run));
            report.setAccessControlList(acl.get());
            ItemPlan<TestReport> plan = service.buildTestReportPlan(report, acl.get(), definitions, recorded);
            List<ReferencePlan> references = writer.recordReferences(plan, acl.get());
            Set<ReferencePlan> prior = new HashSet<>();
            recorded.values().forEach(value -> prior.addAll(value.references()));
            recorded.put(key, new RecordedStep(snapshot, references.stream().filter(ref -> !prior.contains(ref)).toList()));
            return new StepRunFinished(event.getSessionId(), event.getTestId(), event.getStepName(), null,
                    snapshots.copy(snapshot));
        } catch (RuntimeException error) {
            recordingFailure = error;
            throw error;
        }
    }

    @Override
    public synchronized String finish(TestReport report) {
        if (finished) throw new IllegalStateException("Recording session already finished");
        try {
            if (recordingFailure != null) {
                throw new net.ihe.gazelle.maestro.engine.business.TestReportRecordingException(
                        "Incremental recording failed; no automatic retry of potentially committed writes", recordingFailure);
            }
            TestReport snapshot = snapshots.copy(report);
            ItemPlan<TestReport> plan = service.buildTestReportPlan(snapshot, snapshot.getAccessControlList(), definitions, recorded);
            return datahouseUrl + "/items/" + writer.record(plan, snapshot.getAccessControlList());
        } finally {
            finished = true;
            recorded.clear();
            writer.close();
        }
    }
}
