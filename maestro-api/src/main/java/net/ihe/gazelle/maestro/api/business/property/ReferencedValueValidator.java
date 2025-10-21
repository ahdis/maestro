package net.ihe.gazelle.maestro.api.business.property;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;

class ReferencedValueValidator extends AbstractValidator<ReferencedValue> {

   public ReferencedValueValidator() {
      super();
   }

   public ReferencedValueValidator(ValidatorBuilderFactory validatorBuilderFactory) {
      super(validatorBuilderFactory);
   }

   @Override
   protected ValidatorBuilder<? extends ReferencedValue> instantiateValidatorBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
      return validatorBuilderFactory.getBuilder(ReferencedValue.class);
   }

   @Override
   protected void addRulesAndMembers(ValidatorBuilder<? extends ReferencedValue> validatorBuilder) {
      validatorBuilder
            .addRule(ReferencedValue::isReferenceDefined, "Reference must be defined");
   }
}
