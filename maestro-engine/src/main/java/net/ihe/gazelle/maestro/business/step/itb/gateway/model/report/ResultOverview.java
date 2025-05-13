package net.ihe.gazelle.maestro.business.step.itb.gateway.model.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Model for a summary of all the test cases results
 */
public class ResultOverview implements Serializable {
    private String testSuiteName;

    private TestResultRecap testResultRecap;

    private List<Text> notes;

    private String urlToTestSuiteResult;

    public ResultOverview() {
        this.testSuiteName = "Test suite";
        this.testResultRecap = new TestResultRecap();
        this.notes = new ArrayList<>();
    }

    public ResultOverview(String testSuiteName, TestResultRecap testResultRecap) {
        this.testSuiteName = testSuiteName;
        this.testResultRecap = testResultRecap;
        this.notes = new ArrayList<>();
    }

    public ResultOverview(String testSuiteName, TestResultRecap testResultRecap, List<Text> notes, String urlToTestSuiteResult) {
        this.testSuiteName = testSuiteName;
        this.testResultRecap = testResultRecap;
        this.notes = notes;
        this.urlToTestSuiteResult = urlToTestSuiteResult;
    }

    public String getTestSuiteName() {
        return testSuiteName;
    }

    public void setTestSuiteName(String testSuiteName) {
        this.testSuiteName = testSuiteName;
    }

    public TestResultRecap getTestResultRecap() {
        return testResultRecap;
    }

    public void setTestResultRecap(TestResultRecap testResultRecap) {
        this.testResultRecap = testResultRecap;
    }

    public List<Text> getNotes() {
        return notes;
    }

    public void setNotes(List<Text> notes) {
        this.notes = notes;
    }

    public String getUrlToTestSuiteResult() {
        return urlToTestSuiteResult;
    }

    public void setUrlToTestSuiteResult(String urlToTestSuiteResult) {
        this.urlToTestSuiteResult = urlToTestSuiteResult;
    }
}
