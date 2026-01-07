package net.ihe.gazelle.maestro.api.business.property;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;

/**
 * Validator for the {@link Property} class that ensures specific validation rules are applied.
 */
public class PropertyValidator extends AbstractValidator<Property> {

   private final ReferenceValueValidator referenceValueValidator;
   private final DirectValueValidator directValueValidator;

   /**
    * Default constructor.
    */
   public PropertyValidator() {
      super();
      referenceValueValidator = new ReferenceValueValidator();
      directValueValidator = new DirectValueValidator();
   }

   /**
    * Constructs a new instance of {@code PropertyValidator} with the specified
    * {@link ValidatorBuilderFactory}. This constructor initializes the
    * {@code PropertyValidator} with validator builders for validating reference values
    * and direct values associated with properties.
    *
    * @param validatorBuilderFactory the factory used to create validator builders
    *                                for {@code Property}, {@code ReferenceValue},
    *                                and {@code DirectValue} validation processes
    */
   public PropertyValidator(ValidatorBuilderFactory validatorBuilderFactory) {
      super(validatorBuilderFactory);
      referenceValueValidator = new ReferenceValueValidator(validatorBuilderFactory);
      directValueValidator = new DirectValueValidator(validatorBuilderFactory);
   }

   @Override
   protected ValidatorBuilder<Property> instantiateValidatorBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
      return validatorBuilderFactory.getBuilder(Property.class);
   }


   @Override
   @SuppressWarnings("unchecked")
   protected void addRulesAndMembers(ValidatorBuilder<? extends Property> validatorBuilder) {
      validatorBuilder
            .addRule(Property::isNameDefined, "Name must be set and not empty")
            .addMember("valueHolder", Property::getValueHolder,
                  referenceValueValidator::getAssembledValidatorBuilder,
                  directValueValidator::getAssembledValidatorBuilder);
   }
}
