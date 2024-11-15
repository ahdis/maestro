package net.ihe.gazelle.report.validator.supplier;

import net.ihe.gazelle.framework.modelvalidator.domain.AbstractValidatorBuilderSupplier;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.report.model.TestCounters;

public class TestCountersValidatorBuilderSupplier extends AbstractValidatorBuilderSupplier<TestCounters> {

    public TestCountersValidatorBuilderSupplier(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
    }

    @Override
    protected Class<? extends TestCounters> getType() {
        return TestCounters.class;
    }

    @Override
    protected ValidatorBuilder<TestCounters> supplyRulesAndMembers(ValidatorBuilder<TestCounters> validatorBuilder) {
        return validatorBuilder
                .addRule("totalValid", TestCounters::isTotalValid, "Number of total test must be positive and equals to the sum of passed, failed and undefined")
                .addRule("passedValid", TestCounters::isPassedValid, "Number of passed test must be positive")
                .addRule("failedValid", TestCounters::isFailedValid, "Number failed test must be positive")
                .addRule("undefinedValid", TestCounters::isUndefinedValid, "Number undefined test must be positive")
                .addRule("unexpectedErrorsValid", TestCounters::isUnexpectedErrorsValid, "Number of unexpectedErrors must be positive");
    }
}
