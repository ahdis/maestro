package net.ihe.gazelle.testreport.validator.supplier;

import net.ihe.gazelle.framework.modelvalidator.domain.AbstractValidatorBuilderSupplier;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.testreport.EntityIdentification;
import net.ihe.gazelle.testreport.TestService;

public class TestServiceValidatorBuilderSupplier extends AbstractValidatorBuilderSupplier<TestService> {

    public TestServiceValidatorBuilderSupplier(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
    }

    @Override
    protected Class<? extends TestService> getType() {
        return TestService.class;
    }

    @Override
    protected ValidatorBuilder<TestService> supplyRulesAndMembers(ValidatorBuilder<TestService> validatorBuilder) {
        return validatorBuilder
                .addRule("serviceIdentificationValid", TestService::isServiceIdentificationValid, "serviceIdentification must be set")
                .addRule("serviceVersion", TestService::isServiceVersionValid, "version in serviceIdentification must be set and not empty")
                .addRule("disclaimerValid", TestService::isDisclaimerValid, "disclaimer must be set and not empty")
                .addMember("serviceIdentification", TestService::getServiceIdentification, EntityIdentification.class);
    }
}
