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

import net.ihe.gazelle.lang.UnexpectedError;
import net.ihe.gazelle.maestro.api.business.testreport.Result;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestReportSummary;
import net.ihe.gazelle.maestro.api.business.testreport.TestRunReport;
import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.PropertyRef;
import net.ihe.gazelle.maestro.api.business.testrun.Step;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.assertion.step.business.AssertionStep;
import net.ihe.gazelle.maestro.business.MaestroImpl;
import net.ihe.gazelle.maestro.quarkus.mock.InMemoryTestReportRecordService;
import net.ihe.gazelle.maestro.quarkus.mock.StepMockEvent;
import net.ihe.gazelle.maestro.quarkus.client.TestReportClient;
import net.ihe.gazelle.maestro.quarkus.factory.TestRunMockEvent;
import net.ihe.gazelle.maestro.quarkus.handler.HandlerProviderMock;
import net.ihe.gazelle.maestro.quarkus.jobs.EventListener;
import net.ihe.gazelle.maestro.quarkus.jobs.EventMaestroProducer;
import net.ihe.gazelle.maestro.quarkus.jobs.StepRunnerJob;
import net.ihe.gazelle.maestro.quarkus.jobs.SynchronousMaestro;
import net.ihe.gazelle.maestro.quarkus.ws.async.AsynchronousMaestro;
import net.ihe.gazelle.maestro.quarkus.ws.async.tmintegration.TMTestRunMaestro;
import net.ihe.gazelle.maestro.simulation.step.business.SimulationStep;
import net.ihe.gazelle.maestro.validation.step.business.ValidationStep;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;


class MaestroStepsIntegrationTest {

    private static SynchronousMaestro synchronousMaestro;
    private static InMemoryTestReportRecordService testReportRecordService;

    @BeforeAll
    static void setUp() {
        TestRunMockEvent testRunMockEvent = new TestRunMockEvent();
        StepMockEvent stepMockEvent = new StepMockEvent();
        EventMaestroProducer maestroProducer = new EventMaestroProducer(testRunMockEvent, stepMockEvent);
        testReportRecordService = new InMemoryTestReportRecordService();
        MaestroImpl maestro = new MaestroImpl(
                new EventMaestroProducer(testRunMockEvent, stepMockEvent),
                new SessionStoreContextImpl(),
                testReportRecordService
        );
        synchronousMaestro = new SynchronousMaestro(3, maestroProducer);
        EventListener eventListener = createListener(maestroProducer, maestro);
        testRunMockEvent.addListener(eventListener);
        stepMockEvent.addListener(eventListener);
    }

    private static EventListener createListener(EventMaestroProducer maestroProducer, MaestroImpl maestro) {
        AsynchronousMaestro asynchronousMaestro = new AsynchronousMaestro(maestroProducer, new TestReportClient());
        StepRunnerJob stepRunnerJob = new StepRunnerJob(
                new StepRunnerProviderSPI(new HandlerProviderMock()),
                maestroProducer
        );
        TMTestRunMaestro tmTestRunMaestro = new TMTestRunMaestro(maestroProducer, new TestReportClient());
        return new EventListener(maestro, synchronousMaestro, asynchronousMaestro, stepRunnerJob, tmTestRunMaestro);
    }


    @Test
    void testMaestroValidationStep() throws ExecutionException, InterruptedException, TimeoutException {
        testReportRecordService.clear(); // Clear previous test data

        List<TestRun> testRuns = getTestRunsWithOneValidationStep();
        TestReportSummary reportSummary = synchronousMaestro.executeTestSet(testRuns);

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
        if (hasInputsWithContent(testRuns)) {
            assertTrue(testReportRecordService.getStoredAttachmentsCount() > 0, "Input attachments should be stored");
        }
    }

    @Test
    void testMaestroSimulationStep() throws ExecutionException, InterruptedException, TimeoutException {
        testReportRecordService.clear(); // Clear previous test data

        List<TestRun> testRuns = getTestRunsWithOneSimulationStep();
        TestReportSummary reportSummary = synchronousMaestro.executeTestSet(testRuns);

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

        List<TestRun> testRuns = getTestRunsWithValidationStepAndAssertionStepWithReference();
        TestReportSummary reportSummary = synchronousMaestro.executeTestSet(testRuns);

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

        List<TestRun> testRuns = getTestRunsWithOneValidationStep();
        Property<byte[]> contentToValidate = new Property<>(ValidationStep.CONTENT_TO_VALIDATE, "liyvfgljv".getBytes());
        Step validationStep = new ValidationStep("test-validation-two",
                new Property<>(ValidationStep.VALIDATION_SERVICE, "matchbox"),
                new Property<>(ValidationStep.VALIDATION_PROFILE, "Parameters"),
                contentToValidate
        );
        testRuns.getFirst().getTest().addStep(validationStep);
        testRuns.getFirst().addInputs(contentToValidate);
        TestReportSummary reportSummary = synchronousMaestro.executeTestSet(testRuns);

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
        assertTrue(testReportRecordService.getStoredAttachmentsCount() >= 2, "Both validation step inputs should be stored as attachments");
    }

    @Test
    void testMaestroWithInvalidTwoSameStepTestRun() throws ExecutionException, InterruptedException, TimeoutException {
        testReportRecordService.clear(); // Clear previous test data

        List<TestRun> testRuns = getTestRunsWithOneValidationStep();
        Step validationStep = new ValidationStep("test-validation",
                new Property<>(ValidationStep.VALIDATION_SERVICE, "matchbox"),
                new Property<>(ValidationStep.VALIDATION_PROFILE, "Parameters"),
                new Property<>(ValidationStep.CONTENT_TO_VALIDATE, "liyvfgljv".getBytes())
        );
        testRuns.getFirst().getTest().addStep(validationStep);
        TestReportSummary reportSummary = synchronousMaestro.executeTestSet(testRuns);

        // Verify the summary
        assertNotNull(reportSummary);
        assertEquals(Result.UNDEFINED, reportSummary.getResult());
        assertNotNull(reportSummary.getId(), "Report summary should have an ID");

        // Verify persistence behavior even with errors
        assertTrue(testReportRecordService.getStoredReportsCount() > 0, "TestReport should be persisted even with errors");

        // Retrieve and verify the full report
        TestReport fullReport = testReportRecordService.getStoredReport(reportSummary.getId());
        assertNotNull(fullReport, "Should be able to retrieve full report from storage");

        List<TestRunReport> testRunReports = fullReport.getTestRunReports();
        assertEquals(1, testRunReports.size());
        TestRunReport testRunReport = testRunReports.getFirst();
        assertEquals(0, testRunReport.getStepRunReports().size());
        List<UnexpectedError> unexpectedErrors = testRunReport.getUnexpectedErrors();
        assertEquals(1, unexpectedErrors.size());
        assertEquals("Several steps has the same name : test-validation should be unique", unexpectedErrors.getFirst().getMessage());
        net.ihe.gazelle.maestro.api.business.testreport.Test test = testRunReport.getTest();
        assertEquals("validationTest", test.getName());
        assertFalse(test.getId().isBlank());
    }

    @Test
    void testMaestroWithNullTestRun() {
        testReportRecordService.clear(); // Clear previous test data

        List<TestRun> testRuns = new ArrayList<>();
        testRuns.add(null);
        assertThrows(TimeoutException.class, () -> synchronousMaestro.executeTestSet(testRuns));
    }

    // Helper method to check if test runs have inputs with content
    private boolean hasInputsWithContent(List<TestRun> testRuns) {
        return testRuns.stream()
                .filter(tr -> tr != null && tr.getTest() != null && tr.getTest().getSteps() != null)
                .flatMap(tr -> tr.getTest().getSteps().stream())
                .anyMatch(ValidationStep.class::isInstance);
    }

    private static List<TestRun> getTestRunsWithOneValidationStep() {
        List<TestRun> testRuns = new ArrayList<>();
        Property<byte[]> contentToValidate = new Property<>(ValidationStep.CONTENT_TO_VALIDATE, "liyvfgljv".getBytes());
        Step validationStep = new ValidationStep("test-validation",
                new Property<>(ValidationStep.VALIDATION_SERVICE, "matchbox"),
                new Property<>(ValidationStep.VALIDATION_PROFILE, "Parameters"),
                contentToValidate
        );
        net.ihe.gazelle.maestro.api.business.testrun.Test test = new net.ihe.gazelle.maestro.api.business.testrun.Test().addStep(validationStep);
        test.setName("validationTest");
        test.setId(UUID.randomUUID().toString());
        TestRun testRun = new TestRun();
        testRun.setTest(test);
        testRun.addInputs(contentToValidate);
        testRuns.add(testRun);
        return testRuns;
    }

    private static List<TestRun> getTestRunsWithOneSimulationStep() {
        List<TestRun> testRuns = new ArrayList<>();
        Step simulationStep = new SimulationStep("simulationStep",
                new Property<>(SimulationStep.SIMULATION_SERVICE, "MCSD"),
                new Property<>(SimulationStep.SEQUENCE_ID, "sequencId"),
                new Property<>(SimulationStep.TIMEOUT_SECONDS, 60),
                List.of(
                        new Property<>("Patient Id", "1.1.1.1"),
                        new Property<>("Boolean prop", true),
                        new Property<>("File prop", "test".getBytes())
                ));
        net.ihe.gazelle.maestro.api.business.testrun.Test test = new net.ihe.gazelle.maestro.api.business.testrun.Test().addStep(simulationStep);
        test.setName("simulationTest");
        test.setId(UUID.randomUUID().toString());
        TestRun testRun = new TestRun();
        testRun.setTest(test);
        testRuns.add(testRun);
        return testRuns;
    }

    private static List<TestRun> getTestRunsWithValidationStepAndAssertionStepWithReference() {
        Step assertionStep = new AssertionStep("test-assertion",
                new PropertyRef<>(AssertionStep.ASSERTION, "assertion", byte[].class),
                new Property<>(AssertionStep.ASSERTION_SERVICE, "toto")
        );
        net.ihe.gazelle.maestro.api.business.testrun.Test test = new net.ihe.gazelle.maestro.api.business.testrun.Test().addStep(assertionStep);
        test.setName("assertionTest");
        test.setId(UUID.randomUUID().toString());
        TestRun testRun = new TestRun();
        testRun.setTest(test);
        List<TestRun> result = getTestRunsWithOneValidationStep();
        result.add(testRun);
        return result;
    }


}
