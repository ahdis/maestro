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

package net.ihe.gazelle.maestro.quarkus;

import net.ihe.gazelle.maestro.api.business.testreport.Result;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestReportSummary;
import net.ihe.gazelle.maestro.api.business.testreport.TestRunReport;
import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.PropertyRef;
import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;
import net.ihe.gazelle.maestro.assertion.step.business.AssertionStep;
import net.ihe.gazelle.maestro.engine.business.TestRunner;
import net.ihe.gazelle.maestro.engine.business.context.SessionStoreImpl;
import net.ihe.gazelle.maestro.quarkus.factory.TestRunMockEvent;
import net.ihe.gazelle.maestro.quarkus.handler.HandlerProviderMock;
import net.ihe.gazelle.maestro.quarkus.broker.MaestroEventController;
import net.ihe.gazelle.maestro.engine.business.StepRunner;
import net.ihe.gazelle.maestro.api.business.SynchronousMaestro;
import net.ihe.gazelle.maestro.quarkus.mock.InMemoryTestReportRecordService;
import net.ihe.gazelle.maestro.engine.business.MaestroImpl;
import net.ihe.gazelle.maestro.simulation.step.business.SimulationStep;
import net.ihe.gazelle.maestro.validation.step.business.ValidationStep;
import net.ihe.gazelle.security.business.acl.AccessControlList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class MaestroStepsIntegrationTest {

   private static SynchronousMaestro synchronousMaestro;
   private static InMemoryTestReportRecordService testReportRecordService;
   private static MaestroImpl maestroImpl;

   @BeforeAll
   static void setUp() {
      TestRunMockEvent testRunMockEvent = new TestRunMockEvent();
      EventMaestroProducer eventProducer = new EventMaestroProducer(testRunMockEvent);
      testReportRecordService = new InMemoryTestReportRecordService();
      TestRunner maestro = new TestRunner(
            eventProducer,
            new SessionStoreImpl(),
            testReportRecordService, "999.0.0"
      );
      maestroImpl = new MaestroImpl(eventProducer);
      synchronousMaestro = new SynchronousMaestro(10, maestroImpl);
      StepRunner stepRunner = new StepRunner(
            new StepRunnerProviderSPI(new HandlerProviderMock()),
            eventProducer
      );
      MaestroEventController eventListener = new MaestroEventController(maestro, maestroImpl, stepRunner);
      testRunMockEvent.addListener(eventListener);
   }


   @Test
   void testMaestroValidationStep() throws ExecutionException, InterruptedException, TimeoutException {
      testReportRecordService.clear(); // Clear previous test data

      TestSuiteRun testSuiteRun = getTestRunsWithOneValidationStep();
      TestReportSummary reportSummary = synchronousMaestro.executeTestSuite(testSuiteRun);

      // Verify the summary contains basic information
      assertNotNull(reportSummary);
      assertEquals(Result.UNDEFINED, reportSummary.getResult());
      assertNotNull(reportSummary.getId(), "Report summary should have an ID");

      // Verify persistence behavior - the full report should be stored
      assertTrue(testReportRecordService.getStoredReportsCount() > 0, "TestReport should be persisted");

      // Retrieve the full report from storage using the ID
      TestReport fullReport = testReportRecordService.getStoredReport(reportSummary.getId());
      assertNotNull(fullReport, "Should be able to retrieve full report from storage");

      // Verify the full report details
      List<TestRunReport> testRunReports = fullReport.getTestRunReports();
      assertEquals(1, testRunReports.size());
      assertEquals(1, testRunReports.getFirst().getStepRunReports().size());
      assertEquals(1, fullReport.getTestCounters().getTotal());
      assertEquals(1, fullReport.getTestCounters().getUndefined());

      // Verify that inputs with content were stored as attachments
      if (hasInputsWithContent(testSuiteRun.getTestRuns())) {
         assertTrue(testReportRecordService.getStoredAttachmentsCount() > 0, "Input attachments should be stored");
      }
   }

   @Test
   void testMaestroSimulationStep() throws ExecutionException, InterruptedException, TimeoutException {
      testReportRecordService.clear(); // Clear previous test data

      TestSuiteRun testSuiteRun = getTestRunsWithOneSimulationStep();
      TestReportSummary reportSummary = synchronousMaestro.executeTestSuite(testSuiteRun);

      // Verify the summary
      assertNotNull(reportSummary);
      assertEquals(Result.PASSED, reportSummary.getResult());
      assertNotNull(reportSummary.getId(), "Report summary should have an ID");

      // Verify persistence behavior
      assertTrue(testReportRecordService.getStoredReportsCount() > 0, "TestReport should be persisted");

      // Retrieve and verify the full report
      TestReport fullReport = testReportRecordService.getStoredReport(reportSummary.getId());
      assertNotNull(fullReport, "Should be able to retrieve full report from storage");

      List<TestRunReport> testRunReports = fullReport.getTestRunReports();
      assertEquals(1, testRunReports.size());
      assertEquals(1, testRunReports.getFirst().getStepRunReports().size());
      assertEquals(1, fullReport.getTestCounters().getTotal());
      assertEquals(1, fullReport.getTestCounters().getPassed());

      // Check if any outputs were generated and stored as items
      boolean hasOutputs = testRunReports.stream()
            .flatMap(tr -> tr.getStepRunReports().stream())
            .anyMatch(sr -> sr.getOutputs() != null && !sr.getOutputs().isEmpty());
      if (hasOutputs) {
         assertTrue(testReportRecordService.getStoredItemsCount() > 0, "Step outputs should be stored as items");
      }
   }

   @Test
   void testMaestroValidationAndAssertionStep() throws ExecutionException, InterruptedException, TimeoutException {
      testReportRecordService.clear(); // Clear previous test data

      TestSuiteRun testSuiteRun = getTestRunsWithValidationStepAndAssertionStepWithReference();
      TestReportSummary reportSummary = synchronousMaestro.executeTestSuite(testSuiteRun);

      // Verify the summary
      assertNotNull(reportSummary);
      assertEquals(Result.UNDEFINED, reportSummary.getResult());
      assertNotNull(reportSummary.getId(), "Report summary should have an ID");

      // Verify persistence behavior for multiple test runs
      assertTrue(testReportRecordService.getStoredReportsCount() > 0, "TestReports should be persisted");

      // Retrieve and verify the full report
      TestReport fullReport = testReportRecordService.getStoredReport(reportSummary.getId());
      assertNotNull(fullReport, "Should be able to retrieve full report from storage");

      List<TestRunReport> testRunReports = fullReport.getTestRunReports();
      assertEquals(2, testRunReports.size());
      assertEquals(1, testRunReports.getFirst().getStepRunReports().size());
      assertEquals(1, testRunReports.getLast().getStepRunReports().size());
      assertEquals(2, fullReport.getTestCounters().getTotal());
      assertEquals(1, fullReport.getTestCounters().getPassed());
      assertEquals(1, fullReport.getTestCounters().getUndefined());
   }

   @Test
   void testMaestroWithSeveralStepOnSameTestRun() throws ExecutionException, InterruptedException, TimeoutException {
      testReportRecordService.clear(); // Clear previous test data

      TestSuiteRun testSuiteRun = getTestRunsWithOneValidationStep();
      Property<byte[]> contentToValidate = new Property<>("content", "liyvfgljv".getBytes());
      Step validationStep = new ValidationStep("test-validation-two",
            new Property<>(ValidationStep.VALIDATION_SERVICE, "matchbox"),
            new Property<>(ValidationStep.VALIDATION_PROFILE, "Parameters"),
            new ArrayList<>(List.of(contentToValidate))
      );
      testSuiteRun.getTestRuns().getFirst().getTest().addStep(validationStep);
      testSuiteRun.getTestRuns().getFirst().addInputs(contentToValidate);
      TestReportSummary reportSummary = synchronousMaestro.executeTestSuite(testSuiteRun);

      // Verify the summary
      assertNotNull(reportSummary);
      assertEquals(Result.UNDEFINED, reportSummary.getResult());
      assertNotNull(reportSummary.getId(), "Report summary should have an ID");

      // Verify persistence behavior with multiple steps
      assertTrue(testReportRecordService.getStoredReportsCount() > 0, "TestReport should be persisted");

      // Retrieve and verify the full report
      TestReport fullReport = testReportRecordService.getStoredReport(reportSummary.getId());
      assertNotNull(fullReport, "Should be able to retrieve full report from storage");

      List<TestRunReport> testRunReports = fullReport.getTestRunReports();
      assertEquals(1, testRunReports.size());
      assertEquals(2, testRunReports.getFirst().getStepRunReports().size());
      assertEquals(1, fullReport.getTestCounters().getTotal());
      assertEquals(1, fullReport.getTestCounters().getUndefined());

      // With 2 validation steps containing content, we should have attachments
      assertTrue(testReportRecordService.getStoredAttachmentsCount() >= 2,
            "Both validation step inputs should be stored as attachments");
   }

   @Test
   void testMaestroWithInvalidTwoSameStepTestRun() {
      testReportRecordService.clear(); // Clear previous test data

      TestSuiteRun testSuiteRun = getTestRunsWithOneValidationStep();
      Step validationStep = new ValidationStep("test-validation",
            new Property<>(ValidationStep.VALIDATION_SERVICE, "matchbox"),
            new Property<>(ValidationStep.VALIDATION_PROFILE, "Parameters"),
            List.of(new Property<>("content", "liyvfgljv".getBytes()))
      );
      testSuiteRun.getTestRuns().getFirst().getTest().addStep(validationStep);
      Throwable throwable = assertThrows(IllegalArgumentException.class, () -> synchronousMaestro.executeTestSuite(
            testSuiteRun));
      assertThat(throwable.getMessage(), containsString("Steps must be unique => invalid"));
   }


   // Helper method to check if test runs have inputs with content
   private boolean hasInputsWithContent(List<TestRun> testRuns) {
      return testRuns.stream()
            .filter(tr -> tr != null && tr.getTest() != null && tr.getTest().getSteps() != null)
            .flatMap(tr -> tr.getTest().getSteps().stream())
            .anyMatch(ValidationStep.class::isInstance);
   }

   private static TestSuiteRun getTestRunsWithOneValidationStep() {
      List<TestRun> testRuns = new ArrayList<>();
      Property<byte[]> contentToValidate = new Property<>("content", "liyvfgljv".getBytes());
      Step validationStep = new ValidationStep("test-validation",
            new Property<>(ValidationStep.VALIDATION_SERVICE, "matchbox"),
            new Property<>(ValidationStep.VALIDATION_PROFILE, "Parameters"),
            new ArrayList<>(List.of(contentToValidate))
      );
      net.ihe.gazelle.maestro.api.business.test.Test test =
            new net.ihe.gazelle.maestro.api.business.test.Test().addStep(
            validationStep);
      test.setName("validationTest");
      test.setId(UUID.randomUUID().toString());
      TestSuiteRun testSuiteRun = new TestSuiteRun();
      TestRun testRun = new TestRun();
      testRun.setTest(test);
      testRun.addInputs(contentToValidate);
      testRun.setTestSuiteId(testSuiteRun.getInternalId());
      testRuns.add(testRun);
      return testSuiteRun
            .setTestSuiteName("Test Suite")
            .setTestSuiteClientId(UUID.randomUUID().toString())
            .setTestRuns(testRuns)
            .setAccessControlList(new AccessControlList().setOwners(Set.of("gazelle")))
            ;
   }

   private static TestSuiteRun getTestRunsWithOneSimulationStep() {
      List<TestRun> testRuns = new ArrayList<>();
      List<Property<?>> inputs = List.of(
            new Property<>("Patient Id", "1.1.1.1"),
            new Property<>("Boolean prop", true),
            new Property<>("File prop", "test".getBytes())
      );
      Step simulationStep = new SimulationStep("simulationStep",
            new Property<>(SimulationStep.SIMULATION_SERVICE, "MCSD"),
            new Property<>(SimulationStep.SEQUENCE_ID, "sequencId"),
            new Property<>(SimulationStep.TIMEOUT_SECONDS, 60),
            inputs);
      TestSuiteRun testSuiteRun = new TestSuiteRun();
      net.ihe.gazelle.maestro.api.business.test.Test test =
            new net.ihe.gazelle.maestro.api.business.test.Test().addStep(
            simulationStep);
      test.setName("simulationTest");
      test.setId(UUID.randomUUID().toString());
      TestRun testRun = new TestRun()
            .setTestSuiteId(testSuiteRun.getInternalId())
            .setInputs(inputs);
      testRun.setTest(test);
      testRuns.add(testRun);
      return testSuiteRun
            .setTestSuiteName("Test Suite")
            .setTestSuiteClientId(UUID.randomUUID().toString())
            .setTestRuns(testRuns)
            .setAccessControlList(new AccessControlList().setOwners(Set.of("gazelle")))
            ;
   }

   private static TestSuiteRun getTestRunsWithValidationStepAndAssertionStepWithReference() {
      Step assertionStep = new AssertionStep("test-assertion",
            new PropertyRef<>(AssertionStep.ASSERTION, "assertion", byte[].class),
            new Property<>(AssertionStep.ASSERTION_SERVICE, "toto")
      );
      TestSuiteRun testSuiteRun = getTestRunsWithOneValidationStep();
      net.ihe.gazelle.maestro.api.business.test.Test test =
            new net.ihe.gazelle.maestro.api.business.test.Test().addStep(
            assertionStep);
      test.setName("assertionTest");
      test.setId(UUID.randomUUID().toString());
      TestRun testRun = new TestRun()
            .setTestSuiteId(testSuiteRun.getInternalId())
            .setInputs(List.of(new Property<>("assertion", "liyvfgljv".getBytes())));
      testRun.setTest(test);
      testSuiteRun.getTestRuns().add(testRun);
      return testSuiteRun;
   }


}
