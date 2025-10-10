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

import net.ihe.gazelle.errorhandling.business.UnexpectedErrorBuilder;
import net.ihe.gazelle.maestro.api.business.testreport.*;
import net.ihe.gazelle.maestro.api.business.testrun.*;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRun;
import net.ihe.gazelle.security.business.acl.AccessControlListBuilder;
import net.ihe.gazelle.maestro.business.TestReportRecordService.ItemReference;
import net.ihe.gazelle.maestro.business.context.Context;
import net.ihe.gazelle.maestro.business.context.SessionStore;
import net.ihe.gazelle.maestro.business.context.TestRunSession;
import net.ihe.gazelle.maestro.business.context.TestSuiteSession;
import org.slf4j.Logger;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MaestroImpl implements Maestro {

   private final Logger logger = org.slf4j.LoggerFactory.getLogger(MaestroImpl.class);

   private static final String GAZELLE_ENGINE = "Gazelle engine";

   private final MaestroEventProducer maestroEventProducer;
   private final SessionStore sessionStore;
   private final TestReportRecordService testReportRecordService;
   private final String maestroVersion;

   public MaestroImpl(MaestroEventProducer maestroEventProducer,
                      SessionStore sessionStore,
                      TestReportRecordService testReportRecordService, String maestroVersion) {
      this.maestroEventProducer = maestroEventProducer;
      this.sessionStore = sessionStore;
      this.testReportRecordService = testReportRecordService;
      this.maestroVersion = maestroVersion;
   }

   @Override
   public void run(TestRun testRun) {
      if (testRun == null) {
         throw new InvalidTestRunException("TestRun is null");
      }

      try {
         // create context
         Context context = extractInputs(testRun.getInputs());

         String testRunId = UUID.randomUUID().toString();
         String testSetId = testRun.getTestSetId();

         putInStore(testRunId, testRun, context);

         StepCursor stepCursor = sessionStore.getTestRun(testRunId).getStepCursor();
         Step step = stepCursor.next();

         try {
            // iterate steps starting with the first; on error, record step error and continue
            runStep(new StepRun(testSetId, testRunId, step));
         } catch (Exception e) {
            logger.error("Error running step {}", step.getName(), e);
            StepRun stepRun = new StepRun(testSetId, testRunId, step);
            StepRunReportBuilder builder = new StepRunReportBuilder()
                  .setStepId(step.getName())
                  .setType(step.getType())
                  .setResult(StepResult.UNDEFINED)
                  .addUnexpectedError(getUnexpectedErrorBuilder(e));
            finishStep(stepRun, builder);
         }
      } catch (Exception e) {
         logger.error("Error running test {}", (testRun.getTest() != null ? testRun.getTest().getName() : ""), e);
         TestRunReportBuilder testRunReportBuilder = new TestRunReportBuilder()
               .setDateTime(Date.from(Instant.now()))
               .setTest(new TestBuilder()
                     .setId(testRun.getTest() != null ? testRun.getTest().getId() : "")
                     .setName(testRun.getTest() != null ? testRun.getTest().getName() : ""))
               .addUnexpectedError(getUnexpectedErrorBuilder(e));
         finishTest(testRun, testRunReportBuilder);
      }
   }

   @Override
   public void run(TestSuiteRun testSuiteRun) {

      if (testSuiteRun == null) {
         throw new InvalidTestSetException("TestSuiteRun is null");
      }

      try {
         String testSuiteId = testSuiteRun.getInternalId();
         TestSuiteSession testSuiteSession = new TestSuiteSession(testSuiteId, testSuiteRun);
         sessionStore.addTestSuite(testSuiteId, testSuiteSession);
         TestRunCursor testRunCursor = testSuiteSession.getTestRunCursor();
         if (testRunCursor == null || !testRunCursor.hasNext()) {
            logger.error("Test set {} does not contain test runs to execute", testSuiteId);
            return;
         }
         TestRun testRun = testRunCursor.next();
         run(testRun);
      } catch (Exception e) {
         logger.error("Error running test set {}", testSuiteRun.getInternalId(), e);
         TestReportBuilder testReportBuilder = buildGenericTestReport(testSuiteRun)
               .setDateTime(Date.from(Instant.now()))
               .addUnexpectedError(getUnexpectedErrorBuilder(e));
         TestReport testReport = testReportBuilder.build();
         String sessionId = testSuiteRun.getInternalId();
         if (sessionId != null) {
            sessionStore.removeTestRun(sessionId);
            sessionStore.removeTestSuite(sessionId);
         }
         recordAndReturnTestReport(testReport, testSuiteRun);
      }

   }

   @Override
   public void finishTest(TestRun testRun, TestRunReportBuilder testRunReportBuilder) {
      TestSuiteSession testSuiteSession = sessionStore.getTestSuite(testRun.getTestSetId());
      if (testSuiteSession == null) {
         // Standalone test run execution (not part of a test set) - emit error directly
         maestroEventProducer.returnTestRunReport(testRun, testRunReportBuilder);
         return;
      }
      testSuiteSession.addTestRunReportBuilder(testRunReportBuilder);
      TestSuiteRun testSuiteRun = testSuiteSession.getTestSuiteRun();
      TestRunCursor testRunCursor = testSuiteSession.getTestRunCursor();
      if (testRunCursor == null) {
         throw new InvalidTestSetException("FATAL: No cursor available for test set " + testRun.getTestSetId());
      }
      if (!testRunCursor.hasNext()) {
         TestReport testReport;
         try {
            testReport = buildGenericTestReport(testSuiteRun)
                  .setTestRunReports(testSuiteSession.getTestRunReportBuilders())
                  .build();
         } catch (Exception e) {
            TestReportBuilder testReportBuilder = buildGenericTestReport(testSuiteRun);
            testReport = testReportBuilder
                  .setTestRunReports(testSuiteSession.getTestRunReportBuilders())
                  .setTestService(getTestServiceBuilder())
                  .addUnexpectedError(new UnexpectedErrorBuilder().fromThrowable(e))
                  .build();
         }
         String sessionId = testRun.getTestSetId();
         sessionStore.removeTestRun(sessionId);
         sessionStore.removeTestSuite(sessionId);
         recordAndReturnTestReport(testReport, testSuiteRun);
      } else {
         try {
            run(testRunCursor.next());
            //fixme: remove this error handling from here
         } catch (Exception e) {
            logger.error("Error running test {}", (testRun.getTest() != null ? testRun.getTest().getName() : ""), e);
            TestReportBuilder testReportBuilder = buildGenericTestReport(testSuiteRun);
            TestReport testReport = testReportBuilder.setDateTime(Date.from(Instant.now()))
                  .setTestRunReports(testSuiteSession.getTestRunReportBuilders())
                  .addUnexpectedError(getUnexpectedErrorBuilder(e))
                  .build();
            String sessionId = testRun.getTestSetId();
            sessionStore.removeTestRun(sessionId);
            sessionStore.removeTestSuite(sessionId);
            recordAndReturnTestReport(testReport, testSuiteRun);
         }
      }
   }


   @Override
   public void finishStep(StepRun stepRun, StepRunReportBuilder stepRunReportBuilder) {
      String testRunId = stepRun.testRunId();
      TestRun testRun = getTestRunFromStore(testRunId);
      List<InputBuilder> inputs = convertPropertiesToItems(testRun.getInputs());
      addStepReportToStore(testRunId, stepRunReportBuilder);
      StepCursor stepCursor = sessionStore.getTestRun(testRunId).getStepCursor();
      if (!stepCursor.hasNext()) {
         TestRunReportBuilder testRunReportBuilder = new TestRunReportBuilder()
               .addStepRunReports(getStepReportsFromStore(testRunId))
               .setDateTime(Date.from(Instant.now()))
               .setInputs(inputs)
               .setTest(new TestBuilder()
                     .setId(testRun.getTest().getId())
                     .setName(testRun.getTest().getName()));
         maestroEventProducer.returnTestRunReport(testRun, testRunReportBuilder);
      } else {
         runStep(new StepRun(stepRun.testSetId(), stepRun.testRunId(), stepCursor.next()));
      }
   }

   /**
    * @param e an exception
    *
    * @return UnexpectedErrorBuilder containing e data
    */
   private UnexpectedErrorBuilder getUnexpectedErrorBuilder(Exception e) {
      return new UnexpectedErrorBuilder()
            .setName(e.getClass().getSimpleName())
            .setMessage(e.getMessage());
   }

   private TestServiceBuilder getTestServiceBuilder() {
      return new TestServiceBuilder()
            .setDisclaimer(
                  "This Gazelle execution service is provided for technical compliance purposes only and does not " +
                  "constitute legal, medical, or regulatory advice.")
            .setServiceIdentification(
                  new EntityIdentificationBuilder().setName(GAZELLE_ENGINE).setVersion(maestroVersion));
   }


   private TestReportBuilder buildGenericTestReport(TestSuiteRun testSuiteRun) {
      TestReportBuilder testReportBuilder = new TestReportBuilder()
            .setTestService(getTestServiceBuilder())
            .setDateTime(Date.from(Instant.now()))
            .setTestSuiteName(testSuiteRun.getTestSuiteName())
            .setAccessControlList(new AccessControlListBuilder(testSuiteRun.getAccessControlList()))
            .setSystemsUnderTest(testSuiteRun.getSystemsUnderTest().stream().map(SystemUnderTestBuilder::new).toList());
      if ((testSuiteRun.getInternalId() != null && !testSuiteRun.getInternalId().isBlank()) ||
          (testSuiteRun.getTestSuiteName() != null && !testSuiteRun.getTestSuiteName().isBlank())) {
         StringBuilder noteBuilder = buildNote(testSuiteRun);
         testReportBuilder.setNote(noteBuilder.toString());
      }
      return testReportBuilder;
   }

   private StringBuilder buildNote(TestSuiteRun testSuiteRun) {
      StringBuilder noteBuilder = new StringBuilder("Tests executed as part of the suite ");
      if (testSuiteRun.getTestSuiteName() != null && !testSuiteRun.getTestSuiteName().isBlank()) {
         noteBuilder.append(testSuiteRun.getTestSuiteName());
      }
      if (!noteBuilder.isEmpty()) {
         noteBuilder.append("#");
      }
      if (testSuiteRun.getTestSuiteClientId() != null && !testSuiteRun.getTestSuiteClientId().isBlank()) {
         noteBuilder.append(testSuiteRun.getTestSuiteClientId());
      }
      return noteBuilder;
   }

   private void recordAndReturnTestReport(TestReport testReport, TestSuiteRun testSuiteRun) {
      try {
         ItemReference reportRef = testReportRecordService.recordTestReportWithReferences(testReport);
         TestReportSummary reportSummary = new TestReportSummary()
               .setId(reportRef.id())
               .setLocation(reportRef.location())
               .setDateTime(testReport.getDateTime())
               .setResult(testReport.getResult());
         if (testReport.getUnexpectedErrors() != null) {
            reportSummary.setUnexpectedErrors(testReport.getUnexpectedErrors());
         }
         maestroEventProducer.returnTestSetReport(testSuiteRun, reportSummary);
      } catch (Exception e) {
         maestroEventProducer.emitErrorTestRunEvent(e);
      }
   }

   /**
    * Create a new TestRunSession and put it in SessionStore
    *
    * @param testRunID id used to identify TestRunSession
    * @param testRun   TestRun currently being executed
    * @param context   testRun's Context with all the TestRun's inputs
    */
   private void putInStore(String testRunID, TestRun testRun, Context context) {
      StepCursor stepCursor = StepCursor.from(testRun.getTest());
      TestRunSession testRunSession = new TestRunSession(context, testRun, new ArrayList<>(),
            stepCursor);
      sessionStore.addTestRun(testRunID, testRunSession);
   }

   /**
    * @param testRunID id of currently executed testRun
    *
    * @return TestRun from TestRunSession with corresponding id
    */
   private TestRun getTestRunFromStore(String testRunID) {
      return sessionStore.getTestRun(testRunID).getTestRun();
   }

   /**
    * @param testRunID id of currently executed testRun
    *
    * @return Context from TestRunSession with corresponding id
    */
   private Context getContextFromStore(String testRunID) {
      return sessionStore.getTestRun(testRunID).getContext();
   }

   /**
    *
    * @param testRunId     id of currently executed testRun
    * @param reportBuilder a builder to put in TestRunSession with corresponding id
    */
   private void addStepReportToStore(String testRunId, StepRunReportBuilder reportBuilder) {
      sessionStore.getTestRun(testRunId).addStepRunReport(reportBuilder);
   }

   /**
    * @param testRunId id of currently executed testRun
    *
    * @return A list of StepRunReportBuilders from TestRunSession with corresponding id
    */
   private List<StepRunReportBuilder> getStepReportsFromStore(String testRunId) {
      return sessionStore.getTestRun(testRunId).getStepReports();
   }


   /**
    * Run current testreport and initialize the stepRunner if mandatory
    *
    * @param stepRun contain step to run
    */
   private void runStep(StepRun stepRun) {
      try {
         String testRunId = stepRun.testRunId();
         stepRun.step().setProperties(proxifyProperty(stepRun.step().getProperties(), getContextFromStore(testRunId)));
         maestroEventProducer.executeStep(stepRun);
      } catch (Exception e) {
         logger.error("Error running step {}", stepRun.step().getName(), e);
         StepRunReportBuilder builder = new StepRunReportBuilder()
               .setStepId(stepRun.step().getName())
               .setType(stepRun.step().getType())
               .setResult(StepResult.UNDEFINED)
               .addUnexpectedError(getUnexpectedErrorBuilder(e));
         finishStep(stepRun, builder);
      }
   }

   /**
    * @param properties a list of property with a name and an associated value
    *
    * @return a context containing inputs marked has content
    */
   private Context extractInputs(List<Property<?>> properties) {

      Context context = new Context();

      for (Property<?> property : properties) {
         context.addProperty(property.getName(), property);
      }

      return context;
   }

   /**
    * method used to convert PropertyRef to ResolvablePropertyRef
    *
    * @param properties a list of Property which can contains PropertyRef
    */
   private List<Property<?>> proxifyProperty(List<Property<?>> properties, Context context) {
      List<Property<?>> res = new ArrayList<>();
      for (Property<?> property : properties) {
         if (property instanceof PropertyRef<?> propertyRef) {
            res.add(new ResolvablePropertyRef<>(propertyRef, context));
         } else {
            res.add(property);
         }
      }
      return res;
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
