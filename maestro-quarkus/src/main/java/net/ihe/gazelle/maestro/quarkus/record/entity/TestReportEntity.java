package net.ihe.gazelle.maestro.quarkus.record.entity;

import net.ihe.gazelle.errorhandling.technical.UnexpectedErrorDTO;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.api.technical.dto.report.*;

import java.util.ArrayList;
import java.util.List;

public class TestReportEntity {

    private final TestReportDTO testReport;

    private List<TestReportReference> subReportsReferences = new ArrayList<>();

    public TestReportEntity() {
        this.testReport = new TestReportDTO();
    }

    public TestReportEntity(TestReport testReport) {
        this.testReport = new TestReportDTO(testReport);
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

    public String getDateTime() {
        return testReport.getDateTime();
    }

    public TestReportEntity setDateTime(String dateTime) {
        this.testReport.setDateTime(dateTime);
        return this;
    }

    public TestServiceDTO getTestService() {
        return testReport.getTestService();
    }

    public TestReportEntity setTestService(TestServiceDTO testService) {
        this.testReport.setTestService(testService);
        return this;
    }

    public List<SystemUnderTestDTO> getSystemsUnderTest() {
        return new ArrayList<>(testReport.getSystemsUnderTest());
    }

    public TestReportEntity setSystemsUnderTest(List<SystemUnderTestDTO> systemsUnderTest) {
        this.testReport.setSystemsUnderTest(new ArrayList<>(systemsUnderTest));
        return this;
    }

    public TestReportEntity addSystemUnderTest(SystemUnderTestDTO systemUnderTest) {
        this.testReport.getSystemsUnderTest().add(systemUnderTest);
        return this;
    }

    public TestReportEntity addSystemsUnderTest(List<SystemUnderTestDTO> systemsUnderTest) {
        this.testReport.getSystemsUnderTest().addAll(systemsUnderTest);
        return this;
    }

    public String getResult() {
        return testReport.getResult();
    }

    public TestReportEntity setResult(String result) {
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

    public TestCountersDTO getTestCounters() {
        return testReport.getTestCounters();
    }

    public TestReportEntity setTestCounters(TestCountersDTO testCounters) {
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


    public List<TestRunReportDTO> getTestRunReports() {
        return new ArrayList<>(testReport.getTestRunReports());
    }

    public TestReportEntity setTestRunReports(List<TestRunReportDTO> testRunReports) {
        this.testReport.setTestRunReports(new ArrayList<>(testRunReports));
        return this;
    }

    public List<UnexpectedErrorDTO> getUnexpectedErrors() {
        return new ArrayList<>(testReport.getUnexpectedErrors());
    }

    public TestReportEntity setUnexpectedErrors(List<UnexpectedErrorDTO> unexpectedErrors) {
        this.testReport.setUnexpectedErrors(new ArrayList<>(unexpectedErrors));
        return this;
    }

}
