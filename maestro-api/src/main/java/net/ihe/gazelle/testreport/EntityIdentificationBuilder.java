package net.ihe.gazelle.testreport;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.testreport.validator.EntityIdentificationValidator;

public class EntityIdentificationBuilder extends AbstractBuilder<EntityIdentification> {

    private String version;
    private String name;

    public EntityIdentificationBuilder() {
        super();
    }

    public EntityIdentificationBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
    }

    public EntityIdentificationBuilder setVersion(String version) {
        this.version = version;
        return this;
    }

    public EntityIdentificationBuilder setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    protected AbstractValidator<EntityIdentification> instantiateValidator(ValidatorBuilderFactory validatorBuilderFactory) {
        return new EntityIdentificationValidator(validatorBuilderFactory);
    }

    @Override
    protected EntityIdentification instantiate() {
        return new EntityIdentification();
    }

    @Override
    protected void make(EntityIdentification entityIdentification) {
        entityIdentification
                .setVersion(version)
                .setName(name);
    }
}
