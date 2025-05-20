package net.ihe.gazelle.maestro.api.business.testreport.validator;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.maestro.api.business.testreport.EntityIdentification;

public class EntityIdentificationValidator extends AbstractValidator<EntityIdentification> {

    public EntityIdentificationValidator() {
        super();
    }

    public EntityIdentificationValidator(ValidatorBuilderFactory builderFactory) {
        super(builderFactory);
    }

    @Override
    protected ValidatorBuilder<? extends EntityIdentification> instantiateValidatorBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        return validatorBuilderFactory.getBuilder(EntityIdentification.class);
    }

    @Override
    protected void addRulesAndMembers(ValidatorBuilder<? extends EntityIdentification> validatorBuilder) {
        validatorBuilder
                .addRule("versionValid", EntityIdentification::isVersionValid, "Version must be set if present")
                .addRule("nameValid", EntityIdentification::isNameValid, "Name must be set and not empty");
    }
}
