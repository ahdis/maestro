package net.ihe.gazelle.maestro.api.business.property;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;

class DirectValueValidator extends AbstractValidator<DirectValue> {

   public DirectValueValidator() {
      super();
   }

   public DirectValueValidator(ValidatorBuilderFactory validatorBuilderFactory) {
      super(validatorBuilderFactory);
   }

   @Override
   protected ValidatorBuilder<? extends DirectValue> instantiateValidatorBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
      return validatorBuilderFactory.getBuilder(DirectValue.class);
   }

   @Override
   protected void addRulesAndMembers(ValidatorBuilder<? extends DirectValue> validatorBuilder) {
      validatorBuilder
            .addRule(DirectValue::isValueDefined, "Property value must be set and not empty");
   }
}
