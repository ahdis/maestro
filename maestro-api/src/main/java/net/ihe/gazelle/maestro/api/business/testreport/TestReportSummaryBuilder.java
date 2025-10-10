package net.ihe.gazelle.maestro.api.business.testreport;

import net.ihe.gazelle.errorhandling.business.UnexpectedErrorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.maestro.api.business.testreport.validator.TestReportSummaryValidator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestReportSummaryBuilder extends AbstractBuilder<TestReportSummary> {

    private String id;
    private Date dateTime;
    private Result result;
    private List<UnexpectedErrorBuilder> unexpectedErrors = new ArrayList<>();

    public TestReportSummaryBuilder() {
        super();
    }

    public TestReportSummaryBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public TestReportSummaryBuilder setDateTime(Date datetime) {
        this.dateTime = datetime;
        return this;
    }

    public TestReportSummaryBuilder setResult(Result result) {
        this.result = result;
        return this;
    }

    public TestReportSummaryBuilder setUnexpectedErrors(List<UnexpectedErrorBuilder> unexpectedErrors) {
        this.unexpectedErrors = unexpectedErrors;
        return this;
    }

    public TestReportSummaryBuilder addUnexpectedError(UnexpectedErrorBuilder unexpectedError) {
        this.unexpectedErrors.add(unexpectedError);
        return this;
    }

    public TestReportSummaryBuilder addUnexpectedErrors(List<UnexpectedErrorBuilder> unexpectedErrors) {
        this.unexpectedErrors.addAll(unexpectedErrors);
        return this;
    }

    @Override
    protected AbstractValidator<TestReportSummary> instantiateValidator(ValidatorBuilderFactory validatorBuilderFactory) {
        return new TestReportSummaryValidator(validatorBuilderFactory);
    }

    @Override
    protected TestReportSummary instantiate() {
        return new TestReportSummary();
    }

    @Override
    protected void make(TestReportSummary testReportSummary) {
        testReportSummary
                .setId(id)
                .setDateTime(dateTime)
                .setResult(result);
    }
}
