package net.ihe.gazelle.testreport;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.testreport.validator.TestServiceValidator;

public class TestServiceBuilder extends AbstractBuilder<TestService> {

    private EntityIdentificationBuilder serviceIdentification;
    private String disclaimer;

    public TestServiceBuilder() {
        super();
    }

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
    protected AbstractValidator<TestService> instantiateValidator(ValidatorBuilderFactory validatorBuilderFactory) {
        return new TestServiceValidator(validatorBuilderFactory);
    }

    @Override
    protected TestService instantiate() {
        return new TestService();
    }

    @Override
    protected void make(TestService testService) {
        testService.setServiceIdentification(AbstractBuilder.staticBuildWithoutValidation(serviceIdentification))
                .setDisclaimer(disclaimer);
    }
}
