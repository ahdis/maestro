package net.ihe.gazelle.maestro.api.technical.dto.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import net.ihe.gazelle.maestro.api.business.testreport.Result;
import net.ihe.gazelle.maestro.api.business.testreport.TestReportSummary;
import net.ihe.gazelle.modelmarshaller.technical.dto.DTO;

import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "id",
        "dateTime",
        "result",
        "location",
        "unexpectedErrors"
})
public class TestReportSummaryDTO implements DTO<TestReportSummary> {

    private final TestReportSummary testReportSummary;

    public TestReportSummaryDTO() {
        this.testReportSummary = new TestReportSummary();
    }

    public TestReportSummaryDTO(TestReportSummary testReportSummary) {
        this.testReportSummary = testReportSummary;
    }

    @Override
    @JsonIgnore
    public TestReportSummary getBusinessObject() {
        return testReportSummary;
    }

    @JsonProperty("id")
    public String getId() {
        return getBusinessObject().getId();
    }

    public void setId(String id) {
        getBusinessObject().setId(id);
    }

    @JsonProperty("dateTime")
    public Date getDateTime() {
        return getBusinessObject().getDateTime();
    }

    public void setDateTime(Date datetime) {
        getBusinessObject().setDateTime(datetime);
    }

    @JsonProperty("result")
    public Result getResult() {
        return getBusinessObject().getResult();
    }

    public void setResult(Result result) {
        getBusinessObject().setResult(result);
    }

    @JsonProperty("location")
    public String getLocation() {
        return getBusinessObject().getLocation();
    }

    public void setLocation(String location) {
        getBusinessObject().setLocation(location);
    }

    @JsonProperty("unexpectedErrors")
    public List<UnexpectedErrorDTO> getUnexpectedErrors() {
        return getBusinessObject().getUnexpectedErrors().stream().map(UnexpectedErrorDTO::new).toList();
    }

    public void setUnexpectedErrors(List<UnexpectedErrorDTO> unexpectedErrors) {
        getBusinessObject().setUnexpectedErrors(
                unexpectedErrors.stream().map(UnexpectedErrorDTO::getBusinessObject).toList()
        );
    }
}
