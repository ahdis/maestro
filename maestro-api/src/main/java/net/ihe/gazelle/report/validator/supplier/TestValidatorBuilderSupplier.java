package net.ihe.gazelle.report.validator.supplier;

import net.ihe.gazelle.framework.modelvalidator.domain.AbstractValidatorBuilderSupplier;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.report.model.Test;

public class TestValidatorBuilderSupplier extends AbstractValidatorBuilderSupplier<Test> {

    public TestValidatorBuilderSupplier(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
    }

    @Override
    protected Class<? extends Test> getType() {
        return Test.class;
    }

    @Override
    protected ValidatorBuilder<Test> supplyRulesAndMembers(ValidatorBuilder<Test> validatorBuilder) {
        return validatorBuilder
                .addRule("idValid", Test::isIdValid, "Id must be set and not empty")
                .addRule("nameValid", Test::isNameValid, "Name must be set if present")
                .addRule("versionValid", Test::isVersionValid, "Version must be set if present")
                .addRule("descriptionValid", Test::isDescriptionValid, "Description must be set id present");
    }
}
