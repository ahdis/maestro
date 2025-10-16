package net.ihe.gazelle.maestro.api.business.testrun.validator;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.maestro.api.business.test.Test;

public class TestValidator extends AbstractValidator<Test> {

    @Override
    protected ValidatorBuilder<? extends Test> instantiateValidatorBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        return validatorBuilderFactory.getBuilder(Test.class);
    }

    @Override
    protected void addRulesAndMembers(ValidatorBuilder<? extends Test> validatorBuilder) {
        validatorBuilder
                .addRule(Test::isIdDefined, "ID must be set")
                .addRule(Test::isNameDefined, "Name must be set and not empty")
                .addRule(Test::atLeastOneStep, "Test must contain at least one step")
                .addRule(Test::areStepsUnique, "Steps must be unique")
        ;
    }
}
