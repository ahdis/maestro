package net.ihe.gazelle.maestro.api.business.testreport;

import net.ihe.gazelle.errorhandling.business.UnexpectedError;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestReportSummary {

    private String id;
    private Date dateTime;
    private Result result;
    private List<UnexpectedError> unexpectedErrors;

    public TestReportSummary() {
        this.unexpectedErrors = new ArrayList<>();
    }

    public TestReportSummary(String id, Date dateTime, Result result) {
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

    public Date getDateTime() {
        return dateTime;
    }

    public TestReportSummary setDateTime(Date dateTime) {
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

    public boolean isIdValid() {
        return id != null && !id.isBlank();
    }

    public boolean isDateTimeValid() {
        return dateTime != null && !dateTime.after(new Date());
    }

    public boolean isResultValid() {
        return result != null;
    }

    public boolean isValid() {
        return isIdValid() && isDateTimeValid() && isResultValid();
    }


}
