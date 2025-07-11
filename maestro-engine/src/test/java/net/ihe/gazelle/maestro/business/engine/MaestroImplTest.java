/*
 * Copyright 2025 IHE International.
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

package net.ihe.gazelle.maestro.business.engine;

import net.ihe.gazelle.lang.UnexpectedError;
import net.ihe.gazelle.maestro.api.business.testreport.Result;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestRunReport;
import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.PropertyRef;
import net.ihe.gazelle.maestro.api.business.testrun.Step;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.business.step.assertion.AssertionStep;
import net.ihe.gazelle.maestro.business.step.validation.ValidationStep;
import net.ihe.gazelle.maestro.simulation.step.business.SimulationStep;
import net.ihe.gazelle.maestro.technical.SessionStoreContextImpl;
import net.ihe.gazelle.maestro.technical.StepRunnerProviderSPI;
import net.ihe.gazelle.maestro.technical.client.TestReportClient;
import net.ihe.gazelle.maestro.technical.handler.HandlerProviderMock;
import net.ihe.gazelle.maestro.technical.jobs.*;
import net.ihe.gazelle.maestro.technical.ws.async.AsynchronousMaestro;
import net.ihe.gazelle.maestro.technical.ws.async.tmintegration.TMTestRunMaestro;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;


class MaestroImplTest {

    private static SynchronousMaestro synchronousMaestro;

    @BeforeAll
    static void setUp() {
        TestRunMockEvent testRunMockEvent = new TestRunMockEvent();
        StepMockEvent stepMockEvent = new StepMockEvent();
        EventMaestroProducer maestroProducer = new EventMaestroProducer(testRunMockEvent, stepMockEvent);
        MaestroImpl maestro = new MaestroImpl(
                new EventMaestroProducer(testRunMockEvent, stepMockEvent),
                new SessionStoreContextImpl()
        );
        synchronousMaestro = new SynchronousMaestro(60, maestroProducer);
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
    void testMaestroValidationStep() throws ExecutionException, InterruptedException {
        List<TestRun> testRuns = getTestRunsWithOneValidationStep();
        TestReport report = synchronousMaestro.executeTestSet(testRuns);
        assertEquals(Result.UNDEFINED, report.getResult());
        List<TestRunReport> testRunReports = report.getTestRunReports();
        assertEquals(1, testRunReports.size());
        assertEquals(1, testRunReports.getFirst().getStepRunReports().size());
        assertEquals(1, report.getTestCounters().getTotal());
        assertEquals(1, report.getTestCounters().getUndefined());
    }

    @Test
    void testMaestroSimulationStep() throws ExecutionException, InterruptedException {
        List<TestRun> testRuns = getTestRunsWithOneSimulationStep();
        TestReport report = synchronousMaestro.executeTestSet(testRuns);
        assertEquals(Result.PASSED, report.getResult());
        List<TestRunReport> testRunReports = report.getTestRunReports();
        assertEquals(1, testRunReports.size());
        assertEquals(1, testRunReports.getFirst().getStepRunReports().size());
        assertEquals(1, report.getTestCounters().getTotal());
        assertEquals(1, report.getTestCounters().getPassed());
    }

    @Test
    void testMaestroValidationAndAssertionStep() throws ExecutionException, InterruptedException {
        List<TestRun> testRuns = getTestRunsWithValidationStepAndAssertionStepWithReference();
        TestReport report = synchronousMaestro.executeTestSet(testRuns);
        assertEquals(Result.UNDEFINED, report.getResult());
        List<TestRunReport> testRunReports = report.getTestRunReports();
        assertEquals(2, testRunReports.size());
        assertEquals(1, testRunReports.getFirst().getStepRunReports().size());
        assertEquals(1, testRunReports.getLast().getStepRunReports().size());
        assertEquals(2, report.getTestCounters().getTotal());
        assertEquals(1, report.getTestCounters().getPassed());
        assertEquals(1, report.getTestCounters().getUndefined());
    }

    @Test
    void testMaestroWithSeveralStepOnSameTestRun() throws ExecutionException, InterruptedException {
        List<TestRun> testRuns = getTestRunsWithOneValidationStep();
        Step validationStep = new ValidationStep("test-validation-two",
                new Property<>(ValidationStep.VALIDATION_SERVICE, "matchbox"),
                new Property<>(ValidationStep.VALIDATION_PROFILE, "Parameters"),
                new Property<>(ValidationStep.CONTENT_TO_VALIDATE, "liyvfgljv".getBytes())
        );
        testRuns.getFirst().getTest().addStep(validationStep);
        TestReport report = synchronousMaestro.executeTestSet(testRuns);
        assertEquals(Result.UNDEFINED, report.getResult());
        List<TestRunReport> testRunReports = report.getTestRunReports();
        assertEquals(1, testRunReports.size());
        assertEquals(2, testRunReports.getFirst().getStepRunReports().size());
        assertEquals(1, report.getTestCounters().getTotal());
        assertEquals(1, report.getTestCounters().getUndefined());

    }

    @Test
    void testMaestroWithInvalidTwoSameStepTestRun() throws ExecutionException, InterruptedException {
        List<TestRun> testRuns = getTestRunsWithOneValidationStep();
        Step validationStep = new ValidationStep("test-validation",
                new Property<>(ValidationStep.VALIDATION_SERVICE, "matchbox"),
                new Property<>(ValidationStep.VALIDATION_PROFILE, "Parameters"),
                new Property<>(ValidationStep.CONTENT_TO_VALIDATE, "liyvfgljv".getBytes())
        );
        testRuns.getFirst().getTest().addStep(validationStep);
        TestReport report = synchronousMaestro.executeTestSet(testRuns);
        assertEquals(Result.UNDEFINED, report.getResult());
        List<TestRunReport> testRunReports = report.getTestRunReports();
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
    void testMaestroWithNullTestRun() throws ExecutionException, InterruptedException {
        List<TestRun> testRuns = new ArrayList<>();
        testRuns.add(null);
        TestReport report = synchronousMaestro.executeTestSet(testRuns);
        assertNotNull(report);
        assertEquals(1, report.getUnexpectedErrors().size());
        assertEquals(IllegalArgumentException.class.getSimpleName(), report.getUnexpectedErrors().getFirst().getName());
    }

    private static List<TestRun> getTestRunsWithOneValidationStep() {
        List<TestRun> testRuns = new ArrayList<>();
        Step validationStep = new ValidationStep("test-validation",
                new Property<>(ValidationStep.VALIDATION_SERVICE, "matchbox"),
                new Property<>(ValidationStep.VALIDATION_PROFILE, "Parameters"),
                new Property<>(ValidationStep.CONTENT_TO_VALIDATE, "liyvfgljv".getBytes())
        );
        net.ihe.gazelle.maestro.api.business.testrun.Test test = new net.ihe.gazelle.maestro.api.business.testrun.Test().addStep(validationStep);
        test.setName("validationTest");
        test.setId(UUID.randomUUID().toString());
        TestRun testRun = new TestRun();
        testRun.setTest(test);
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
