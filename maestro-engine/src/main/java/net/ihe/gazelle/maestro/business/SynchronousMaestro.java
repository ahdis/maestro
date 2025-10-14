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

package net.ihe.gazelle.maestro.business;

import net.ihe.gazelle.lang.ExecutionRuntimeException;
import net.ihe.gazelle.maestro.api.business.testreport.TestReportSummary;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Class used to do request to Maestro synchronously.
 */
public class SynchronousMaestro {

   public static final String TIMEOUT_MESSAGE = "The test execution timed out. Either the test was too long for a " +
                                                "synchronous call, either something fatal happened and you may need " +
                                                "to contact an administrator";

   private final int timeoutSeconds;
   private final MaestroFacade maestroFacade;

   /**
    * Constructor
    *
    * @param timeoutSeconds the timeout in seconds for the synchronous call. Must not be null and greater than 0.
    * @param maestroFacade  the Maestro Facade. Must not be null.
    */
   public SynchronousMaestro(int timeoutSeconds, MaestroFacade maestroFacade) {
      this.timeoutSeconds = timeoutSeconds;
      this.maestroFacade = maestroFacade;
   }

   /**
    * Execute a Test Suite. This method will block the current thread until the test execution is finished or timeout
    * occurs.
    *
    * @param testSuiteRun the test suite to execute
    *
    * @return A test report summary.
    *
    * @throws IllegalArgumentException  if testSuiteRun is invalid
    * @throws TimeoutException          if the timeout expires before the test execution is finished.
    * @throws InterruptedException      if the current thread is interrupted while waiting.
    * @throws ExecutionRuntimeException if an unexpected error occurs during the execution.
    */
   public TestReportSummary executeTestSuite(TestSuiteRun testSuiteRun) throws TimeoutException, InterruptedException {

      CompletableFuture<TestReportSummary> future = new CompletableFuture<>();
      maestroFacade.executeTestSuite(
            testSuiteRun,
            testSuiteRunFinished -> future.complete(testSuiteRunFinished.reportSummary())
      );

      try {
         return future.get(timeoutSeconds, TimeUnit.SECONDS);
      } catch (ExecutionException e) {
         throw new ExecutionRuntimeException(e);
      }
   }

}
