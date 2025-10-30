package net.ihe.gazelle.maestro.api.business.property;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;

class ReferenceValueValidator extends AbstractValidator<ReferenceValue> {

   public ReferenceValueValidator() {
      super();
   }

   public ReferenceValueValidator(ValidatorBuilderFactory validatorBuilderFactory) {
      super(validatorBuilderFactory);
   }

   @Override
   protected ValidatorBuilder<? extends ReferenceValue> instantiateValidatorBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
      return validatorBuilderFactory.getBuilder(ReferenceValue.class);
   }

   @Override
   protected void addRulesAndMembers(ValidatorBuilder<? extends ReferenceValue> validatorBuilder) {
      validatorBuilder
            .addRule(ReferenceValue::isReferenceDefined, "Reference must be defined");
   }
}
