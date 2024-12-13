package net.ihe.gazelle.testreport;

import net.ihe.gazelle.framework.modelvalidator.domain.AbstractBuilder;
import net.ihe.gazelle.framework.modelvalidator.domain.BuilderVisitor;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.testreport.visitor.TestBuilderVisitor;


public class TestBuilder extends AbstractBuilder<Test> {

    private String id;
    private String name;
    private String version;
    private String description;

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
    protected BuilderVisitor getBuilderVisitor() {
        return TestBuilderVisitor.INSTANCE;
    }

    @Override
    protected Class<? extends Test> getParameterClass() {
        return Test.class;
    }

    @Override
    protected void make() {
        this.getObjectToBuild()
                .setId(id)
                .setName(name)
                .setVersion(version)
                .setDescription(description);
    }
}
