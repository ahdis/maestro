package net.ihe.gazelle.maestro.api.business.test;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.maestro.api.business.property.PropertyValidator;

public class StepValidator extends AbstractValidator<Step> {

   private final PropertyValidator propertyValidator;

   public StepValidator() {
      super();
      propertyValidator = new PropertyValidator();
   }

   public StepValidator(ValidatorBuilderFactory validatorBuilderFactory) {
      super(validatorBuilderFactory);
      propertyValidator = new PropertyValidator(validatorBuilderFactory);
   }

   @Override
   protected ValidatorBuilder<? extends Step> instantiateValidatorBuilder(
         ValidatorBuilderFactory validatorBuilderFactory) {
      return validatorBuilderFactory.getBuilder(Step.class);
   }

   @Override
   protected void addRulesAndMembers(ValidatorBuilder<? extends Step> validatorBuilder) {
      validatorBuilder
            .addRule(Step::isNameDefined, "Name must be set and not empty")
            .addRule(Step::isTypeDefined, "Type must be set and not empty")
            .addRule(Step::areOutputMappingsValid,
                  "Both key and value of an OutputMapping must be defined if present")
            .addMember("properties", Step::getProperties, propertyValidator::getAssembledValidatorBuilder);
   }
}
