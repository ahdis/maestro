package net.ihe.gazelle.maestro.quarkus.record.entity;

import net.ihe.gazelle.lang.UnexpectedError;
import net.ihe.gazelle.maestro.api.business.testreport.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestReportEntity {

    private final TestReport testReport;

    private List<TestReportReference> subReportsReferences = new ArrayList<>();

    public TestReportEntity() {
        this.testReport = new TestReport();
    }

    public TestReportEntity(TestReport testReport) {
        this.testReport = new TestReport(testReport);
    }


    public String getReportVersion() {
        return testReport.getReportVersion();
    }

    public TestReportEntity setReportVersion(String reportVersion) {
        this.testReport.setReportVersion(reportVersion);
        return this;
    }

    public String getUuid() {
        return testReport.getUuid();
    }

    public TestReportEntity setUuid(String uuid) {
        this.testReport.setUuid(uuid);
        return this;
    }

    public Date getDateTime() {
        return new Date(testReport.getDateTime().getTime());
    }

    public TestReportEntity setDateTime(Date dateTime) {
        this.testReport.setDateTime(new Date(dateTime.getTime()));
        return this;
    }

    public TestService getTestService() {
        return testReport.getTestService();
    }

    public TestReportEntity setTestService(TestService testService) {
        this.setTestService(testService);
        return this;
    }

    public List<SystemUnderTest> getSystemsUnderTest() {
        return new ArrayList<>(testReport.getSystemsUnderTest());
    }

    public TestReportEntity setSystemsUnderTest(List<SystemUnderTest> systemsUnderTest) {
        this.testReport.setSystemsUnderTest(new ArrayList<>(systemsUnderTest));
        return this;
    }

    public TestReportEntity addSystemUnderTest(SystemUnderTest systemUnderTest) {
        this.testReport.getSystemsUnderTest().add(systemUnderTest);
        return this;
    }

    public TestReportEntity addSystemsUnderTest(List<SystemUnderTest> systemsUnderTest) {
        this.testReport.getSystemsUnderTest().addAll(systemsUnderTest);
        return this;
    }

    public Result getResult() {
        return testReport.getResult();
    }

    public TestReportEntity setResult(Result result) {
        this.testReport.setResult(result);
        return this;
    }

    public String getTestSuiteName() {
        return testReport.getTestSuiteName();
    }

    public TestReportEntity setTestSuiteName(String testSuiteName) {
        this.testReport.setTestSuiteName(testSuiteName);
        return this;
    }

    public TestCounters getTestCounters() {
        return testReport.getTestCounters();
    }

    public TestReportEntity setTestCounters(TestCounters testCounters) {
        this.testReport.setTestCounters(testCounters);
        return this;
    }

    public String getNote() {
        return testReport.getNote();
    }

    public TestReportEntity setNote(String note) {
        this.testReport.setNote(note);
        return this;
    }

    public String getUrlToTestSuiteResult() {
        return testReport.getUrlToTestSuiteResult();
    }

    public TestReportEntity setUrlToTestSuiteResult(String urlToTestSuiteResult) {
        this.testReport.setUrlToTestSuiteResult(urlToTestSuiteResult);
        return this;
    }

    public List<TestReportReference> getSubReportsReferences() {
        return new ArrayList<>(subReportsReferences);
    }

    public TestReportEntity setSubReportsReferences(List<TestReportReference> subReports) {
        this.subReportsReferences = new ArrayList<>(subReports);
        return this;
    }


    public List<TestRunReport> getTestRunReports() {
        return new ArrayList<>(testReport.getTestRunReports());
    }

    public TestReportEntity setTestRunReports(List<TestRunReport> testRunReports) {
        this.testReport.setTestRunReports(new ArrayList<>(testRunReports));
        return this;
    }

    public List<UnexpectedError> getUnexpectedErrors() {
        return new ArrayList<>(testReport.getUnexpectedErrors());
    }

    public TestReportEntity setUnexpectedErrors(List<UnexpectedError> unexpectedErrors) {
        this.testReport.setUnexpectedErrors(new ArrayList<>(unexpectedErrors));
        return this;
    }

}
