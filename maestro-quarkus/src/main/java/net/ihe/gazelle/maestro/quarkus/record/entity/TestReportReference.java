package net.ihe.gazelle.maestro.quarkus.record.entity;

import net.ihe.gazelle.maestro.api.business.testreport.Result;

public class TestReportReference {

    private String testReportId;
    private Result result;
    private String testSuiteName;

    public String getTestReportId() {
        return testReportId;
    }

    public void setTestReportId(String testReportId) {
        this.testReportId = testReportId;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getTestSuiteName() {
        return testSuiteName;
    }

    public void setTestSuiteName(String testSuiteName) {
        this.testSuiteName = testSuiteName;
    }
}
