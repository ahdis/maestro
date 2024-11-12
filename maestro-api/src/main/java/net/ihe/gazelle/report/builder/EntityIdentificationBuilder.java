package net.ihe.gazelle.report.builder;

import net.ihe.gazelle.framework.modelvalidator.domain.AbstractBuilder;
import net.ihe.gazelle.framework.modelvalidator.domain.BuilderVisitor;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.report.model.EntityIdentification;
import net.ihe.gazelle.report.visitor.EntityIdentificationBuilderVisitor;

public class EntityIdentificationBuilder extends AbstractBuilder<EntityIdentification> {

    private String version;
    private String name;

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
    protected BuilderVisitor getBuilderVisitor() {
        return EntityIdentificationBuilderVisitor.INSTANCE;
    }

    @Override
    protected Class<? extends EntityIdentification> getParameterClass() {
        return EntityIdentification.class;
    }

    @Override
    protected void make() {
        this.getObjectToBuild()
                .setName(name)
                .setVersion(version);
    }
}
