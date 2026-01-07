package net.ihe.gazelle.maestro.api.business.test;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;

/**
 * Validator implementation for {@link SupportedFileInput}, responsible for defining
 */
public class SupportedFileInputValidator extends SupportedInputValidator<SupportedFileInput> {

   /**
    * Default constructor
    */
   public SupportedFileInputValidator() {
      super();
   }

   /**
    * Constructs a {@code SupportedFileInputValidator} with a specified {@code ValidatorBuilderFactory}.
    *
    * @param validatorBuilderFactory The factory used to create validator builders for the {@code SupportedFileInput}.
    */
   public SupportedFileInputValidator(ValidatorBuilderFactory validatorBuilderFactory) {
      super(validatorBuilderFactory);
   }

   @Override
   protected ValidatorBuilder<? extends SupportedFileInput> instantiateValidatorBuilder(
         ValidatorBuilderFactory validatorBuilderFactory) {
      return validatorBuilderFactory.getBuilder(SupportedFileInput.class);
   }

}
