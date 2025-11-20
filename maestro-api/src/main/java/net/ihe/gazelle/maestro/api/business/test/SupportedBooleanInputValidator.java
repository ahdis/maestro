package net.ihe.gazelle.maestro.api.business.test;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;

public class SupportedBooleanInputValidator extends SupportedInputValidator<SupportedBooleanInput> {

   public SupportedBooleanInputValidator() {
      super();
   }

   public SupportedBooleanInputValidator(ValidatorBuilderFactory validatorBuilderFactory) {
      super(validatorBuilderFactory);
   }

   @Override
   protected ValidatorBuilder<? extends SupportedBooleanInput> instantiateValidatorBuilder(
         ValidatorBuilderFactory validatorBuilderFactory) {
      return validatorBuilderFactory.getBuilder(SupportedBooleanInput.class);
   }

}
