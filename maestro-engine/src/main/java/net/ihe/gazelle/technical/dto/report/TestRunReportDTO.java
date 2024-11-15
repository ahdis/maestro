package net.ihe.gazelle.technical.dto.report;

import com.fasterxml.jackson.annotation.*;
import net.ihe.gazelle.report.model.TestRunReport;

import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "runId", "dateTime", "result", "test", "inputs", "outputs", "stepRunReports", "urlToTestRun", "unexpectedErrors"
})
public record TestRunReportDTO(TestRunReport domain) {

    public static final String ISO_8601_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    @JsonProperty(value = "runId")
    public String getRunId() {
        return domain.getRunId();
    }

    @JsonProperty(value = "dateTime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ISO_8601_DATETIME_PATTERN)
    public Date getDateTime() {
        return domain.getDateTime();
    }

    @JsonProperty(value = "result")
    public String result() {
        return domain.getResult().name();
    }

    @JsonProperty(value = "test")
    public TestDTO test() {
        return new TestDTO(domain.getTest());
    }

    @JsonProperty(value = "inputs")
    public List<ItemDTO> getInputs() {
        return domain.getInputs().stream()
                .map(ItemDTO::new)
                .toList();
    }

    @JsonProperty(value = "outputs")
    public List<ItemDTO> getOutputs() {
        return domain.getOutputs().stream()
                .map(ItemDTO::new)
                .toList();
    }

    @JsonProperty(value = "stepRunReports")
    public List<StepRunReportDTO> getStepRunReports() {
        return domain.getStepRunReports().stream()
                .map(StepRunReportDTO::new)
                .toList();
    }

    @JsonProperty(value = "urlToTestRun")
    public String getUrlToTestRun() {
        return domain.getUrlToTestRun();
    }

    @JsonProperty(value = "unexpectedErrors")
    public List<UnexpectedErrorDTO> getUnexpectedErrors() {
        return domain.getUnexpectedErrors().stream()
                .map(UnexpectedErrorDTO::new)
                .toList();
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
    public boolean isTestValid() {
        return domain.isTestValid();
    }

    @JsonIgnore
    public boolean isUrlToTestRun() {
        return domain.isUrlToTestRunValid();
    }

    @JsonIgnore
    public boolean isValid() {
        return domain.isValid();
    }
}
