package net.ihe.gazelle.maestro.api.business.property;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;

/**
 * Validator for the {@link DirectValue} class that ensures specific validation rules are applied.
 * This class extends {@link AbstractValidator} for type {@link DirectValue} and provides the implementation
 * for creating a validator builder and defining the validation rules.
 */
class DirectValueValidator extends AbstractValidator<DirectValue> {

   /**
    * Default constructor.
    */
   public DirectValueValidator() {
      super();
   }

   /**
    * Creates a new instance of DirectValueValidator with a specified ValidatorBuilderFactory.
    * This constructor is intended to initialize the validator with the provided builder factory,
    * enabling custom rules and validation logic for the DirectValue type.
    *
    * @param validatorBuilderFactory the factory to create the validator builder; used to construct or extend
    *                                validation logic applicable to DirectValue instances.
    */
   public DirectValueValidator(ValidatorBuilderFactory validatorBuilderFactory) {
      super(validatorBuilderFactory);
   }

   @Override
   protected ValidatorBuilder<? extends DirectValue> instantiateValidatorBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
      return validatorBuilderFactory.getBuilder(DirectValue.class);
   }

   @Override
   protected void addRulesAndMembers(ValidatorBuilder<? extends DirectValue> validatorBuilder) {
      validatorBuilder
            .addRule(DirectValue::isValueDefined, "Property value must be set and not empty");
   }
}
