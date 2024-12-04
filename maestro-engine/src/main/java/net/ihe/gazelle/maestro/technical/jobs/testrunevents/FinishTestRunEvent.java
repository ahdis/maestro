package net.ihe.gazelle.maestro.technical.jobs.testrunevents;

import net.ihe.gazelle.testreport.TestRunReportBuilder;
import net.ihe.gazelle.maestro.TestRun;

/**
 * A Message to notify that a TestRun execution ended and to send a TestRunReportBuilder
 */
public class FinishTestRunEvent implements TestRunEvent {

    private final TestRunReportBuilder testRunReportBuilder;
    private final TestRun testRun;

    public FinishTestRunEvent(TestRun testRun, TestRunReportBuilder testReport) {
        this.testRun = testRun;
        this.testRunReportBuilder = testReport;
    }

    public TestRunReportBuilder getTestRunReportBuilder() {
        return testRunReportBuilder;
    }

    public TestRun getTestRun() {
        return testRun;
    }
}