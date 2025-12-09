package net.ihe.gazelle.maestro.api.business.test;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;

import java.util.List;

public class TestSuiteValidator extends AbstractValidator<TestSuite> {

   private final TestReferenceValidator testReferenceValidator;
   private final List<SupportedInputValidator<?>> supportedInputValidators;

   public TestSuiteValidator() {
      super();
      this.testReferenceValidator = new TestReferenceValidator();
      this.supportedInputValidators = SupportedInputValidator.getAllSupportedInputValidators();
   }

   public TestSuiteValidator(ValidatorBuilderFactory validatorBuilderFactory) {
      super(validatorBuilderFactory);
      this.testReferenceValidator = new TestReferenceValidator(validatorBuilderFactory);
      this.supportedInputValidators = SupportedInputValidator.getAllSupportedInputValidators(validatorBuilderFactory);
   }

   @Override
   protected ValidatorBuilder<? extends TestSuite> instantiateValidatorBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
      return validatorBuilderFactory.getBuilder(TestSuite.class);
   }

   @Override
   @SuppressWarnings("unchecked")
   protected void addRulesAndMembers(ValidatorBuilder<? extends TestSuite> validatorBuilder) {
      validatorBuilder
            .addRule(TestSuite::isIdDefined, "Test Suite ID must be set and not empty")
            .addRule(TestSuite::isNameDefined, "Test Suite name must be set and not empty")
            .addRule(TestSuite::hasAtLeastOneTest, "There must be at least one Test in the Test Suite")
            .addMember("testReferences", TestSuite::getTestReferences,
                  testReferenceValidator::getAssembledValidatorBuilder)
            .addMember("supportedInputs", TestSuite::getSupportedInputs,
                  SupportedInputValidator.getAssembledSuppliers(supportedInputValidators));
   }
}
