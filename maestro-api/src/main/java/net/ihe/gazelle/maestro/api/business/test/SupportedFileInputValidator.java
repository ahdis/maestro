package net.ihe.gazelle.maestro.api.business.test;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;

public class SupportedFileInputValidator extends SupportedInputValidator<SupportedFileInput> {

   public SupportedFileInputValidator() {
      super();
   }

   public SupportedFileInputValidator(ValidatorBuilderFactory validatorBuilderFactory) {
      super(validatorBuilderFactory);
   }

   @Override
   protected ValidatorBuilder<? extends SupportedFileInput> instantiateValidatorBuilder(
         ValidatorBuilderFactory validatorBuilderFactory) {
      return validatorBuilderFactory.getBuilder(SupportedFileInput.class);
   }

}
