package net.ihe.gazelle.maestro.api.business.property;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;

public class PropertyValidator extends AbstractValidator<Property> {

   private ReferencedValueValidator referencedValueValidator;
   private DirectValueValidator directValueValidator;

   public PropertyValidator() {
      super();
      referencedValueValidator = new ReferencedValueValidator();
      directValueValidator = new DirectValueValidator();
   }

   public PropertyValidator(ValidatorBuilderFactory validatorBuilderFactory) {
      super(validatorBuilderFactory);
      referencedValueValidator = new ReferencedValueValidator(validatorBuilderFactory);
      directValueValidator = new DirectValueValidator(validatorBuilderFactory);
   }

   @Override
   protected ValidatorBuilder<Property> instantiateValidatorBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
      return validatorBuilderFactory.getBuilder(Property.class);
   }


   @Override
   protected void addRulesAndMembers(ValidatorBuilder<? extends Property> validatorBuilder) {
      validatorBuilder
            .addRule(Property::isNameDefined, "Name must be set and not empty")
            .addMember("valueHolder", Property::getValueHolder,
                  referencedValueValidator::getAssembledValidatorBuilder,
                  directValueValidator::getAssembledValidatorBuilder);
   }
}
