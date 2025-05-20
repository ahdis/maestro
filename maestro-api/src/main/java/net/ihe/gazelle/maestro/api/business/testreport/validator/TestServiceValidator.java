package net.ihe.gazelle.maestro.api.business.testreport.validator;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.maestro.api.business.testreport.TestService;

public class TestServiceValidator extends AbstractValidator<TestService> {

    private final EntityIdentificationValidator entityIdentificationValidator;

    public TestServiceValidator() {
        super();
        entityIdentificationValidator = new EntityIdentificationValidator();
    }

    public TestServiceValidator(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
        entityIdentificationValidator = new EntityIdentificationValidator();
    }

    @Override
    protected ValidatorBuilder<? extends TestService> instantiateValidatorBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        return validatorBuilderFactory.getBuilder(TestService.class);
    }

    @Override
    protected void addRulesAndMembers(ValidatorBuilder<? extends TestService> validatorBuilder) {
        validatorBuilder
                .addRule("serviceIdentificationValid", TestService::isServiceIdentificationValid, "serviceIdentification must be set")
                .addRule("serviceVersion", TestService::isServiceVersionValid, "version in serviceIdentification must be set and not empty")
                .addRule("disclaimerValid", TestService::isDisclaimerValid, "disclaimer must be set and not empty")
                .addMember("serviceIdentification", TestService::getServiceIdentification, entityIdentificationValidator::getAssembledValidatorBuilder);
    }
}
