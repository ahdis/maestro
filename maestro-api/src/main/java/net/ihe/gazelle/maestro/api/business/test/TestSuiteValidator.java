package net.ihe.gazelle.maestro.api.business.test;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;

public class TestSuiteValidator extends AbstractValidator<TestSuite> {

   public TestSuiteValidator() {
      super();
   }

   public TestSuiteValidator(ValidatorBuilderFactory validatorBuilderFactory) {
      super(validatorBuilderFactory);
   }

   @Override
   protected ValidatorBuilder<? extends TestSuite> instantiateValidatorBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
      return validatorBuilderFactory.getBuilder(TestSuite.class);
   }

   @Override
   protected void addRulesAndMembers(ValidatorBuilder<? extends TestSuite> validatorBuilder) {
      validatorBuilder
            .addRule(TestSuite::isIdDefined, "Test Suite ID must be set")
            .addRule(TestSuite::isNameDefined, "Test Suite name must be set and not empty")
            .addRule(TestSuite::hasAtLeastOneTest, "There must be at least one Test in the Test Suite");
      // FIXME add test for members (Test Reference and SupportedInput) when their validators are available
   }
}
