package net.ihe.gazelle.maestro.api.business.testreport.validator;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.lang.UnexpectedError;

public class UnexpectedErrorValidator extends AbstractValidator<UnexpectedError> {

    private final UnexpectedErrorValidator causeValidator;

    public UnexpectedErrorValidator() {
        super();
        causeValidator = new UnexpectedErrorValidator(this);
    }

    public UnexpectedErrorValidator(UnexpectedErrorValidator unexpectedErrorValidator) {
        super();
        causeValidator = unexpectedErrorValidator;
    }

    public UnexpectedErrorValidator(ValidatorBuilderFactory builderFactory) {
        super(builderFactory);
        causeValidator = new UnexpectedErrorValidator(builderFactory, this);
    }

    public UnexpectedErrorValidator(ValidatorBuilderFactory builderFactory, UnexpectedErrorValidator unexpectedErrorValidator) {
        super(builderFactory);
        causeValidator = unexpectedErrorValidator;
    }

    @Override
    protected ValidatorBuilder<? extends UnexpectedError> instantiateValidatorBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        return validatorBuilderFactory.getBuilder(UnexpectedError.class);
    }

    @Override
    protected void addRulesAndMembers(ValidatorBuilder<? extends UnexpectedError> validatorBuilder) {
        validatorBuilder
                .addRule("nameValid", UnexpectedError::isNameValid, "Name must be set and not empty.")
                .addRule("messageValid", UnexpectedError::isMessageValid, "Message must be set and not empty.")
                .addMember("cause", UnexpectedError::getCause, causeValidator::getAssembledValidatorBuilder);
    }
}
