package net.ihe.gazelle.testreport.validator.supplier;

import net.ihe.gazelle.framework.modelvalidator.domain.AbstractValidatorBuilderSupplier;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.testreport.Item;

public class ItemValidatorBuilderSupplier extends AbstractValidatorBuilderSupplier<Item> {

    public ItemValidatorBuilderSupplier(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
    }

    @Override
    protected Class<? extends Item> getType() {
        return Item.class;
    }

    @Override
    protected ValidatorBuilder<Item> supplyRulesAndMembers(ValidatorBuilder<Item> validatorBuilder) {
        return validatorBuilder
                .addRule("nameValid", Item::isNameValid, "Name must be set and not empty")
                .addRule("contentValid", Item::isContentValid, "If reference is null, content must be set and not empty")
                .addRule("referenceValid", Item::isReferenceValid, "If content is null, reference must be set and not empty");
    }
}
