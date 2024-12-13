package net.ihe.gazelle.maestro.technical.jobs.testrunevents;

import net.ihe.gazelle.maestro.TestSetRun;
import net.ihe.gazelle.testreport.TestReport;

/**
 * A message to notify that a TestSet Execution Ended and to send a TestReport
 */
public class FinishTestSetEvent implements TestRunEvent {

    private final TestReport testReport;
    private final TestSetRun testSetRun;

    public FinishTestSetEvent(TestSetRun testRuns, TestReport testReport) {
        this.testSetRun = testRuns;
        this.testReport = testReport;
    }

    public TestReport getTestReport() {
        return testReport;
    }

    public TestSetRun getTestSetRun() {
        return testSetRun;
    }
}
