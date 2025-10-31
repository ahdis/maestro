package net.ihe.gazelle.maestro.api.business.test;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;

public class StepValidator extends AbstractValidator<Step> {

   public StepValidator() {
      super();
   }

   public StepValidator(ValidatorBuilderFactory validatorBuilderFactory) {
      super(validatorBuilderFactory);
   }

   @Override
   protected ValidatorBuilder<? extends Step> instantiateValidatorBuilder(
         ValidatorBuilderFactory validatorBuilderFactory) {
      return validatorBuilderFactory.getBuilder(Step.class);
   }

   @Override
   protected void addRulesAndMembers(ValidatorBuilder<? extends Step> validatorBuilder) {
      validatorBuilder
            .addRule(Step::isIdDefined, "ID must be set")
            .addRule(Step::isNameDefined, "Name must be set and not empty")
            .addRule(Step::isTypeDefined, "Type must be set and not empty");
   }
}
