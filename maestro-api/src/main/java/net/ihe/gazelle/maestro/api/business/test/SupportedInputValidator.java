/*
 * Copyright 2026 IHE International.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ihe.gazelle.maestro.api.business.test;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;

import java.util.List;
import java.util.function.Supplier;

/**
 * Validator implementation for {@link SupportedInput}, responsible for defining
 * @param <T> The concrete type of {@link SupportedInput} to validate
 */
public abstract class SupportedInputValidator<T extends SupportedInput> extends AbstractValidator<T> {

   /**
    * Default constructor.
    */
   protected SupportedInputValidator() {
      super();
   }

   /**
    * Constructor for SupportedInputValidator that takes a ValidatorBuilderFactory.
    * This constructor allows for the customization of the validation process by
    * utilizing a specific factory to create the validator builder.
    *
    * @param validatorBuilderFactory The factory used to construct a ValidatorBuilder
    *                                for the current validation process.
    */
   protected SupportedInputValidator(ValidatorBuilderFactory validatorBuilderFactory) {
      super(validatorBuilderFactory);
   }

   /**
    * Adds validation rules and members to the provided {@code ValidatorBuilder}.
    * This method defines the validation logic for the SupportedInput validation process,
    * ensuring that certain conditions, such as the presence of a valid ID and label, are met.
    *
    * @param validatorBuilder the builder used to define validation rules and members
    *                         for the {@link SupportedInput} object
    */
   @Override
   protected void addRulesAndMembers(ValidatorBuilder<? extends T> validatorBuilder) {
      validatorBuilder
            .addRule(SupportedInput::isIdDefined, "ID must be set and not empty")
            .addRule(SupportedInput::isLabelDefined, "Label must be set and not empty");
   }

   /**
    * Retrieves a list of all supported input validators.
    *
    * @return a list of {@link SupportedInputValidator} instances for various input types,
    *         including text input, file input, boolean input, and value set input.
    */
   @SuppressWarnings("java:S1452")
   static List<SupportedInputValidator<?>> getAllSupportedInputValidators() {
      return List.of(
            new SupportedTextInputValidator(),
            new SupportedFileInputValidator(),
            new SupportedBooleanInputValidator(),
            new SupportedValueSetInputValidator()
      );
   }

   /**
    * Retrieves a list of all supported input validators.
    * This method constructs and returns a list of validators for various input types,
    * such as text, file, boolean, and value set inputs.
    *
    * @param builderFactory The factory used to create or extend {@code ValidatorBuilder} instances
    *                       for different types of supported inputs.
    * @return A list of {@code SupportedInputValidator} instances responsible for validating
    *         specific types of input.
    */
   @SuppressWarnings("java:S1452")
   static List<SupportedInputValidator<?>> getAllSupportedInputValidators(ValidatorBuilderFactory builderFactory) {
      return List.of(
            new SupportedTextInputValidator(builderFactory),
            new SupportedFileInputValidator(builderFactory),
            new SupportedBooleanInputValidator(builderFactory),
            new SupportedValueSetInputValidator(builderFactory)
      );
   }

   /**
    * Constructs an array of suppliers for {@link ValidatorBuilder} objects, assembled from
    * the provided list of {@link SupportedInputValidator} instances. Each supplier in the
    * returned array corresponds to a validator assembler from the input validators.
    *
    * @param inputValidators the list of {@link SupportedInputValidator} instances from which
    *                        validator assembler suppliers will be extracted. Must not be null.
    * @return an array of suppliers for {@link ValidatorBuilder} objects, where each supplier
    *         is extracted from the provided list of input validators.
    * @throws NullPointerException if the inputValidators list is null or contains null elements.
    */
   @SuppressWarnings({"java:S1452", "unchecked"})
   static Supplier<ValidatorBuilder<?>>[] getAssembledSuppliers(final List<SupportedInputValidator<?>> inputValidators) {
      return inputValidators.stream()
            .map(SupportedInputValidator::getAssemblerSupplier)
            .toArray(Supplier[]::new);
   }

   /**
    * Retrieves a supplier that provides an assembled {@code ValidatorBuilder} for the specified
    * {@code SupportedInputValidator}. The supplier, when invoked, returns a validator builder
    * pre-configured with rules and members specific to the provided validator instance.
    *
    * @param validator the {@code SupportedInputValidator} instance from which the supplier will
    *                  extract the assembled {@code ValidatorBuilder}. Must not be null.
    * @return a {@code Supplier} of the assembled {@code ValidatorBuilder} for the given validator instance.
    */
   private static Supplier<ValidatorBuilder<?>> getAssemblerSupplier(final SupportedInputValidator<?> validator) {
      return validator::getAssembledValidatorBuilder;
   }
}
