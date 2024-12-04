package net.ihe.gazelle.testreport.validator.supplier;

import net.ihe.gazelle.framework.modelvalidator.domain.AbstractValidatorBuilderSupplier;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.testreport.EntityIdentification;
import net.ihe.gazelle.testreport.SystemUnderTest;

public class SystemUnderTestValidatorBuilderSupplier extends AbstractValidatorBuilderSupplier<SystemUnderTest> {


    public SystemUnderTestValidatorBuilderSupplier(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
    }

    @Override
    protected Class<? extends SystemUnderTest> getType() {
        return SystemUnderTest.class;
    }

    @Override
    protected ValidatorBuilder<SystemUnderTest> supplyRulesAndMembers(ValidatorBuilder<SystemUnderTest> validatorBuilder) {
        return validatorBuilder
                .addRule("systemIdentificationValid", SystemUnderTest::isSystemIdentificationValid, "systemIdentification must be set")
                .addMember("systemIdentification", SystemUnderTest::getSystemIdentification, EntityIdentification.class);
    }
}
