package net.ihe.gazelle.maestro.api.business.testreport.validator;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.maestro.api.business.testreport.Item;

public class ItemValidator extends AbstractValidator<Item> {

    public ItemValidator() {
        super();
    }

    public ItemValidator(ValidatorBuilderFactory builderFactory) {
        super(builderFactory);
    }

    @Override
    protected ValidatorBuilder<? extends Item> instantiateValidatorBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        return validatorBuilderFactory.getBuilder(Item.class);
    }

    @Override
    protected void addRulesAndMembers(ValidatorBuilder<? extends Item> validatorBuilder) {
        validatorBuilder
                .addRule("nameValid", Item::isNameValid, "Name must be set and not empty")
                .addRule("contentValid", Item::isContentValid, "If reference is null, content must be set and not empty")
                .addRule("referenceValid", Item::isReferenceValid, "If content is null, reference must be set and not empty");
    }
}
