/*
 * Copyright 2025 Kereval.
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

package net.ihe.gazelle.maestro.quarkus.jobs;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import net.ihe.gazelle.lang.ExecutionRuntimeException;
import net.ihe.gazelle.maestro.api.business.testreport.TestReportSummary;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;
import net.ihe.gazelle.maestro.api.technical.event.StepRunFinished;
import net.ihe.gazelle.maestro.api.technical.event.StepRunStarted;
import net.ihe.gazelle.maestro.api.technical.event.TestRunFinished;
import net.ihe.gazelle.maestro.api.technical.event.TestRunStarted;
import net.ihe.gazelle.maestro.api.technical.event.TestSuiteFinished;
import net.ihe.gazelle.maestro.quarkus.client.MaestroCaller;
import net.ihe.gazelle.maestro.quarkus.ws.async.AsynchronousMaestro;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;

/**
 * Class used to do request to Maestro synchronously. When Maestro is called to execute a list of TestRun, we create a
 * Completable future, and when execution is done, completable future is completed with TestReport. There is a Timeout
 * on completable future, if time is up, completableFuture is completed with a TestReport with undefined result and an
 * unexpectedError TimeoutException.
 */
@ApplicationScoped
@Default
public class SynchronousMaestro implements MaestroCaller {

   public static final String TIMEOUT_MESSAGE = "The test execution timed out. Either the test was too long for a " +
                                                "synchronous call, either something fatal happened and you may need " +
                                                "to contact an administrator";

   private static final Map<String, CompletableFuture<TestReportSummary>> PENDING_EXECUTIONS = new ConcurrentHashMap<>();

   private final int timeout;
   private final AsynchronousMaestro asynchronousMaestro;


   @Inject
   public SynchronousMaestro(
         @ConfigProperty(name = "gzl.maestro.timeout", defaultValue = "60") int timeout,
         AsynchronousMaestro asynchronousMaestro) {
      this.timeout = timeout;
      this.asynchronousMaestro = asynchronousMaestro;
   }

   /**
    * Execute a Test Suite. This method will block the current thread until the test execution is finished or timeout
    * occurs.
    *
    * @param testSuiteRun the test suite to execute
    *
    * @return A test report summary.
    *
    * @throws TimeoutException         if the timeout expires before the test execution is finished.
    * @throws InterruptedException     if the current thread is interrupted while waiting.
    * @throws IllegalArgumentException if testSuiteRun is invalid
    */
   public TestReportSummary executeTestSuite(TestSuiteRun testSuiteRun) throws TimeoutException, InterruptedException {

      if (testSuiteRun == null || testSuiteRun.getInternalId() == null) {
         throw new IllegalArgumentException("TestSuiteRun or its TestSetId cannot be null");
      }

      CompletableFuture<TestReportSummary> future = new CompletableFuture<>();
      PENDING_EXECUTIONS.put(testSuiteRun.getInternalId(), future);
      asynchronousMaestro.executeTestSuite(testSuiteRun, this);

      try {
         return future.get(timeout, TimeUnit.SECONDS);
      } catch (ExecutionException e) {
         throw new ExecutionRuntimeException(e);
      } finally {
         PENDING_EXECUTIONS.remove(testSuiteRun.getInternalId());
      }
   }

   @Override
   public void onTestRunStarted(TestRunStarted testRunStarted) {
      // Nothing to do
   }

   @Override
   public void onStepRunStarted(StepRunStarted stepRunStarted) {
      // Nothing to do
   }

   @Override
   public void onStepRunFinished(StepRunFinished stepRunFinished) {
      // Nothing to do
   }

   @Override
   public void onTestRunFinished(TestRunFinished testRunFinished) {
      // Nothing to do
   }

   @Override
   public void onTestSuiteFinished(TestSuiteFinished testSuiteFinished) {
      Optional.ofNullable(PENDING_EXECUTIONS.get(testSuiteFinished.testSuiteRun().getInternalId()))
            .ifPresent(future -> future.complete(testSuiteFinished.reportSummary()));
   }

}
