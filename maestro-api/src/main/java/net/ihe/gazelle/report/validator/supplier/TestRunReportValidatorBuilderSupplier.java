package net.ihe.gazelle.report.validator.supplier;

import net.ihe.gazelle.framework.modelvalidator.domain.AbstractValidatorBuilderSupplier;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.report.model.*;

public class TestRunReportValidatorBuilderSupplier extends AbstractValidatorBuilderSupplier<TestRunReport> {

    public TestRunReportValidatorBuilderSupplier(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
    }

    @Override
    protected Class<? extends TestRunReport> getType() {
        return TestRunReport.class;
    }

    @Override
    protected ValidatorBuilder<TestRunReport> supplyRulesAndMembers(ValidatorBuilder<TestRunReport> validatorBuilder) {
        return validatorBuilder
                .addRule("dateTimeValid", TestRunReport::isDateTimeValid, "DateTime must be set and not in the future")
                .addRule("resultValid", TestRunReport::isResultValid, "Result must be set adn reflect global steps result")
                .addRule("testValid", TestRunReport::isTestValid, "Test must be set")
                .addRule("urlToTestRun", TestRunReport::isUrlToTestRunValid, "URL must be set and not empty if present")
                .addMember("test", TestRunReport::getTest, Test.class)
                .addMember("inputs", TestRunReport::getInputs, Item.class)
                .addMember("outputs", TestRunReport::getOutputs, Item.class)
                .addMember("stepRunReports", TestRunReport::getStepRunReports, StepRunReport.class)
                .addMember("unexpectedErrors", TestRunReport::getUnexpectedErrors, UnexpectedError.class);
    }
}
