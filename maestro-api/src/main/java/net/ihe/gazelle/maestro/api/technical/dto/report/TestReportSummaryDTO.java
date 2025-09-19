package net.ihe.gazelle.maestro.api.technical.dto.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.ihe.gazelle.maestro.api.business.testreport.Result;
import net.ihe.gazelle.maestro.api.business.testreport.TestReportSummary;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestReportSummaryDTO {

    private final TestReportSummary domain;

    private String location;

    private List<UnexpectedErrorDTO> unexpectedErrors = new ArrayList<>();


    public TestReportSummaryDTO() {
        this.domain = new TestReportSummary();
    }

    public TestReportSummaryDTO(TestReportSummary domain, String locationPrefix) {
        this.domain = domain;
        if (domain != null && domain.getId() != null) {
            this.location = locationPrefix + "/" + domain.getId();
        }
    }

    @JsonProperty("id")
    public String getId() {
        return domain.getId();
    }

    public TestReportSummaryDTO setId(String id) {
        domain.setId(id);
        return this;
    }

    @JsonProperty("dateTime")
    public Date getDateTime() {
        return domain.getDateTime();
    }

    public TestReportSummaryDTO setDateTime(Date datetime) {
        domain.setDateTime(datetime);
        return this;
    }

    @JsonProperty("result")
    public Result getResult() {
        return domain.getResult();
    }

    public TestReportSummaryDTO setResult(Result result) {
        domain.setResult(result);
        return this;
    }

    @JsonProperty("location")
    public String getLocation() {
        return location;
    }

    public TestReportSummaryDTO setLocation(String location) {
        this.location = location;
        return this;
    }

    public List<UnexpectedErrorDTO> getUnexpectedErrors() {
        return unexpectedErrors;
    }

    public TestReportSummaryDTO setUnexpectedErrors(List<UnexpectedErrorDTO> unexpectedErrors) {
        this.unexpectedErrors = unexpectedErrors;
        return this;
    }

    @JsonIgnore
    public TestReportSummary getDomain() {
        return domain
                .setUnexpectedErrors(unexpectedErrors.stream().map(UnexpectedErrorDTO::getDomain).toList())
                ;
    }

}
