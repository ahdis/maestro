package net.ihe.gazelle.maestro.api.business.testreport.validator;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.maestro.api.business.testreport.TestRunReport;

public class TestRunReportValidator extends AbstractValidator<TestRunReport> {

    private final TestValidator testValidator;
    private final ItemValidator itemValidator;
    private final StepRunReportValidator stepRunReportValidator;
    private final UnexpectedErrorValidator unexpectedErrorValidator;

    public TestRunReportValidator() {
        super();
        testValidator = new TestValidator();
        itemValidator = new ItemValidator();
        stepRunReportValidator = new StepRunReportValidator();
        unexpectedErrorValidator = new UnexpectedErrorValidator();
    }

    public TestRunReportValidator(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
        testValidator = new TestValidator();
        itemValidator = new ItemValidator();
        stepRunReportValidator = new StepRunReportValidator();
        unexpectedErrorValidator = new UnexpectedErrorValidator();
    }

    @Override
    protected ValidatorBuilder<? extends TestRunReport> instantiateValidatorBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        return validatorBuilderFactory.getBuilder(TestRunReport.class);
    }

    @Override
    protected void addRulesAndMembers(ValidatorBuilder<? extends TestRunReport> validatorBuilder) {
        validatorBuilder
                .addRule("dateTimeValid", TestRunReport::isDateTimeValid, "DateTime must be set and not in the future")
                .addRule("resultValid", TestRunReport::isResultValid, "Result must be set adn reflect global steps result")
                .addRule("testValid", TestRunReport::isTestValid, "Test must be set")
                .addRule("urlToTestRun", TestRunReport::isUrlToTestRunValid, "URL must be set and not empty if present")
                .addMember("test", TestRunReport::getTest, testValidator::getAssembledValidatorBuilder)
                .addMember("inputs", TestRunReport::getInputs, itemValidator::getAssembledValidatorBuilder)
                .addMember("outputs", TestRunReport::getOutputs, itemValidator::getAssembledValidatorBuilder)
                .addMember("stepRunReports", TestRunReport::getStepRunReports, stepRunReportValidator::getAssembledValidatorBuilder)
                .addMember("unexpectedErrors", TestRunReport::getUnexpectedErrors, unexpectedErrorValidator::getAssembledValidatorBuilder);
    }
}
