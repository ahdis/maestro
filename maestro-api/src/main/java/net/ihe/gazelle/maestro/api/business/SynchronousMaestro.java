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

package net.ihe.gazelle.maestro.api.business;

import net.ihe.gazelle.lang.ExecutionRuntimeException;
import net.ihe.gazelle.maestro.api.business.message.*;
import net.ihe.gazelle.maestro.api.business.testreport.LocalizedTestReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.api.business.testrun.TestRunValidator;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRunValidator;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Class used to do request to Maestro synchronously.
 */
public class SynchronousMaestro {

    /**
     * The message to return in case of execution timeout
     */
    public static final String TIMEOUT_MESSAGE = "The test execution timed out. Either the test was too long for a " +
            "synchronous call, either something fatal happened and you may need " +
            "to contact an administrator";

    private final Maestro maestro;
    private final TestSuiteRunValidator testSuiteRunValidator = new TestSuiteRunValidator();
    private final TestRunValidator testRunValidator = new TestRunValidator();

    /**
     * Constructor
     *
     * @param maestro the Maestro Facade. Must not be null.
     */
    public SynchronousMaestro(Maestro maestro) {
        this.maestro = maestro;
    }

    /**
     * Execute a Test Suite. This method will block the current thread until the test execution is finished or timeout
     * occurs.
     *
     * @param startTestSuiteRun   the test suite to execute
     * @return A test report summary.
     * @throws IllegalArgumentException  if testSuiteRun is invalid
     * @throws TimeoutException          if the timeout expires before the test execution is finished.
     * @throws InterruptedException      if the current thread is interrupted while waiting.
     * @throws ExecutionRuntimeException if an unexpected error occurs during the execution.
     */
    public TestReport executeTestSuite(StartTestSuiteRun startTestSuiteRun) throws TimeoutException, InterruptedException {
        testSuiteRunValidator.assertValid(startTestSuiteRun.getTestSuiteRun());
        CompletableFuture<LocalizedTestReport> future = new CompletableFuture<>();
        maestro.executeTestSuite(
                startTestSuiteRun,
                new SynchronousObserver(future)
        );

        try {
            Duration remaining = Duration.between(Instant.now(), startTestSuiteRun.getExpirationTimestamp());
            return future.get(remaining.toMillis(), TimeUnit.MILLISECONDS);
        } catch (ExecutionException e) {
            throw new ExecutionRuntimeException(e);
        }
    }

    /**
     * Execute a single Test. This method will block the current thread until the test execution is finished or timeout
     * occurs.
     *
     * @param startTestRun the test to execute
     * @return A test report summary.
     * @throws IllegalArgumentException  if testRun is invalid
     * @throws TimeoutException          if the timeout expires before the test execution is finished.
     * @throws InterruptedException      if the current thread is interrupted while waiting.
     * @throws ExecutionRuntimeException if an unexpected error occurs during the execution.
     */
    public TestReport executeTest(StartTestRun startTestRun) throws TimeoutException, InterruptedException {
        testRunValidator.assertValid(startTestRun.getTestRun());
        CompletableFuture<LocalizedTestReport> future = new CompletableFuture<>();
        maestro.executeTest(
                startTestRun,
                new SynchronousObserver(future)
        );

        try {
            Duration remaining = Duration.between(Instant.now(), startTestRun.getExpirationTimestamp());
            return future.get(remaining.toMillis(), TimeUnit.MILLISECONDS);
        } catch (ExecutionException e) {
            throw new ExecutionRuntimeException(e);
        }
    }

    private static class SynchronousObserver implements MaestroObserver {

        private final CompletableFuture<LocalizedTestReport> future;

        public SynchronousObserver(CompletableFuture<LocalizedTestReport> future) {
            this.future = future;
        }

        @Override
        public CompletableFuture<UserInteractionCompleted> interactWithUser(InteractWithUser interactWithUser) {
            throw new UnsupportedOperationException("User interaction is not supported via REST API.");
        }

        @Override
        public void onExecutionFinished(ExecutionFinished executionFinished) {
            LocalizedTestReport localizedTestReport = new LocalizedTestReport(executionFinished.getReport(), executionFinished.getReportLocation());
            future.complete(localizedTestReport);
        }

        @Override
        public CompletableFuture<GradingCompleted> askGrading(AskGrading askGrading) {
            throw new UnsupportedOperationException("Manual grading is not supported via REST API.");
        }
    }

}
