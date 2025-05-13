package net.ihe.gazelle.testreport;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.testreport.validator.TestValidator;

public class TestBuilder extends AbstractBuilder<Test> {

    private String id;
    private String name;
    private String version;
    private String description;

    public TestBuilder() {
        super();
    }

    public TestBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
    }

    public TestBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public TestBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public TestBuilder setVersion(String version) {
        this.version = version;
        return this;
    }

    public TestBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    protected AbstractValidator<Test> instantiateValidator(ValidatorBuilderFactory validatorBuilderFactory) {
        return new TestValidator(validatorBuilderFactory);
    }

    @Override
    protected Test instantiate() {
        return new Test();
    }

    @Override
    protected void make(Test test) {
        test.setId(id)
                .setName(name)
                .setVersion(version)
                .setDescription(description);
    }
}
