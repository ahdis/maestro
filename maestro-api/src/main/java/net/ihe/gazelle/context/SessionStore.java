package net.ihe.gazelle.context;

import net.ihe.gazelle.test.Test;

import java.util.Map;

public class SessionStore {

    private Context context;
    private Test test;
    private Map<String, String> stepReports;

    public SessionStore(Context context, Map<String, String> stepReports) {
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

    public Map<String, String> getStepReports() {
        return stepReports;
    }

    public void setStepReports(Map<String, String> stepReports) {
        this.stepReports = stepReports;
    }
}
