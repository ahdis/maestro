package net.ihe.gazelle.technical.dto.report;

import com.fasterxml.jackson.annotation.*;
import net.ihe.gazelle.report.model.TestReport;

import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "modelVersion", "uuid", "dateTime", "testService", "systemsUnderTest", "result", "testSuiteName",
        "testCounters", "note", "urlToTestSuiteResult", "subReports", "testRuns", "unexpectedErrors"
})
public record TestReportDTO(TestReport domain) {

    public static final String ISO_8601_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    @JsonProperty(value = "reportVersion")
    public String getReportVersion() {
        return domain.getReportVersion();
    }

    @JsonProperty(value = "uuid")
    public String getUuid() {
        return domain.getUuid();
    }

    @JsonProperty(value = "dateTime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ISO_8601_DATETIME_PATTERN) // ISO 8601
    public Date getDateTime() {
        return domain.getDateTime();
    }

    @JsonProperty(value = "testService")
    public TestServiceDTO getTestService() {
        TestServiceDTO testServiceDTO = null;
        if (domain.getTestService() != null) {
            testServiceDTO = new TestServiceDTO(domain.getTestService());
        }
        return testServiceDTO;
    }

    @JsonProperty(value = "systemsUnderTest")
    public List<SystemUnderTestDTO> getInputs() {
        return domain.getSystemsUnderTest().stream()
                .map(SystemUnderTestDTO::new)
                .toList();
    }

    @JsonProperty(value = "result")
    public String result() {
        return domain.getResult().name();
    }

    @JsonProperty(value = "testSuiteName")
    public String getTestSuiteName() {
        return domain.getTestSuiteName();
    }

    @JsonProperty(value = "testCounters")
    public TestCountersDTO getTestCounters() {
        return new TestCountersDTO(domain.getTestCounters());
    }

    @JsonProperty(value = "note")
    public String getNote() {
        return domain.getNote();
    }

    @JsonProperty(value = "urlToTestSuiteResult")
    public String getUrlToTestSuiteResult() {
        return domain.getUrlToTestSuiteResult();
    }

    @JsonProperty(value = "subReports")
    public List<TestReportDTO> getSubReports() {
        return domain.getSubReports().stream()
                .map(TestReportDTO::new)
                .toList();
    }

    @JsonProperty(value = "testRuns")
    public List<TestRunDTO> getTestRuns() {
        return domain.getTestRuns().stream()
                .map(TestRunDTO::new)
                .toList();
    }

    @JsonProperty(value = "unexpectedErrors")
    public List<UnexpectedErrorDTO> getUnexpectedErrors() {
        return domain.getUnexpectedErrors().stream()
                .map(UnexpectedErrorDTO::new)
                .toList();
    }

    @JsonIgnore
    public boolean isUuidValid() {
        return domain.isUuidValid();
    }

    @JsonIgnore
    public boolean isDateTimeValid() {
        return domain.isDateTimeValid();
    }

    @JsonIgnore
    public boolean isResultValid() {
        return domain.isResultValid();
    }

    @JsonIgnore
    public boolean isTestSuiteNameValid() {
        return domain.isTestSuiteNameValid();
    }

    @JsonIgnore
    public boolean isTestCountersValid() {
        return domain.isTestCountersValid();
    }

    @JsonIgnore
    public boolean isNoteValid() {
        return domain.isNoteValid();
    }

    @JsonIgnore
    public boolean isUrlToTestSuiteResultValid() {
        return domain.isUrlToTestSuiteResultValid();
    }

    @JsonIgnore
    public boolean hasAtLeastOneResult() {
        return domain.hasAtLeastOneResult();
    }

    @JsonIgnore
    public boolean isValid() {
        return domain.isValid();
    }
}
