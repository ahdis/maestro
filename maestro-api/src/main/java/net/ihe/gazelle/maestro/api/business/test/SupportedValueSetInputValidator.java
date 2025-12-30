package net.ihe.gazelle.maestro.api.business.test;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;

/**
 * Validator implementation for {@link SupportedValueSetInput}, responsible for defining
 */
public class SupportedValueSetInputValidator extends SupportedInputValidator<SupportedValueSetInput> {

   /**
    * Default constructor.
    */
   public SupportedValueSetInputValidator() {
      super();
   }

   /**
    * Constructs a new SupportedValueSetInputValidator with the specified ValidatorBuilderFactory.
    *
    * @param validatorBuilderFactory The factory used to construct a ValidatorBuilder for the current
    *                                validation process.
    */
   public SupportedValueSetInputValidator(ValidatorBuilderFactory validatorBuilderFactory) {
      super(validatorBuilderFactory);
   }

   @Override
   protected ValidatorBuilder<? extends SupportedValueSetInput> instantiateValidatorBuilder(
         ValidatorBuilderFactory validatorBuilderFactory) {
      return validatorBuilderFactory.getBuilder(SupportedValueSetInput.class);
   }

   @Override
   protected void addRulesAndMembers(ValidatorBuilder<? extends SupportedValueSetInput> validatorBuilder) {
      super.addRulesAndMembers(validatorBuilder);
      validatorBuilder
            .addRule(SupportedValueSetInput::isValueSetIdDefined,
                  "valueSetId must be set and not empty");
   }
}
