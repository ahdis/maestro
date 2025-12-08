package net.ihe.gazelle.maestro.api.business.test;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;

import java.util.List;
import java.util.function.Supplier;

public abstract class SupportedInputValidator<T extends SupportedInput> extends AbstractValidator<T> {

   protected SupportedInputValidator() {
      super();
   }

   protected SupportedInputValidator(ValidatorBuilderFactory validatorBuilderFactory) {
      super(validatorBuilderFactory);
   }

   @Override
   protected void addRulesAndMembers(ValidatorBuilder<? extends T> validatorBuilder) {
      validatorBuilder
            .addRule(SupportedInput::isIdDefined, "ID must be set and not empty")
            .addRule(SupportedInput::isLabelDefined, "Label must be set and not empty");
   }

   @SuppressWarnings("java:S1452")
   static List<SupportedInputValidator<?>> getAllSupportedInputValidators() {
      return List.of(
            new SupportedTextInputValidator(),
            new SupportedFileInputValidator(),
            new SupportedBooleanInputValidator(),
            new SupportedValueSetInputValidator()
      );
   }

   @SuppressWarnings("java:S1452")
   static List<SupportedInputValidator<?>> getAllSupportedInputValidators(ValidatorBuilderFactory builderFactory) {
      return List.of(
            new SupportedTextInputValidator(builderFactory),
            new SupportedFileInputValidator(builderFactory),
            new SupportedBooleanInputValidator(builderFactory),
            new SupportedValueSetInputValidator(builderFactory)
      );
   }

   @SuppressWarnings("java:S1452")
   static Supplier<ValidatorBuilder<?>>[] getAssembledSuppliers(final List<SupportedInputValidator<?>> inputValidators) {
      return inputValidators.stream()
            .map(SupportedInputValidator::getAssemblerSupplier)
            .toArray(Supplier[]::new);
   }

   private static Supplier<ValidatorBuilder<?>> getAssemblerSupplier(final SupportedInputValidator<?> validator) {
      return validator::getAssembledValidatorBuilder;
   }
}
