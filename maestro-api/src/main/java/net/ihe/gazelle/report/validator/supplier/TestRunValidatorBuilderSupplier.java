package net.ihe.gazelle.report.validator.supplier;

import net.ihe.gazelle.framework.modelvalidator.domain.AbstractValidatorBuilderSupplier;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.report.model.Item;
import net.ihe.gazelle.report.model.Test;
import net.ihe.gazelle.report.model.TestRun;
import net.ihe.gazelle.report.model.UnexpectedError;

public class TestRunValidatorBuilderSupplier extends AbstractValidatorBuilderSupplier<TestRun> {

    public TestRunValidatorBuilderSupplier(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
    }

    @Override
    protected Class<? extends TestRun> getType() {
        return TestRun.class;
    }

    @Override
    protected ValidatorBuilder<TestRun> supplyRulesAndMembers(ValidatorBuilder<TestRun> validatorBuilder) {
        return validatorBuilder
                .addRule("dateTimeValid", TestRun::isDateTimeValid, "DateTime must be set and not in the future")
                .addRule("resultValid", TestRun::isResultValid, "Result must be set adn reflect global steps result")
                .addRule("testValid", TestRun::isTestValid, "Test must be set")
                .addRule("urlToTestRun", TestRun::isUrlToTestRunValid, "URL must be set and not empty if present")
                .addMember("test", TestRun::getTest, Test.class)
                .addMember("inputs", TestRun::getInputs, Item.class)
                .addMember("outputs", TestRun::getOutputs, Item.class)
                .addMember("unexpectedErrors", TestRun::getUnexpectedErrors, UnexpectedError.class);
    }
}
