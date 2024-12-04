package net.ihe.gazelle.testreport.validator.supplier;

import net.ihe.gazelle.framework.modelvalidator.domain.AbstractValidatorBuilderSupplier;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.testreport.UnexpectedError;

public class UnexpectedErrorValidatorBuilderSupplier extends AbstractValidatorBuilderSupplier<UnexpectedError> {

    public UnexpectedErrorValidatorBuilderSupplier(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
    }

    @Override
    protected Class<? extends UnexpectedError> getType() {
        return UnexpectedError.class;
    }

    @Override
    protected ValidatorBuilder<UnexpectedError> supplyRulesAndMembers(ValidatorBuilder<UnexpectedError> validatorBuilder) {
        return  validatorBuilder
                .addRule("nameValid", UnexpectedError::isNameValid, "Name must be set and not empty")
                .addRule("messageValid", UnexpectedError::isMessageValid, "Message must be set and not empty")
                .addMember("cause", UnexpectedError::getCause, UnexpectedError.class);
    }

}
