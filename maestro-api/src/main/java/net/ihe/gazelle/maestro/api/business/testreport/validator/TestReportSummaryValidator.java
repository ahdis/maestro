package net.ihe.gazelle.maestro.api.business.testreport.validator;

import net.ihe.gazelle.errorhandling.business.UnexpectedErrorValidator;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.maestro.api.business.testreport.TestReportSummary;

public class TestReportSummaryValidator extends AbstractValidator<TestReportSummary> {

    private final UnexpectedErrorValidator unexpectedErrorValidator;

    public TestReportSummaryValidator() {
        super();
        this.unexpectedErrorValidator = new UnexpectedErrorValidator();
    }

    public TestReportSummaryValidator(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
        this.unexpectedErrorValidator = new UnexpectedErrorValidator(validatorBuilderFactory);
    }

    @Override
    protected ValidatorBuilder<? extends TestReportSummary> instantiateValidatorBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        return validatorBuilderFactory.getBuilder(TestReportSummary.class);
    }

    @Override
    protected void addRulesAndMembers(ValidatorBuilder<? extends TestReportSummary> validatorBuilder) {
        validatorBuilder
                .addRule("idValid", TestReportSummary::isIdDefined, "Id must be set and not empty")
                .addRule("dateTimeValid", TestReportSummary::isDateTimeDefined, "DateTime must be set and not in the future")
                .addRule("resultValid", TestReportSummary::isResultDefined, "Result must be set and reflect the global result of the test testreport")
                .addMember("unexpectedErrors", TestReportSummary::getUnexpectedErrors, unexpectedErrorValidator::getAssembledValidatorBuilder)
                ;

    }
}
