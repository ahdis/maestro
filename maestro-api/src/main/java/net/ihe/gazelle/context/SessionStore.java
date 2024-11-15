package net.ihe.gazelle.context;

import net.ihe.gazelle.report.model.StepRunReport;
import net.ihe.gazelle.test.Test;

import java.util.List;

public class SessionStore {

    private Context context;
    private Test test;
    private List<StepRunReport> stepReports;

    public SessionStore(Context context, List<StepRunReport> stepReports) {
        this.context = context;
        this.stepReports = stepReports;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public List<StepRunReport> getStepReports() {
        return stepReports;
    }

    public void setStepReports(List<StepRunReport> stepReports) {
        this.stepReports = stepReports;
    }
}
