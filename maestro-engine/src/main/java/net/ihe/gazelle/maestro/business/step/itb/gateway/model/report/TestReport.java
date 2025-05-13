package net.ihe.gazelle.maestro.business.step.itb.gateway.model.report;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Model for a Gazelle test report
 */
public class TestReport implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String uuid;

    private String date;

    private String result;

    private TestService testService;

    private SystemUnderTest sut;

    private ResultOverview overview;

    private List<Result> testRuns;

    public TestReport(String uuid, String date, String result) {
        this.uuid = uuid;
        this.date = date;
        this.result = result;
        this.testService = new TestService();
        this.sut = new SystemUnderTest();
        this.overview = new ResultOverview();
        this.testRuns = new ArrayList<>();
    }

    public TestReport(String uuid, String date) {
        this.uuid = uuid;
        this.date = date;
        this.testService = new TestService();
        this.sut = new SystemUnderTest();
        this.overview = new ResultOverview();
        this.testRuns = new ArrayList<>();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public TestService getTestService() {
        return testService;
    }

    public void setTestService(TestService testService) {
        this.testService = testService;
    }

    public SystemUnderTest getSut() {
        return sut;
    }

    public void setSut(SystemUnderTest sut) {
        this.sut = sut;
    }

    public ResultOverview getOverview() {
        return overview;
    }

    public void setOverview(ResultOverview overview) {
        this.overview = overview;
    }

    public List<Result> getTestRuns() {
        return testRuns;
    }

    public void setTestRuns(List<Result> testRuns) {
        this.testRuns = testRuns;
    }
}
