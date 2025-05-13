package net.ihe.gazelle.testreport;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.lang.UnexpectedError;
import net.ihe.gazelle.testreport.validator.UnexpectedErrorValidator;

public class UnexpectedErrorBuilder extends AbstractBuilder<UnexpectedError> {

    private String name;
    private String message;
    private UnexpectedErrorBuilder cause;

    public UnexpectedErrorBuilder() {
        super();
    }

    public UnexpectedErrorBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
    }

    public UnexpectedErrorBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public UnexpectedErrorBuilder setMessage(String message) {
        this.message = message;
        return this;
    }

    public UnexpectedErrorBuilder setCause(UnexpectedErrorBuilder cause) {
        this.cause = cause;
        return this;
    }

    @Override
    protected AbstractValidator<UnexpectedError> instantiateValidator(ValidatorBuilderFactory validatorBuilderFactory) {
        return new UnexpectedErrorValidator(validatorBuilderFactory);
    }

    @Override
    protected UnexpectedError instantiate() {
        return new UnexpectedError();
    }

    @Override
    protected void make(UnexpectedError unexpectedError) {
        unexpectedError
                .setName(name)
                .setMessage(message)
                .setCause(AbstractBuilder.staticBuildWithoutValidation(cause));
    }
}