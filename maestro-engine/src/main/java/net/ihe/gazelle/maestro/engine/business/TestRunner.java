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

package net.ihe.gazelle.maestro.engine.business;

import net.ihe.gazelle.errorhandling.business.UnexpectedErrorBuilder;
import net.ihe.gazelle.maestro.api.business.InvalidTestRunException;
import net.ihe.gazelle.maestro.api.business.message.StartStepRun;
import net.ihe.gazelle.maestro.api.business.message.StartTestRun;
import net.ihe.gazelle.maestro.api.business.message.StepRunFinished;
import net.ihe.gazelle.maestro.api.business.message.TestRunFinished;
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
import java.util.List;
import java.util.Map;

public class TestRunner {

   private static final Logger LOG = LoggerFactory.getLogger(TestRunner.class);

   private final SessionStore<TestRunSession> sessionStore;
   private final TestRunEventProducer testRunEventProducer;
   private final TestRunValidator testRunValidator = new TestRunValidator();

   public TestRunner(SessionStore<TestRunSession> sessionStore, TestRunEventProducer testRunEventProducer) {
      this.sessionStore = sessionStore;
      this.testRunEventProducer = testRunEventProducer;
   }

   public void run(StartTestRun startTestRun) {
      String sessionId = startTestRun.getSessionId();
      TestRun testRun = startTestRun.getTestRun();
      try {
         testRunValidator.validate(testRun).orThrow(InvalidTestRunException::new);

         TestRunSession testRunSession = new TestRunSession(testRun);
          long timeout = testRun.getTest().computeTestRunTimeout();
         sessionStore.addSession(sessionId, testRunSession, timeout);
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
               )
         );
      }
   }

   public void listen(StepRunFinished stepRunFinished) {
      String sessionId = stepRunFinished.getSessionId();
      TestRunSession testRunSession = sessionStore.getSession(sessionId);
      if (testRunSession != null) {
         try {
            updateSessionState(stepRunFinished, testRunSession);
            if (isPreviousStepSuccessful(stepRunFinished) && testRunSession.hasNextStep()) {
               runNextStep(testRunSession, sessionId, stepRunFinished.getTestId());
            } else {
               finishTest(sessionId, testRunSession);
            }
         } catch (Exception e) {
            TestRun testRun = testRunSession.getTestRun();
            LOG.atError().setCause(e).log("Error running test {}", testRun.getTest().getName());
            sessionStore.removeSession(sessionId);
            testRunEventProducer.notify(
                  new TestRunFinished(sessionId, testRun,
                        newTestRunReportBuilder(testRun.getTest())
                              .addStepRunReports(testRunSession.getStepReports())
                              .setInputs(testRun.getInputs())
                              .addUnexpectedError(new UnexpectedErrorBuilder().fromThrowable(e))
                              .build()
                  )
            );
         }
      }
   }

   private void finishTest(String sessionId, TestRunSession testRunSession) {
      sessionStore.removeSession(sessionId);
      testRunEventProducer.notify(
            new TestRunFinished(sessionId, testRunSession.getTestRun(),
                  newTestRunReportBuilder(testRunSession.getTestRun().getTest())
                        .addStepRunReports(testRunSession.getStepReports())
                        .setInputs(testRunSession.getTestRun().getInputs())
                        .build()
            )
      );
   }

   private void runNextStep(TestRunSession testRunSession, String sessionId, String testId) {
      Step nextStep = testRunSession.nextStep();
      testRunEventProducer.command(new StartStepRun(
            sessionId,
            testId,
            testRunSession.currentStepIndex(),
            new StepRun(nextStep, testRunSession.getContext())
      ));
   }

   private static void updateSessionState(StepRunFinished stepRunFinished, TestRunSession testRunSession) {
      StepRunReport stepRunReport = stepRunFinished.getStepRunReport();
      testRunSession.addStepRunReport(stepRunReport);
      testRunSession.addPropertiesToContext(
            mapOutputs(testRunSession.currentStep(), stepRunReport)
      );
   }

   private boolean isPreviousStepSuccessful(StepRunFinished stepRunFinished) {
      return StepResult.PASSED.equals(stepRunFinished.getStepRunReport().getResult()) ||
             StepResult.DONE.equals(stepRunFinished.getStepRunReport().getResult());
   }

   private static List<Property> mapOutputs(Step step, StepRunReport stepRunReport) {
      return step.getOutputMappings().entrySet().stream()
            .map(entry -> mapOutput(entry, stepRunReport.getOutputs()))
            .toList();
   }

   private static Property mapOutput(Map.Entry<String, String> outputMapping, List<Property> outputs) {
      return outputs.stream().filter(output -> output.getName().equals(outputMapping.getKey()))
            .findFirst()
            .orElseThrow(() -> new InvalidOutputMappingException("There is no output named " + outputMapping.getKey()))
            .copy()
            .setName(outputMapping.getValue());
   }

   private static TestRunReportBuilder newTestRunReportBuilder(Test test) {
      return new TestRunReportBuilder()
            .setTest(new TestBuilder()
                  .setId(test.getId())
                  .setName(test.getName()));
   }

   public static class InvalidOutputMappingException extends RuntimeException {
      @Serial
      private static final long serialVersionUID = 1727233135259169863L;

      public InvalidOutputMappingException(String message) {
         super(message);
      }
   }
}
