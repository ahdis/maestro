package net.ihe.gazelle.maestro.api.business.test;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;

public class SupportedTextInputValidator extends SupportedInputValidator<SupportedTextInput> {

   public SupportedTextInputValidator() {
      super();
   }

   public SupportedTextInputValidator(ValidatorBuilderFactory validatorBuilderFactory) {
      super(validatorBuilderFactory);
   }

   @Override
   protected ValidatorBuilder<? extends SupportedTextInput> instantiateValidatorBuilder(
         ValidatorBuilderFactory validatorBuilderFactory) {
      return validatorBuilderFactory.getBuilder(SupportedTextInput.class);
   }

   @Override
   protected void addRulesAndMembers(ValidatorBuilder<? extends SupportedTextInput> validatorBuilder) {
      super.addRulesAndMembers(validatorBuilder);
      validatorBuilder
            .addRule(SupportedTextInput::areAllPossibleValuesDefined,
                  "possibleValues must not contain null or empty values");
   }
}
