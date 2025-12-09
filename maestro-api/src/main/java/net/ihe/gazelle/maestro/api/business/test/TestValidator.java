package net.ihe.gazelle.maestro.api.business.test;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;

import java.util.List;

public class TestValidator extends AbstractValidator<Test> {

   private final StepValidator stepValidator;
   private final List<SupportedInputValidator<?>> supportedInputValidators;

   public TestValidator() {
      super();
      this.stepValidator = new StepValidator();
      this.supportedInputValidators = SupportedInputValidator.getAllSupportedInputValidators();
   }

   public TestValidator(ValidatorBuilderFactory validatorBuilderFactory) {
      super(validatorBuilderFactory);
      this.stepValidator = new StepValidator(validatorBuilderFactory);
      this.supportedInputValidators = SupportedInputValidator.getAllSupportedInputValidators(validatorBuilderFactory);
   }

   @Override
   protected ValidatorBuilder<? extends Test> instantiateValidatorBuilder(
         ValidatorBuilderFactory validatorBuilderFactory) {
      return validatorBuilderFactory.getBuilder(Test.class);
   }

   @Override
   @SuppressWarnings("unchecked")
   protected void addRulesAndMembers(ValidatorBuilder<? extends Test> validatorBuilder) {
      validatorBuilder
            .addRule(Test::isIdDefined, "ID must be set")
            .addRule(Test::isNameDefined, "Name must be set and not empty")
            .addRule(Test::atLeastOneStep, "Test must contain at least one step")
            .addRule(Test::areStepsUnique, "Steps must be unique")
            .addMember("steps", Test::getSteps, stepValidator::getAssembledValidatorBuilder)
            .addMember("supportedInputs", Test::getSupportedInputs,
                  SupportedInputValidator.getAssembledSuppliers(supportedInputValidators)
            );
   }

}
