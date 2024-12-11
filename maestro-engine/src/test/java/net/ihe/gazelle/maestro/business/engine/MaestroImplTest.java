package net.ihe.gazelle.maestro.business.engine;

import net.ihe.gazelle.framework.modelvalidator.interlay.adapter.BValidatorBuilderFactory;
import net.ihe.gazelle.maestro.Property;
import net.ihe.gazelle.maestro.PropertyRef;
import net.ihe.gazelle.maestro.Step;
import net.ihe.gazelle.maestro.TestRun;
import net.ihe.gazelle.maestro.business.step.assertion.AssertionStep;
import net.ihe.gazelle.maestro.business.step.validation.ValidationStep;
import net.ihe.gazelle.maestro.technical.SessionStoreContextImpl;
import net.ihe.gazelle.maestro.technical.StepRunnerProviderSPI;
import net.ihe.gazelle.maestro.technical.jobs.*;
import net.ihe.gazelle.maestro.technical.ws.tmintegration.TMTestReportClient;
import net.ihe.gazelle.maestro.technical.ws.tmintegration.TMTestRunMaestro;
import net.ihe.gazelle.testreport.Result;
import net.ihe.gazelle.testreport.TestReport;
import net.ihe.gazelle.testreport.TestRunReport;
import net.ihe.gazelle.testreport.UnexpectedError;
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
        TestRunMockEmitter testRunMockEmitter = new TestRunMockEmitter();
        StepMockEmitter stepMockEmitter = new StepMockEmitter();
        MaestroImpl maestro = new MaestroImpl(
                new BValidatorBuilderFactory(),
                new MicroprofileMaestroProducer(testRunMockEmitter, stepMockEmitter),
                new SessionStoreContextImpl()
        );
        synchronousMaestro = new SynchronousMaestro(testRunMockEmitter, 60);
        StepRunnerJob stepRunnerJob = new StepRunnerJob(
                new StepRunnerProviderSPI(new HandlerProviderMock()),
                stepMockEmitter
        );
        TMTestRunMaestro tmTestRunMaestro = new TMTestRunMaestro(testRunMockEmitter, new TMTestReportClient());
        MicroprofileListener microprofileListener = new MicroprofileListener(maestro, synchronousMaestro, stepRunnerJob, tmTestRunMaestro);
        testRunMockEmitter.addListener(microprofileListener);
        stepMockEmitter.addListener(microprofileListener);
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
    void testMaestroValidationAndAssertionStep() throws ExecutionException, InterruptedException {
        List<TestRun> testRuns = getTestRunsWithValidationStepAndAssertionStepWithReference();
        TestReport report = synchronousMaestro.executeTestSet(testRuns);
        assertEquals(Result.UNDEFINED, report.getResult());
        List<TestRunReport> testRunReports = report.getTestRunReports();
        assertEquals(2, testRunReports.size());
        assertEquals(1, testRunReports.getFirst().getStepRunReports().size());
        assertEquals(1, testRunReports.getLast().getStepRunReports().size());
        assertEquals(2, report.getTestCounters().getTotal());
        assertEquals(2, report.getTestCounters().getUndefined());
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
    void testMaestroWithInvalidTestRun() throws ExecutionException, InterruptedException {
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
        net.ihe.gazelle.testreport.Test test = testRunReport.getTest();
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
        net.ihe.gazelle.maestro.Test test = new net.ihe.gazelle.maestro.Test().addStep(validationStep);
        test.setName("validationTest");
        test.setId(UUID.randomUUID().toString());
        TestRun testRun = new TestRun();
        testRun.setTest(test);
        testRuns.add(testRun);
        return testRuns;
    }

    private static List<TestRun> getTestRunsWithValidationStepAndAssertionStepWithReference() {
        Step assertionStep = new AssertionStep("test-assertion",
                new PropertyRef<>(AssertionStep.ASSERTION, "assertion"),
                new Property<>(AssertionStep.ASSERTION_SERVICE, "toto")
        );
        net.ihe.gazelle.maestro.Test test = new net.ihe.gazelle.maestro.Test().addStep(assertionStep);
        test.setName("assertionTest");
        test.setId(UUID.randomUUID().toString());
        TestRun testRun = new TestRun();
        testRun.setTest(test);
        List<TestRun> result = getTestRunsWithOneValidationStep();
        result.add(testRun);
        return result;
    }


}
