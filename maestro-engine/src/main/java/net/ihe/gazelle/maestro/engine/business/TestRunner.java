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
import net.ihe.gazelle.maestro.api.business.InvalidTestRunException;
import net.ihe.gazelle.maestro.api.business.message.*;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.business.test.Test;
import net.ihe.gazelle.maestro.api.business.testreport.StepResult;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestBuilder;
import net.ihe.gazelle.maestro.api.business.testreport.TestRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestRunValidator;
import net.ihe.gazelle.maestro.engine.business.context.SessionStore;
import net.ihe.gazelle.maestro.engine.business.context.TestRunSession;
import net.ihe.gazelle.maestro.spi.business.StepRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.List;
import java.util.Map;

/**
 * TestRunner is responsible for executing {@link TestRun} instances step by step,
 * managing session state, and notifying observers of execution progress and results.
 */
public class TestRunner {

    private static final Logger LOG = LoggerFactory.getLogger(TestRunner.class);

    private final SessionStore<TestRunSession> sessionStore;
    private final TestRunEventProducer testRunEventProducer;
    private final Function<StepFailureDecisionRequested, CompletableFuture<Boolean>> failureDecision;
    private final TestRunValidator testRunValidator = new TestRunValidator();

    /**
     * Creates a new {@code TestRunner} with the specified session store and event producer.
     *
     * @param sessionStore         the store used to manage {@link TestRunSession} instances
     * @param testRunEventProducer the producer used to send test run events
     */
    public TestRunner(SessionStore<TestRunSession> sessionStore, TestRunEventProducer testRunEventProducer) {
        this(sessionStore, testRunEventProducer, request -> CompletableFuture.completedFuture(false));
    }

    public TestRunner(SessionStore<TestRunSession> sessionStore, TestRunEventProducer testRunEventProducer,
                      Function<StepFailureDecisionRequested, CompletableFuture<Boolean>> failureDecision) {
        this.failureDecision = failureDecision;
        this.sessionStore = sessionStore;
        this.testRunEventProducer = testRunEventProducer;
    }

    /**
     * Executes a {@link TestRun} as specified by a {@link StartTestRun} event.
     * Manages session state and handles exceptions during execution.
     *
     * @param startTestRun the event containing the test run to execute and session information
     */
    public void run(StartTestRun startTestRun) {
        String sessionId = startTestRun.getSessionId();
        TestRun testRun = startTestRun.getTestRun();
        try {
            testRunValidator.validate(testRun).orThrow(InvalidTestRunException::new);
            TestRunSession testRunSession = new TestRunSession(testRun)
                    .setTestSessionId(startTestRun.getTestSessionId())
                    .setPromptOnStepFailure(startTestRun.isPromptOnStepFailure())
                    .setExpirationTimestamp(startTestRun.getExpirationTimestamp());
            sessionStore.putSession(sessionId, testRunSession, startTestRun.getExpirationTimestamp());
            runNextStep(testRunSession, sessionId, testRun.getTest().getId());
        } catch (Exception e) {
            Test test = testRun.getTest();
            LOG.atError().setCause(e).log("Error running test {}", test.getName());
            sessionStore.removeSession(sessionId);
            testRunEventProducer.notify(
                    new TestRunFinished(sessionId, testRun,
                            newTestRunReportBuilder(test)
                                    .addUnexpectedError(new UnexpectedErrorBuilder().fromThrowable(e))
                                    .build()
                    ).setTestSessionId(startTestRun.getTestSessionId())
            );
        }
    }

    /**
     * Handles a {@link StepRunFinished} event by updating the session state,
     * executing the next step if available, or finishing the test run.
     *
     * @param stepRunFinished the finished step run event to process
     */
    public void listen(StepRunFinished stepRunFinished) {
        String sessionId = stepRunFinished.getSessionId();
        TestRunSession testRunSession = sessionStore.getSession(sessionId);
        if (testRunSession != null) {
            try {
                updateSessionState(stepRunFinished, testRunSession);
                if (!testRunSession.hasNextStep()) {
                    finishTest(sessionId, testRunSession);
                } else if (isPreviousStepSuccessful(stepRunFinished)) {
                    runNextStep(testRunSession, sessionId, stepRunFinished.getTestId());
                } else if (testRunSession.isPromptOnStepFailure()) {
                    awaitFailureDecision(stepRunFinished, testRunSession);
                } else {
                    finishTest(sessionId, testRunSession);
                }
            } catch (Exception e) {
                finishWithError(sessionId, testRunSession, e);
            }
        }
    }

    private void finishWithError(String sessionId, TestRunSession testRunSession, Exception e) {
        TestRun testRun = testRunSession.getTestRun();
        LOG.atError().setCause(e).log("Error running test {}", testRun.getTest().getName());
        testRunSession.closeEvidence();
        sessionStore.removeSession(sessionId);
        addEvidencesToReport(testRunSession);
        testRunEventProducer.notify(
                new TestRunFinished(sessionId, testRun,
                        newTestRunReportBuilder(testRun.getTest())
                                .addStepRunReports(testRunSession.getStepReports())
                                .setInputs(testRun.getInputs())
                                .addUnexpectedError(new UnexpectedErrorBuilder().fromThrowable(e))
                                .build()
                ).setTestSessionId(testRunSession.getTestSessionId())
        );
    }

    private void awaitFailureDecision(StepRunFinished finished, TestRunSession session) {
        String sessionId = finished.getSessionId();
        StepFailureDecisionRequested request = new StepFailureDecisionRequested(sessionId, session.currentStepIndex(),
                finished.getStepName(), finished.getStepRunReport().getResult());
        long remaining = Duration.between(Instant.now(), session.getExpirationTimestamp()).toMillis();
        if (remaining <= 0) {
            finishTest(sessionId, session);
            return;
        }
        sessionStore.putSession(sessionId, session, session.getExpirationTimestamp());
        failureDecision.apply(request).completeOnTimeout(false, remaining, TimeUnit.MILLISECONDS)
                .whenCompleteAsync((proceed, error) -> {
                    // The answer arrives on a WebSocket event loop. Session and report persistence may block.
                    TestRunSession current = sessionStore.getSession(sessionId);
                    if (current == null) {
                        // The cache may expire first. Complete the report without re-adding an expired run.
                        if (!Instant.now().isBefore(session.getExpirationTimestamp())) {
                            finishTest(sessionId, session);
                        }
                        return;
                    }
                    if (current.currentStepIndex() != request.stepIndex()) return;
                    try {
                        if (error == null && Boolean.TRUE.equals(proceed)
                                && Instant.now().isBefore(current.getExpirationTimestamp())) {
                            runNextStep(current, sessionId, finished.getTestId());
                        } else {
                            finishTest(sessionId, current);
                        }
                    } catch (RuntimeException exception) {
                        finishWithError(sessionId, current, exception);
                    }
                }, Thread::startVirtualThread).exceptionally(error -> {
                    LOG.atError().setCause(error).log("Unable to resolve step {} in test {}", request.stepIndex(), finished.getTestId());
                    return null;
                });
    }

    private void finishTest(String sessionId, TestRunSession testRunSession) {
        testRunSession.closeEvidence();
        sessionStore.removeSession(sessionId);
        addEvidencesToReport(testRunSession);

        // VLD: keep only resolvable inputs in the report (fix problem of deserialization of references)
        List<Property> inputs = testRunSession.getTestRun().getInputs().stream()
                .filter(property -> !property.isReference())
                .toList();

        testRunEventProducer.notify(
                new TestRunFinished(sessionId, testRunSession.getTestRun(),
                        newTestRunReportBuilder(testRunSession.getTestRun().getTest())
                                .addStepRunReports(testRunSession.getStepReports())
                                .setInputs(inputs)
                                .build()
                ).setTestSessionId(testRunSession.getTestSessionId())
        );
    }

    private void runNextStep(TestRunSession testRunSession, String sessionId, String testId) {
        Step nextStep = testRunSession.nextStep();
        sessionStore.putSession(sessionId, testRunSession, testRunSession.getExpirationTimestamp());
        testRunEventProducer.command(new StartStepRun(
                sessionId,
                testId,
                testRunSession.currentStepIndex(),
                new StepRun(nextStep, testRunSession.getContext())
        ).setExpirationTimestamp(testRunSession.getExpirationTimestamp()));
    }

    private void updateSessionState(StepRunFinished stepRunFinished, TestRunSession testRunSession) {
        StepRunReport stepRunReport = stepRunFinished.getStepRunReport();
        testRunSession.addStepRunReport(stepRunReport);
        testRunSession.addPropertiesToContext(
                mapOutputs(testRunSession.currentStep(), stepRunReport,
                        testRunSession.isPromptOnStepFailure() && !isPreviousStepSuccessful(stepRunFinished))
        );
    }

    private void addEvidencesToReport(TestRunSession testRunSession) {
        for (PutEvidence putEvidence : testRunSession.getEvidences()) {
            for (StepRunReport stepRunReport : testRunSession.getStepReports()) {
                if (putEvidence.getStepName().equals(stepRunReport.getStepName())) {
                    List<Property> evidence = stepRunReport.getEvidences();
                    evidence.removeIf(existing ->
                            existing.getName().equals(putEvidence.getContent().getName()));
                    evidence.add(putEvidence.getContent());
                    stepRunReport.setEvidences(evidence);
                }
            }
        }
    }

    private boolean isPreviousStepSuccessful(StepRunFinished stepRunFinished) {
        return StepResult.PASSED.equals(stepRunFinished.getStepRunReport().getResult())
                || StepResult.DONE.equals(stepRunFinished.getStepRunReport().getResult());
    }

    private static List<Property> mapOutputs(Step step, StepRunReport stepRunReport, boolean allowMissing) {
        return step.getOutputMappings().entrySet().stream()
                .filter(entry -> !allowMissing || stepRunReport.getOutputs().stream()
                        .anyMatch(output -> output.getName().equals(entry.getKey())))
                .map(entry -> mapOutput(entry, stepRunReport.getOutputs()))
                .toList();
    }

    private static Property mapOutput(Map.Entry<String, String> outputMapping, List<Property> outputs) {
        return outputs.stream()
                .filter(output -> output.getName().equals(outputMapping.getKey()))
                .findFirst()
                .orElseThrow(
                        () -> new InvalidOutputMappingException("There is no output named " + outputMapping.getKey()))
                .copy()
                .setName(outputMapping.getValue());
    }

    private static TestRunReportBuilder newTestRunReportBuilder(Test test) {
        return new TestRunReportBuilder()
                .setTest(new TestBuilder()
                        .setId(test.getId())
                        .setName(test.getName()));
    }

    /**
     * Exception thrown when an output mapping of a {@link Step} or {@link StepRun} is invalid.
     */
    public static class InvalidOutputMappingException extends RuntimeException {

        @Serial
        private static final long serialVersionUID = 1727233135259169863L;

        /**
         * Creates a new {@code InvalidOutputMappingException} with the specified message.
         *
         * @param message the detail message describing the invalid output mapping
         */
        public InvalidOutputMappingException(String message) {
            super(message);
        }
    }
}
