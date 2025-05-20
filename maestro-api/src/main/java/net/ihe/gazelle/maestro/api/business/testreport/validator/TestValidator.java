package net.ihe.gazelle.maestro.api.business.testreport.validator;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.maestro.api.business.testreport.Test;

public class TestValidator extends AbstractValidator<Test> {

    public TestValidator() {
        super();
    }

    public TestValidator(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
    }

    @Override
    protected ValidatorBuilder<? extends Test> instantiateValidatorBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        return validatorBuilderFactory.getBuilder(Test.class);
    }

    @Override
    protected void addRulesAndMembers(ValidatorBuilder<? extends Test> validatorBuilder) {
        validatorBuilder
                .addRule("idValid", Test::isIdValid, "Id must be set and not empty")
                .addRule("nameValid", Test::isNameValid, "Name must be set if present")
                .addRule("versionValid", Test::isVersionValid, "Version must be set if present")
                .addRule("descriptionValid", Test::isDescriptionValid, "Description must be set id present");
    }
}
