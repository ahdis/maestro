package net.ihe.gazelle.maestro.api.business.testrun.context;

import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;

import java.util.ArrayList;
import java.util.List;

/**
 * TestRunSessionsStore is a structure used to store TestRun's context
 * and StepRunReportBuilder of executed step during TestRun execution.
 * It also contains a reference to the TestRun.
 */
public class TestRunSessionStore {

    private Context context;
    private TestRun testRun;
    private List<StepRunReportBuilder> stepReports;

    public TestRunSessionStore(Context context, TestRun testRun, List<StepRunReportBuilder> stepReports) {
        this.context = context;
        this.testRun = testRun;
        this.stepReports = new ArrayList<>(stepReports);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public TestRun getTestRun() {
        return testRun;
    }

    public void setTestRun(TestRun testRun) {
        this.testRun = testRun;
    }

    public List<StepRunReportBuilder> getStepReports() {
        return new ArrayList<>(stepReports);
    }

    public void setStepReports(List<StepRunReportBuilder> stepReports) {
        this.stepReports = new ArrayList<>(stepReports);
    }

    public void addStepRunReport(StepRunReportBuilder stepReportBuilder) {
        stepReports.add(stepReportBuilder);
    }
}
