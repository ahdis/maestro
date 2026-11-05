/*
 * Copyright 2025-2026 IHE International.
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

package net.ihe.gazelle.maestro.engine.business;

import net.ihe.gazelle.errorhandling.business.UnexpectedErrorBuilder;
import net.ihe.gazelle.maestro.api.business.Maestro;
import net.ihe.gazelle.maestro.api.business.MaestroObserver;
import net.ihe.gazelle.maestro.api.business.message.AskGrading;
import net.ihe.gazelle.maestro.api.business.message.ExecutionFinished;
import net.ihe.gazelle.maestro.api.business.message.GradingCompleted;
import net.ihe.gazelle.maestro.api.business.message.InteractWithUser;
import net.ihe.gazelle.maestro.api.business.message.StepRunFinished;
import net.ihe.gazelle.maestro.api.business.message.StepRunStarted;
import net.ihe.gazelle.maestro.api.business.message.TestRunFinished;
import net.ihe.gazelle.maestro.api.business.message.TestRunStarted;
import net.ihe.gazelle.maestro.api.business.message.UserInteractionCompleted;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.test.Test;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;
import net.ihe.gazelle.security.business.acl.AccessControlList;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Maestro implementation that records test execution reports when {@code persist} is enabled.
 * Delegates execution to a {@link MaestroFacade} and wraps observers for recording.
 */
public class RecordingMaestro implements Maestro {

    private final MaestroFacade maestro;
    private final TestReportRecordingService testReportRecordingService;

    /**
     * Creates a new {@code RecordingMaestro} instance with the specified facade and recording service.
     *
     * @param maestro                    the {@link MaestroFacade} used for executing tests
     * @param testReportRecordingService the service used to record test reports
     */
    public RecordingMaestro(
            MaestroFacade maestro, TestReportRecordingService testReportRecordingService) {
        this.maestro = maestro;
        this.testReportRecordingService = testReportRecordingService;
    }

    @Override
    public void executeTestSuite(
            String testSessionId,
            TestSuiteRun testSuiteRun,
            boolean persist,
            MaestroObserver observer) {
        if (persist) {
            observer =
                    new RecordObserver(
                            observer,
                            testSuiteRun.getTests(),
                            testSuiteRun::getAccessControlList,
                            null);
        }
        maestro.executeTestSuite(testSessionId, testSuiteRun, observer);
    }

    @Override
    public void executeTest(
            String testSessionId, TestRun testRun, boolean persist, MaestroObserver observer) {
        if (persist) {
            observer =
                    new RecordObserver(
                            observer,
                            testRun.getTest() == null ? List.of() : List.of(testRun.getTest()),
                            testRun::getAccessControlList,
                            testRun::getInputs);
        }
        maestro.executeTest(testSessionId, testRun, observer);
    }

    private class RecordObserver implements MaestroObserver {

        private final MaestroObserver delegate;

        private final ExecutionRecordingSession recording;

        public RecordObserver(
                MaestroObserver delegate,
                List<Test> definitions,
                Supplier<AccessControlList> acl,
                Supplier<List<Property>> inputs) {
            this.delegate = delegate;
            this.recording =
                    inputs == null
                            ? testReportRecordingService.openSession(definitions, acl)
                            : testReportRecordingService.openSession(definitions, acl, inputs);
        }

        @Override
        public void onTestRunStarted(TestRunStarted testRunStarted) {
            delegate.onTestRunStarted(testRunStarted);
        }

        @Override
        public void onStepRunStarted(StepRunStarted stepRunStarted) {
            delegate.onStepRunStarted(stepRunStarted);
        }

        @Override
        public CompletableFuture<UserInteractionCompleted> interactWithUser(
                InteractWithUser interactWithUser) {
            return delegate.interactWithUser(interactWithUser);
        }

        @Override
        public void onStepRunFinished(StepRunFinished stepRunFinished) {
            delegate.onStepRunFinished(stepRunFinished);
            recording.submitStep(
                    stepRunFinished,
                    delegate instanceof RecordedOutputsObserver outputsObserver
                            ? outputsObserver
                            : null);
        }

        @Override
        public void onTestRunFinished(TestRunFinished testRunFinished) {
            delegate.onTestRunFinished(testRunFinished);
        }

        @Override
        public void onExecutionFinished(ExecutionFinished executionFinished) {
            try {
                ExecutionRecordingResult recorded = recording.finish(executionFinished.getReport());
                executionFinished
                        .setReport(recorded.report())
                        .setReportLocation(recorded.location());
            } catch (Exception e) {
                executionFinished
                        .getReport()
                        .addUnexpectedError(new UnexpectedErrorBuilder().fromThrowable(e).build());
            }
            delegate.onExecutionFinished(executionFinished);
        }

        @Override
        public CompletableFuture<GradingCompleted> askGrading(AskGrading askGrading) {
            return delegate.askGrading(askGrading);
        }
    }
}
