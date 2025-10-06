package net.ihe.gazelle.maestro.api.business.testrun.validator;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.maestro.api.business.testrun.Test;

public class TestValidator extends AbstractValidator<Test> {

    @Override
    protected ValidatorBuilder<? extends Test> instantiateValidatorBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        return validatorBuilderFactory.getBuilder(Test.class);
    }

    @Override
    protected void addRulesAndMembers(ValidatorBuilder<? extends Test> validatorBuilder) {
        validatorBuilder
                .addRule("idValid", Test::isIdValid, "ID must be set and greater than 0")
                .addRule("nameValid", Test::isNameValid, "Name must be set and not empty")
                .addRule("stepsValid", Test::areStepsValid, "Test must contain at least one step")
                .addRule("stepsUniques", Test::areStepsUnique, "Steps must be unique")
        ;
    }
}
