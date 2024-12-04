package net.ihe.gazelle.testreport.validator.supplier;

import net.ihe.gazelle.framework.modelvalidator.domain.AbstractValidatorBuilderSupplier;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.testreport.EntityIdentification;

public class EntityIdentificationValidatorBuilderSupplier extends AbstractValidatorBuilderSupplier<EntityIdentification> {

    public EntityIdentificationValidatorBuilderSupplier(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
    }

    @Override
    protected Class<? extends EntityIdentification> getType() {
        return EntityIdentification.class;
    }

    @Override
    protected ValidatorBuilder<EntityIdentification> supplyRulesAndMembers(ValidatorBuilder<EntityIdentification> validatorBuilder) {
        return validatorBuilder
                .addRule("versionValid", EntityIdentification::isVersionValid, "Version must be set if present")
                .addRule("nameValid", EntityIdentification::isNameValid, "Name must be set and not empty");
    }
}
