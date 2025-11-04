package net.ihe.gazelle.maestro.api.business.testreport;

import net.ihe.gazelle.errorhandling.business.UnexpectedError;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TestReportSummary implements Serializable {

    @Serial
    private static final long serialVersionUID = -1344193617744601144L;

    private String id;
    private String location;
    private Instant dateTime;
    private Result result;
    private List<UnexpectedError> unexpectedErrors;

    public TestReportSummary() {
        this.unexpectedErrors = new ArrayList<>();
    }

    public TestReportSummary(String id, Instant dateTime, Result result) {
        this.id = id;
        this.dateTime = dateTime;
        this.result = result;
        this.unexpectedErrors = new ArrayList<>();
    }

    public TestReportSummary(TestReport testReport) {
        this.dateTime = testReport.getDateTime();
        this.result = testReport.getResult();
        this.unexpectedErrors = new ArrayList<>(testReport.getUnexpectedErrors());
    }


    public String getId() {
        return id;
    }

    public TestReportSummary setId(String id) {
        this.id = id;
        return this;
    }

   public String getLocation() {
      return location;
   }

   public TestReportSummary setLocation(String location) {
      this.location = location;
      return this;
   }

   public Instant getDateTime() {
        return dateTime;
    }

    public TestReportSummary setDateTime(Instant dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public Result getResult() {
        return result;
    }

    public TestReportSummary setResult(Result result) {
        this.result = result;
        return this;
    }

    public List<UnexpectedError> getUnexpectedErrors() {
        return unexpectedErrors;
    }

    public TestReportSummary setUnexpectedErrors(List<UnexpectedError> unexpectedErrors) {
        this.unexpectedErrors = unexpectedErrors;
        return this;
    }

    public TestReportSummary addUnexpectedError(UnexpectedError unexpectedError) {
        this.unexpectedErrors.add(unexpectedError);
        return this;
    }

    public boolean isIdDefined() {
        return id != null && !id.isBlank();
    }

    public boolean isDateTimeDefined() {
        return dateTime != null && !dateTime.isAfter(Instant.now());
    }

    public boolean isResultDefined() {
        return result != null;
    }

}
