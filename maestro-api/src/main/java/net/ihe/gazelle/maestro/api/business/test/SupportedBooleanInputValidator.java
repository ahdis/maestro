package net.ihe.gazelle.maestro.api.business.test;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;

/**
 * Validator implementation for {@link SupportedBooleanInput}, responsible for defining
 */
public class SupportedBooleanInputValidator extends SupportedInputValidator<SupportedBooleanInput> {

   /**
    * Default constructor
    */
   public SupportedBooleanInputValidator() {
      super();
   }

   /**
    * Constructs a {@code SupportedBooleanInputValidator} with the specified {@code ValidatorBuilderFactory}.
    *
    * @param validatorBuilderFactory the factory to create or extend {@code ValidatorBuilder} instances
    */
   public SupportedBooleanInputValidator(ValidatorBuilderFactory validatorBuilderFactory) {
      super(validatorBuilderFactory);
   }

   @Override
   protected ValidatorBuilder<? extends SupportedBooleanInput> instantiateValidatorBuilder(
         ValidatorBuilderFactory validatorBuilderFactory) {
      return validatorBuilderFactory.getBuilder(SupportedBooleanInput.class);
   }

}
