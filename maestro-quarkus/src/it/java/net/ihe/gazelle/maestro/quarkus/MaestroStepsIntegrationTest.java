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

import net.ihe.gazelle.maestro.api.business.SynchronousMaestro;
import net.ihe.gazelle.maestro.api.business.property.*;
import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.business.test.TestSuite;
import net.ihe.gazelle.maestro.api.business.testreport.Result;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestRunReport;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;
import net.ihe.gazelle.maestro.assertion.step.business.equals.AssertEqualsStepDefinition;
import net.ihe.gazelle.maestro.engine.business.HardWiredEventBroker;
import net.ihe.gazelle.maestro.engine.business.MaestroImpl;
import net.ihe.gazelle.maestro.engine.business.MaestroRecorderImpl;
import net.ihe.gazelle.maestro.engine.business.MetadataServiceMock;
import net.ihe.gazelle.maestro.engine.business.mock.InMemoryTestReportRecordService;
import net.ihe.gazelle.maestro.quarkus.broker.MaestroEventController;
import net.ihe.gazelle.maestro.quarkus.mock.TestRunMockEvent;
import net.ihe.gazelle.maestro.simulation.step.business.SimulationStepDefinition;
import net.ihe.gazelle.maestro.validation.step.business.ValidationStepDefinition;
import net.ihe.gazelle.security.business.acl.AccessControlList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import technical.dao.SessionStoreImpl;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;


/*class MaestroStepsIntegrationTest {

    private static SynchronousMaestro synchronousMaestro;
    private static InMemoryTestReportRecordService testReportRecordService;

    @BeforeAll
    static void setUp() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        TestRunMockEvent testRunMockEvent = new TestRunMockEvent();
        testReportRecordService = new InMemoryTestReportRecordService();
        MaestroImpl maestroImpl = new MaestroImpl(
                new SessionStoreImpl<>(),
                new SessionStoreImpl<>(),
                new HardWiredEventBroker(executorService),
                new MetadataServiceMock()
        );
        synchronousMaestro = new SynchronousMaestro(10, new MaestroRecorderImpl(maestroImpl, testReportRecordService));
        MaestroEventController eventListener = new MaestroEventController(maestroImpl, testRunMockEvent);
        testRunMockEvent.addListener(eventListener);
    }


    @Test
    void testMaestroValidationStep() throws InterruptedException, TimeoutException {
        testReportRecordService.clear(); // Clear previous test data

        TestSuiteRun testSuiteRun = getTestRunsWithOneValidationStep();
        TestReport report = synchronousMaestro.executeTestSuite(testSuiteRun, false);

        // Verify the summary contains basic information
        assertNotNull(report);
        assertEquals(Result.UNDEFINED, report.getResult());
        assertNotNull(report.getUuid(), "Report summary should have an ID");

        // Verify persistence behavior - the full report should be stored
        assertFalse(testReportRecordService.getRecordedReports().isEmpty(), "TestReport should be persisted");

        // Retrieve the full report from storage using the ID
        TestReport fullReport = testReportRecordService.getRecordedReports().getFirst();
        assertNotNull(fullReport, "Should be able to retrieve full report from storage");

        // Verify the full report details
        List<TestRunReport> testRunReports = fullReport.getTestRunReports();
        assertEquals(1, testRunReports.size());
        assertEquals(1, testRunReports.getFirst().getStepRunReports().size());
        assertEquals(1, fullReport.getTestCounters().getTotal());
        assertEquals(1, fullReport.getTestCounters().getUndefined());
    }

    @Test
    void testMaestroSimulationStep() throws InterruptedException, TimeoutException {
        testReportRecordService.clear(); // Clear previous test data

        TestSuiteRun testSuiteRun = getTestRunsWithOneSimulationStep();
        TestReport report = synchronousMaestro.executeTestSuite(testSuiteRun, false);

        // Verify the summary
        assertNotNull(report);
        assertEquals(Result.PASSED, report.getResult());
        assertNotNull(report.getUuid(), "Report summary should have an ID");

        // Verify persistence behavior
        assertFalse(testReportRecordService.getRecordedReports().isEmpty(), "TestReport should be persisted");

        // Retrieve and verify the full report
        TestReport fullReport = testReportRecordService.getRecordedReports().getFirst();
        assertNotNull(fullReport, "Should be able to retrieve full report from storage");

        List<TestRunReport> testRunReports = fullReport.getTestRunReports();
        assertEquals(1, testRunReports.size());
        assertEquals(1, testRunReports.getFirst().getStepRunReports().size());
        assertEquals(1, fullReport.getTestCounters().getTotal());
        assertEquals(1, fullReport.getTestCounters().getPassed());
    }

    @Test
    void testMaestroValidationAndAssertionStep() throws ExecutionException, InterruptedException, TimeoutException {
        testReportRecordService.clear(); // Clear previous test data

        TestSuiteRun testSuiteRun = getTestRunsWithValidationStepAndAssertionStepWithReference();
        TestReport report = synchronousMaestro.executeTestSuite(testSuiteRun, false);

        // Verify the summary
        assertNotNull(report);
        assertEquals(Result.UNDEFINED, report.getResult());
        assertNotNull(report.getUuid(), "Report summary should have an ID");

        // Verify persistence behavior for multiple test runs
        assertFalse(testReportRecordService.getRecordedReports().isEmpty(), "TestReports should be persisted");

        // Retrieve and verify the full report
        TestReport fullReport = testReportRecordService.getRecordedReports().getFirst();
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
        Property contentToValidate = new ByteArrayProperty("content", "liyvfgljv".getBytes());
        Step validationStep = new Step()
                .setId("test-validation-two")
                .setProperties(List.of(
                        new StringProperty(ValidationStepDefinition.VALIDATION_SERVICE, "matchbox"),
                        new StringProperty(ValidationStepDefinition.VALIDATION_PROFILE, "Parameters"),
                        contentToValidate
                )
        );
        testSuiteRun.getTests().getFirst().addStep(validationStep);
        testSuiteRun.getInputs().add(contentToValidate);
        TestReport report = synchronousMaestro.executeTestSuite(testSuiteRun, false);

        // Verify the summary
        assertNotNull(report);
        assertEquals(Result.UNDEFINED, report.getResult());
        assertNotNull(report.getUuid(), "Report summary should have an ID");

        // Verify persistence behavior with multiple steps
        assertFalse(testReportRecordService.getRecordedReports().isEmpty(), "TestReport should be persisted");

        // Retrieve and verify the full report
        TestReport fullReport = testReportRecordService.getRecordedReports().getFirst();
        assertNotNull(fullReport, "Should be able to retrieve full report from storage");

        List<TestRunReport> testRunReports = fullReport.getTestRunReports();
        assertEquals(1, testRunReports.size());
        assertEquals(2, testRunReports.getFirst().getStepRunReports().size());
        assertEquals(1, fullReport.getTestCounters().getTotal());
        assertEquals(1, fullReport.getTestCounters().getUndefined());
    }

    @Test
    void testMaestroWithInvalidTwoSameStepTestRun() {
        testReportRecordService.clear(); // Clear previous test data

        TestSuiteRun testSuiteRun = getTestRunsWithOneValidationStep();
        Step validationStep = new Step()
                .setId("test-validation-two")
                .setProperties(List.of(
                        new StringProperty(ValidationStepDefinition.VALIDATION_SERVICE, "matchbox"),
                        new StringProperty(ValidationStepDefinition.VALIDATION_PROFILE, "Parameters"),
                        new ByteArrayProperty("content", "liyvfgljv".getBytes())
                )
        );
        testSuiteRun.getTests().getFirst().addStep(validationStep);
        Throwable throwable = assertThrows(IllegalArgumentException.class, () -> synchronousMaestro.executeTestSuite(
                testSuiteRun, false));
        assertThat(throwable.getMessage(), containsString("Steps must be unique => invalid"));
    }

    private static TestSuiteRun getTestRunsWithOneValidationStep() {
        Property contentToValidate = new ByteArrayProperty("content", "liyvfgljv".getBytes());
        Step validationStep = new Step()
                .setId("test-validation-two")
                .setName("Validation Step")
                .setType(ValidationStepDefinition.TYPE)
                .setProperties(List.of(
                                new StringProperty(ValidationStepDefinition.VALIDATION_SERVICE, "matchbox"),
                                new StringProperty(ValidationStepDefinition.VALIDATION_PROFILE, "Parameters"),
                                contentToValidate
                        )
                );
        net.ihe.gazelle.maestro.api.business.test.Test test =
                new net.ihe.gazelle.maestro.api.business.test.Test().addStep(
                        validationStep);
        test.setName("validationTest");
        test.setId(UUID.randomUUID().toString());
        TestSuiteRun testSuiteRun = new TestSuiteRun();
        return testSuiteRun
                .setTestSuite(
                        new TestSuite()
                                .setName("Test Suite")
                                .setId("Test Suite"))
                .setTests(List.of(test))
                .setAccessControlList(new AccessControlList().setOwners(Set.of("gazelle")));
    }

    private static TestSuiteRun getTestRunsWithOneSimulationStep() {
        List<Property> inputs = List.of(
                new StringProperty("Patient Id", "1.1.1.1"),
                new BooleanProperty("Boolean prop", true),
                new ByteArrayProperty("File prop", "test".getBytes())
        );
        Step simulationStep = new Step()
                .setId("simulationStep")
                .setName("Simulation Step")
                .setType(SimulationStepDefinition.TYPE)
                .setProperties(List.of(
                        new StringProperty(SimulationStepDefinition.SIMULATION_SERVICE, "MCSD"),
                        new StringProperty(SimulationStepDefinition.SEQUENCE_ID, "sequenceId"),
                        new IntegerProperty(SimulationStepDefinition.TIMEOUT_SECONDS, 60)
                ))
                .addProperties(inputs);
        TestSuiteRun testSuiteRun = new TestSuiteRun();
        net.ihe.gazelle.maestro.api.business.test.Test test =
                new net.ihe.gazelle.maestro.api.business.test.Test().addStep(
                        simulationStep);
        test.setName("simulationTest");
        test.setId(UUID.randomUUID().toString());
        return testSuiteRun
                .setTestSuite(
                        new TestSuite()
                                .setName("Test Suite")
                                .setId("Test Suite"))
                .setTests(List.of(test))
                .setAccessControlList(new AccessControlList().setOwners(Set.of("gazelle")));
    }

    private static TestSuiteRun getTestRunsWithValidationStepAndAssertionStepWithReference() {
        Step assertionStep = new Step()
                .setId("test-assertion")
                .setName("Assertion Step")
                .setType(AssertEqualsStepDefinition.TYPE)
                .setProperties(List.of(
                        new StringProperty(AssertEqualsStepDefinition.EXPECTED, "Expected"),
                        new StringProperty(AssertEqualsStepDefinition.ACTUAL, "Expected")
                )
        );
        TestSuiteRun testSuiteRun = getTestRunsWithOneValidationStep();
        net.ihe.gazelle.maestro.api.business.test.Test test =
                new net.ihe.gazelle.maestro.api.business.test.Test().addStep(
                        assertionStep);
        test.setName("assertionTest");
        test.setId(UUID.randomUUID().toString());
        testSuiteRun.getTests().add(test);
        return testSuiteRun;
    }


}*/
