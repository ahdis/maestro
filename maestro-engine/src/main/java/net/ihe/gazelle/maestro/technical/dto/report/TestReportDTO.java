package net.ihe.gazelle.maestro.technical.dto.report;

import com.fasterxml.jackson.annotation.*;
import net.ihe.gazelle.testreport.Result;
import net.ihe.gazelle.testreport.TestReport;

import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "modelVersion", "uuid", "dateTime", "testService", "systemsUnderTest", "result", "testSuiteName",
        "testCounters", "note", "urlToTestSuiteResult", "testRunReports", "subReports", "unexpectedErrors"
})
public class TestReportDTO {

    public static final String ISO_8601_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    @JsonIgnore
    private final TestReport domain;

    public TestReportDTO() {
        domain = new TestReport();
    }

    public TestReportDTO(TestReport domain) {
        this.domain = domain;
    }

    @JsonGetter(value = "reportVersion")
    public String getReportVersion() {
        return domain.getReportVersion();
    }

    @JsonSetter(value = "reportVersion")
    public void setReportVersion(String reportVersion) {
        // do nothing
    }

    @JsonGetter(value = "uuid")
    public String getUuid() {
        return domain.getUuid();
    }

    @JsonSetter(value = "uuid")
    public void setUuid(String uuid) {
        domain.setUuid(uuid);
    }

    @JsonGetter(value = "dateTime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ISO_8601_DATETIME_PATTERN) // ISO 8601
    public Date getDateTime() {
        return domain.getDateTime();
    }

    @JsonSetter(value = "dateTime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ISO_8601_DATETIME_PATTERN) // ISO 8601
    public void setDateTime(Date dateTime) {
        domain.setDateTime(dateTime);
    }

    @JsonGetter(value = "testService")
    public TestServiceDTO getTestService() {
        TestServiceDTO testServiceDTO = null;
        if (domain.getTestService() != null) {
            testServiceDTO = new TestServiceDTO(domain.getTestService());
        }
        return testServiceDTO;
    }

    @JsonSetter(value = "testService")
    public void setTestService(TestServiceDTO testServiceDTO) {
        domain.setTestService(testServiceDTO.getDomain());
    }

    @JsonGetter(value = "systemsUnderTest")
    public List<SystemUnderTestDTO> getSystemsUnderTest() {
        return domain.getSystemsUnderTest().stream()
                .map(SystemUnderTestDTO::new)
                .toList();
    }

    @JsonSetter(value = "systemsUnderTest")
    public void setSystemsUnderTest(List<SystemUnderTestDTO> systemsUnderTest) {
        domain.setSystemsUnderTest(systemsUnderTest.stream()
                .map(SystemUnderTestDTO::getDomain)
                .toList());
    }

    @JsonGetter(value = "result")
    public String getResult() {
        return domain.getResult().name();
    }

    @JsonSetter(value = "result")
    public void setResult(String result) {
        domain.setResult(Result.valueOf(result));
    }

    @JsonGetter(value = "testSuiteName")
    public String getTestSuiteName() {
        return domain.getTestSuiteName();
    }

    @JsonSetter(value = "testSuiteName")
    public void setTestSuiteName(String testSuiteName) {
        domain.setTestSuiteName(testSuiteName);
    }

    @JsonGetter(value = "testCounters")
    public TestCountersDTO getTestCounters() {
        return new TestCountersDTO(domain.getTestCounters());
    }

    @JsonSetter(value = "testCounters")
    public void setTestCounters(TestCountersDTO testCounters) {
        domain.setTestCounters(testCounters.getDomain());
    }

    @JsonGetter(value = "note")
    public String getNote() {
        return domain.getNote();
    }

    @JsonSetter(value = "note")
    public void setNote(String note) {
        domain.setNote(note);
    }

    @JsonGetter(value = "urlToTestSuiteResult")
    public String getUrlToTestSuiteResult() {
        return domain.getUrlToTestSuiteResult();
    }

    @JsonSetter(value = "urlToTestSuiteResult")
    public void setUrlToTestSuiteResult(String urlToTestSuiteResult) {
        domain.setUrlToTestSuiteResult(urlToTestSuiteResult);
    }

    @JsonGetter(value = "testRunReports")
    public List<TestRunReportDTO> getTestRunReports() {
        return domain.getTestRunReports().stream()
                .map(TestRunReportDTO::new)
                .toList();
    }

    @JsonSetter(value = "testRunReports")
    public void setTestRunReports(List<TestRunReportDTO> testRunReports) {
        domain.setTestRunReports(testRunReports.stream()
                .map(TestRunReportDTO::getDomain)
                .toList());
    }

    @JsonGetter(value = "subReports")
    public List<TestReportDTO> getSubReports() {
        return domain.getSubReports().stream()
                .map(TestReportDTO::new)
                .toList();
    }

    @JsonSetter(value = "subReports")
    public void setSubReports(List<TestReportDTO> subReports) {
        domain.setSubReports(subReports.stream()
                .map(TestReportDTO::getDomain)
                .toList());
    }

    @JsonGetter(value = "unexpectedErrors")
    public List<UnexpectedErrorDTO> getUnexpectedErrors() {
        return domain.getUnexpectedErrors().stream()
                .map(UnexpectedErrorDTO::new)
                .toList();
    }

    @JsonSetter(value = "unexpectedErrors")
    public void setUnexpectedErrors(List<UnexpectedErrorDTO> unexpectedErrors) {
        domain.setUnexpectedErrors(unexpectedErrors.stream()
                .map(UnexpectedErrorDTO::getDomain)
                .toList());
    }

    @JsonIgnore
    public TestReport getDomain() {
        return domain;
    }
}
