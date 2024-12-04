package net.ihe.gazelle.testreport;

import net.ihe.gazelle.framework.modelvalidator.domain.AbstractBuilder;
import net.ihe.gazelle.framework.modelvalidator.domain.BuilderVisitor;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.testreport.visitor.ItemBuilderVisitor;

public class ItemBuilder extends AbstractBuilder<Item> {

    private String name;
    private byte[] content;
    private String reference;

    public ItemBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
    }

    public ItemBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ItemBuilder setContent(byte[] content) {
        this.content = content;
        return this;
    }

    public ItemBuilder setReference(String reference) {
        this.reference = reference;
        return this;
    }

    @Override
    protected BuilderVisitor getBuilderVisitor() {
        return ItemBuilderVisitor.INSTANCE;
    }

    @Override
    protected Class<? extends Item> getParameterClass() {
        return Item.class;
    }

    @Override
    protected void make() {
        this.getObjectToBuild()
                .setName(name)
                .setContent(content)
                .setReference(reference);
    }
}
