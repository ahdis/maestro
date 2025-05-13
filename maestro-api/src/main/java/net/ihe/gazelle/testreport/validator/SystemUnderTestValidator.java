package net.ihe.gazelle.testreport.validator;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.testreport.SystemUnderTest;

public class SystemUnderTestValidator extends AbstractValidator<SystemUnderTest> {

    private final EntityIdentificationValidator entityIdentificationValidator;

    public SystemUnderTestValidator() {
        super();
        entityIdentificationValidator = new EntityIdentificationValidator();
    }

    public SystemUnderTestValidator(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
        entityIdentificationValidator = new EntityIdentificationValidator();
    }

    @Override
    protected ValidatorBuilder<? extends SystemUnderTest> instantiateValidatorBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        return validatorBuilderFactory.getBuilder(SystemUnderTest.class);
    }

    @Override
    protected void addRulesAndMembers(ValidatorBuilder<? extends SystemUnderTest> validatorBuilder) {
        validatorBuilder
                .addRule("systemIdentificationValid", SystemUnderTest::isSystemIdentificationValid, "systemIdentification must be set")
                .addMember("systemIdentification", SystemUnderTest::getSystemIdentification, entityIdentificationValidator::getAssembledValidatorBuilder);
    }
}
