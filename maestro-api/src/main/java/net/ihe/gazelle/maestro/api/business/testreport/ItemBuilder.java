package net.ihe.gazelle.maestro.api.business.testreport;


import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.maestro.api.business.testreport.validator.ItemValidator;

public class ItemBuilder extends AbstractBuilder<Item> {

    private String name;
    private byte[] content;
    private String reference;

    public ItemBuilder() {
        super();
    }

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
    protected AbstractValidator<Item> instantiateValidator(ValidatorBuilderFactory validatorBuilderFactory) {
        return new ItemValidator(validatorBuilderFactory);
    }

    @Override
    protected Item instantiate() {
        return new Item();
    }

    @Override
    protected void make(Item item) {
        item.setName(name)
                .setContent(content)
                .setReference(reference);
    }
}
