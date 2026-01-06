package net.ihe.gazelle.maestro.api.business.property;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;

/**
 * Validator implementation for {@link ReferenceValue}, responsible for defining
 * validation rules and structure for instances of the {@code ReferenceValue} class.
 * This class ensures that a {@code ReferenceValue} object meets pre-defined validation
 * criteria.
 */
class ReferenceValueValidator extends AbstractValidator<ReferenceValue> {

   /**
    * Default constructor.
    */
   public ReferenceValueValidator() {
      super();
   }

   /**
    * Constructs a ReferenceValueValidator with the provided ValidatorBuilderFactory.
    *
    * @param validatorBuilderFactory the factory used to instantiate validator builders
    *                                for the {@code ReferenceValue} validation process
    */
   public ReferenceValueValidator(ValidatorBuilderFactory validatorBuilderFactory) {
      super(validatorBuilderFactory);
   }

   @Override
   protected ValidatorBuilder<? extends ReferenceValue> instantiateValidatorBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
      return validatorBuilderFactory.getBuilder(ReferenceValue.class);
   }

   @Override
   protected void addRulesAndMembers(ValidatorBuilder<? extends ReferenceValue> validatorBuilder) {
      validatorBuilder
            .addRule(ReferenceValue::isReferenceDefined, "Reference must be defined");
   }
}
