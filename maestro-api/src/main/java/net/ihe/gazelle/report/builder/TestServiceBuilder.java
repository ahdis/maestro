package net.ihe.gazelle.report.builder;

import net.ihe.gazelle.framework.modelvalidator.domain.AbstractBuilder;
import net.ihe.gazelle.framework.modelvalidator.domain.BuilderVisitor;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.report.model.TestService;
import net.ihe.gazelle.report.visitor.TestServiceBuilderVisitor;

public class TestServiceBuilder extends AbstractBuilder<TestService> {

    private EntityIdentificationBuilder serviceIdentification;
    private String disclaimer;

    public TestServiceBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
    }

    public TestServiceBuilder setServiceIdentification(EntityIdentificationBuilder serviceIdentification) {
        this.serviceIdentification = serviceIdentification;
        return this;
    }

    public TestServiceBuilder setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
        return this;
    }

    @Override
    protected BuilderVisitor getBuilderVisitor() {
        return TestServiceBuilderVisitor.INSTANCE;
    }

    @Override
    protected Class<? extends TestService> getParameterClass() {
        return TestService.class;
    }

    @Override
    protected void make() {
        this.getObjectToBuild()
                .setServiceIdentification(AbstractBuilder.buildField(serviceIdentification))
                .setDisclaimer(disclaimer);
    }
}
