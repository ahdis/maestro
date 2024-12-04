package net.ihe.gazelle.testreport;

import net.ihe.gazelle.framework.modelvalidator.domain.AbstractBuilder;
import net.ihe.gazelle.framework.modelvalidator.domain.BuilderVisitor;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.testreport.visitor.UnexpectedErrorBuilderVisitor;

public class UnexpectedErrorBuilder extends AbstractBuilder<UnexpectedError> {

    private String name;
    private String message;
    private UnexpectedErrorBuilder cause;

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
    protected Class<? extends UnexpectedError> getParameterClass() {
        return UnexpectedError.class;
    }

    @Override
    protected BuilderVisitor getBuilderVisitor() {
        return UnexpectedErrorBuilderVisitor.INSTANCE;
    }

    @Override
    protected void make() {
        this.getObjectToBuild()
                .setName(name)
                .setMessage(message)
                .setCause(AbstractBuilder.buildField(cause));
    }
}