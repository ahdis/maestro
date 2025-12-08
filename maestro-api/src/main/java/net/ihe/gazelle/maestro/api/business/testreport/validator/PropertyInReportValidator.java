package net.ihe.gazelle.maestro.api.business.testreport.validator;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.maestro.api.business.property.Property;

public class PropertyInReportValidator extends AbstractValidator<Property> {

   public PropertyInReportValidator() {
      super();
   }

   public PropertyInReportValidator(ValidatorBuilderFactory validatorBuilderFactory) {
      super(validatorBuilderFactory);
   }

   @Override
   protected ValidatorBuilder<? extends Property> instantiateValidatorBuilder(
         ValidatorBuilderFactory validatorBuilderFactory) {
      return validatorBuilderFactory.getBuilder(Property.class);
   }

   @Override
   protected void addRulesAndMembers(ValidatorBuilder<? extends Property> validatorBuilder) {
      validatorBuilder
            .addRule(Property::isNameDefined, "Name must be set and not empty");
   }
}
