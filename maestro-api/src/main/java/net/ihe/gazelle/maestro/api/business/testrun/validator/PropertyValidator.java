package net.ihe.gazelle.maestro.api.business.testrun.validator;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.maestro.api.business.testrun.Property;

public class PropertyValidator extends AbstractValidator<Property> {

    @Override
    protected ValidatorBuilder<Property> instantiateValidatorBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        return  validatorBuilderFactory.getBuilder(Property.class);
    }


    @Override
    protected void addRulesAndMembers(ValidatorBuilder<? extends Property> validatorBuilder) {
        validatorBuilder
                .addRule("nameValid", Property::isNameValid, "Name must be set and not empty");
    }
}
