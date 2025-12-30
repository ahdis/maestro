package net.ihe.gazelle.maestro.api.business.test;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;

import java.util.List;

/**
 * Validator for the {@link Test} class that ensures specific validation rules are applied.
 */
public class TestValidator extends AbstractValidator<Test> {

   private final StepValidator stepValidator;
   private final List<SupportedInputValidator<?>> supportedInputValidators;

   /**
    * Default constructor.
    */
   public TestValidator() {
      super();
      this.stepValidator = new StepValidator();
      this.supportedInputValidators = SupportedInputValidator.getAllSupportedInputValidators();
   }

   /**
    * Constructs a {@code TestValidator} using the specified {@link ValidatorBuilderFactory}.
    *
    * @param validatorBuilderFactory The factory used to create {@link ValidatorBuilder} instances
    *                                for the {@code TestValidator} and its associated components.
    */
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
