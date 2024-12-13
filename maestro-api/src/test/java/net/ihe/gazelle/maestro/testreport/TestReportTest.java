package net.ihe.gazelle.maestro.testreport;

import net.ihe.gazelle.testreport.*;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestReportTest {

    @Test
    void testReport_valid() {
        TestReport testReport = buildTestReport();
        assertEquals("1.0", testReport.getReportVersion());
        assertEquals("service", testReport.getTestService().getServiceIdentification().getName());
        assertEquals(1, testReport.getSystemsUnderTest().size());
        assertEquals(1, testReport.getTestRunReports().size());
        assertEquals(Result.PASSED, testReport.getResult());
        assertTrue(testReport.isValid());
    }

    @Test
    void testReport_compute_test_runs_valid() {
        TestReport testReport = buildTestReport();
        assertEquals(Result.PASSED, testReport.getResult());
        assertEquals(1, testReport.getTestCounters().getTotal());
        TestRunReport testRunReport1 = buildTestRun();
        testRunReport1.addStepRunReport(new StepRunReport("id", "type", StepResult.FAILED));
        testRunReport1.computeResult();
        testReport.addTestRunReport(testRunReport1);
        testReport.computeResult();
        testReport.computeCounters();
        assertEquals(Result.FAILED, testReport.getResult());
        assertEquals(2, testReport.getTestCounters().getTotal());
        TestRunReport testRunReport2 = buildTestRun();
        testRunReport2.addStepRunReport(new StepRunReport("id", "type", StepResult.UNDEFINED));
        testRunReport2.computeResult();
        testReport.addTestRunReport(testRunReport2);
        testReport.computeResult();
        testReport.computeCounters();
        assertEquals(Result.UNDEFINED, testReport.getResult());
        assertEquals(3, testReport.getTestCounters().getTotal());
        assertEquals(1, testReport.getTestCounters().getFailed());
        assertEquals(1, testReport.getTestCounters().getPassed());
        assertEquals(1, testReport.getTestCounters().getUndefined());
    }

    @Test
    void testReport_compute_sub_reports_valid() {
        TestReport testReport = buildTestReport();
        TestRunReport testRunReport = buildTestRun();
        testRunReport.addStepRunReport(new StepRunReport("id", "type", StepResult.FAILED));
        testRunReport.computeResult();
        TestReport subReport = buildTestReport();
        subReport.addTestRunReport(testRunReport);
        subReport.computeResult();
        subReport.computeCounters();
        testReport.addSubReport(subReport);
        testReport.computeResult();
        testReport.computeCounters();
        assertEquals(Result.FAILED, testReport.getResult());
        assertEquals(3, testReport.getTestCounters().getTotal());
        assertEquals(2, testReport.getTestCounters().getPassed());
        assertEquals(1, testReport.getTestCounters().getFailed());
        TestRunReport testRunReport2 = buildTestRun();
        testRunReport2.addStepRunReport(new StepRunReport("id", "type", StepResult.UNDEFINED));
        testRunReport2.computeResult();
        TestReport subReport2 = buildTestReport();
        subReport2.addTestRunReport(testRunReport2);
        subReport2.computeResult();
        subReport2.computeCounters();
        testReport.addSubReport(subReport2);
        testReport.computeResult();
        testReport.computeCounters();
        assertEquals(1, testReport.getTestCounters().getUndefined());
        assertEquals(Result.UNDEFINED, testReport.getResult());
    }

    @Test
    void testReport_compute_sub_with_error() {
        TestReport testReport = buildTestReport();
        assertEquals(Result.PASSED, testReport.getResult());
        TestRunReport testRunReport = buildTestRun();
        testRunReport.addUnexpectedError(new UnexpectedError().setName("name").setMessage("message"));
        testRunReport.computeResult();
        TestReport subReport = buildTestReport();
        subReport.addTestRunReport(testRunReport);
        subReport.computeResult();
        subReport.computeCounters();
        testReport.addSubReport(subReport);
        testReport.computeResult();
        testReport.computeCounters();
        assertEquals(Result.UNDEFINED, testReport.getResult());
        assertEquals(1, testReport.getTestCounters().getUnexpectedErrors());
    }

    private TestReport buildTestReport() {
        TestReport testReport = new TestReport()
                .setReportVersion("1.0")
                .setUuid(UUID.randomUUID().toString())
                .setDateTime(new Date())
                .setTestService(
                        new TestService()
                                .setServiceIdentification(new EntityIdentification("service"))
                                .setDisclaimer("disclaimer")
                )
                .addSystemUnderTest(
                        new SystemUnderTest()
                                .setSystemIdentification(new EntityIdentification("system"))
                )
                .setTestSuiteName("testSuite")
                .setTestCounters(new TestCounters())
                .setNote("note")
                .setUrlToTestSuiteResult("urlToTestSuiteResult")
                .addTestRunReport(buildTestRun());
        testReport.computeCounters();
        testReport.computeResult();
        return testReport;
    }

    private TestRunReport buildTestRun() {
        TestRunReport testRunReport = new TestRunReport()
                .setRunId("id")
                .setDateTime(new Date())
                .setTest(new net.ihe.gazelle.testreport.Test("id"))
                .addInput(new Item("input", "reference"))
                .addOutput(new Item("output", "reference"))
                .addStepRunReport(new StepRunReport("id", "type", StepResult.PASSED))
                .setUrlToTestRun("urlToTestRun");
        testRunReport.computeResult();
        return testRunReport;
    }
}
