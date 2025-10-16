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
import net.ihe.gazelle.maestro.api.business.message.StartStepRun;
import net.ihe.gazelle.maestro.api.business.message.StartTestRun;
import net.ihe.gazelle.maestro.api.business.message.StepRunFinished;
import net.ihe.gazelle.maestro.api.business.message.TestRunFinished;
import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.business.testreport.*;
import net.ihe.gazelle.maestro.api.business.testrun.*;
import net.ihe.gazelle.maestro.api.business.test.Test;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRun;
import net.ihe.gazelle.maestro.engine.business.context.Context;
import net.ihe.gazelle.maestro.engine.business.context.SessionStore;
import net.ihe.gazelle.maestro.engine.business.context.StepCursor;
import net.ihe.gazelle.maestro.engine.business.context.TestRunSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class TestRunner {

   private static final Logger LOG = LoggerFactory.getLogger(TestRunner.class);

   private final SessionStore sessionStore;
   private final TestRunEventProducer testRunEventProducer;

   public TestRunner(SessionStore sessionStore, TestRunEventProducer testRunEventProducer) {
      this.sessionStore = sessionStore;
      this.testRunEventProducer = testRunEventProducer;
   }


   public void execute(StartTestRun startTestRun) {
      TestRun testRun = startTestRun.getTestRun();
      if (testRun == null) {
         throw new InvalidTestRunException("TestRun is null");
      }

      try {
         StepCursor stepCursor = createSession(startTestRun.getTestSuiteRunId(), testRun);
         runStep(startTestRun.getTestSuiteRunId(), stepCursor.next(), testRun);
      } catch (Exception e) {
         Test test = testRun.getTest();
         LOG.atError().setCause(e).log("Error running test {}", test.getName());
         finishTest(startTestRun.getTestSuiteRunId(), testRun, newTestRunReportBuilder(test)
               .addUnexpectedError(new UnexpectedErrorBuilder().fromThrowable(e))
         );
      }
   }

   public void listen(StepRunFinished stepRunFinished) {
      TestRunSession testRunSession = sessionStore.getTestRunSession(stepRunFinished.getTestRunId());
      testRunSession.addStepRunReport(stepRunFinished.getStepRunReportBuilder());

      StepCursor stepCursor = testRunSession.getStepCursor();
      if (stepCursor.hasNext()) {
         runStep(testRunSession.getTestSuiteRunId(), stepCursor.next(), testRunSession.getTestRun());
      } else {
         finishTest(
               testRunSession.getTestRun(),
               newTestRunReportBuilder(testRunSession.getTestRun().getTest())
                     .addStepRunReports(testRunSession.getStepReports())
                     .setInputs(convertPropertiesToItems(testRunSession.getTestRun().getInputs()))
         );
      }
   }

   private void finishTest(String testSuiteRunId, TestRun testRun, final TestRunReportBuilder testRunReportBuilder) {
      sessionStore.removeTestRun(testRun.getInternalId());
      testRunEventProducer.notify(new TestRunFinished(testSuiteRunId, testRun, testRunReportBuilder));
   }

   private StepCursor createSession(String testSuiteRunId, TestRun testRun) {
      TestRunSession testRunSession = new TestRunSession(testSuiteRunId, testRun);
      sessionStore.addTestRunSession(testRun.getInternalId(), testRunSession);
      return testRunSession.getStepCursor();
   }

   private void runStep(String testSuiteRunId, final Step step, TestRun testRun) {
      StepRun stepRun = new StepRun(step);
      stepRun.step().setProperties(proxifyProperties(stepRun.step().getProperties(), getContextFromStore(testRun.getInternalId())));
      testRunEventProducer.command(new StartStepRun(testSuiteRunId, testRun.getInternalId(), testRun.getTest().getId(), stepRun));
   }


   private static TestRunReportBuilder newTestRunReportBuilder(Test test) {
      return new TestRunReportBuilder()
            .setDateTime(Date.from(Instant.now()))
            .setTest(new TestBuilder()
                  .setId(test.getId())
                  .setName(test.getName()));
   }

   /**
    * @param testRunID id of currently executed testRun
    *
    * @return Context from TestRunSession with corresponding id
    */
   private Context getContextFromStore(String testRunID) {
      return sessionStore.getTestRunSession(testRunID).getContext();
   }

   private List<Property<?>> proxifyProperties(List<Property<?>> properties, Context context) {
      return properties.stream()
            .map(p -> proxifyProperty(p, context))
            .collect(toList());
   }

   private Property<?> proxifyProperty(Property<?> property, Context context) {
      if (property instanceof PropertyRef<?> propertyRef) {
         return new ResolvablePropertyRef<>(propertyRef, context);
      } else {
         return property;
      }
   }


   private List<InputBuilder> convertPropertiesToItems(List<Property<?>> properties) {
      List<InputBuilder> items = new ArrayList<>();
      for (Property<?> property : properties) {
         if (property instanceof PropertyRef<?> propertyRef) {
            InputBuilder item = new InputBuilder()
                  .setName(property.getName())
                  .setContent(("${" + propertyRef.getReference() + "}").getBytes(StandardCharsets.UTF_8));
            items.add(item);
         } else if (property.getValue() instanceof byte[] content) {
            InputBuilder item = new InputBuilder()
                  .setName(property.getName())
                  .setContent(content)
                  .setType(Input.BYTE_ARRAY_TYPE);
            items.add(item);
         } else if (property.getValue() instanceof String value) {
            InputBuilder item = new InputBuilder()
                  .setName(property.getName())
                  .setContent(value.getBytes(StandardCharsets.UTF_8))
                  .setType(Input.TEXT_TYPE);
            items.add(item);
         } else if (property.getValue() instanceof Boolean value) {
            InputBuilder item = new InputBuilder()
                  .setName(property.getName())
                  .setContent(new byte[]{(byte) (Boolean.TRUE.equals(value) ? 1 : 0)})
                  .setType(Input.BOOLEAN_TYPE);
            items.add(item);
         } else if (property.getValue() instanceof Number value) {
            InputBuilder item = new InputBuilder()
                  .setName(property.getName())
                  .setContent(value.toString().getBytes(StandardCharsets.UTF_8))
                  .setType(Input.NUMBER_TYPE);
            items.add(item);
         }
      }
      return items;
   }
}
