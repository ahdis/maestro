package net.ihe.gazelle.maestro.api.business.testrun;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.maestro.api.business.test.TestValidator;
import net.ihe.gazelle.maestro.api.business.testreport.validator.SystemUnderTestValidator;
import net.ihe.gazelle.security.business.acl.AccessControlListValidator;

public class TestSuiteRunValidator extends AbstractValidator<TestSuiteRun> {

   private final TestValidator testValidator;
   private final AccessControlListValidator accessControlListValidator;
   private final SystemUnderTestValidator systemUnderTestValidator;

   public TestSuiteRunValidator() {
      super();
      this.testValidator = new TestValidator();
      this.accessControlListValidator = new AccessControlListValidator();
      this.systemUnderTestValidator = new SystemUnderTestValidator();
   }

   @Override
   protected ValidatorBuilder<? extends TestSuiteRun> instantiateValidatorBuilder(
         ValidatorBuilderFactory validatorBuilderFactory) {
      return validatorBuilderFactory.getBuilder(TestSuiteRun.class);
   }

   @Override
   protected void addRulesAndMembers(ValidatorBuilder<? extends TestSuiteRun> validatorBuilder) {
      validatorBuilder
            .addRule(TestSuiteRun::isTestSuiteDefined, "Test Suite must be set")
            .addRule(TestSuiteRun::areAllReferencedTestsAttached, "All Tests referenced in the Test Suite must be attached to the Test Suite Run.")
            .addMember("tests", TestSuiteRun::getTests, testValidator::getAssembledValidatorBuilder)
            .addMember("accessControlList", TestSuiteRun::getAccessControlList,
                  accessControlListValidator::getAssembledValidatorBuilder)
            .addMember("systemUnderTest", TestSuiteRun::getSystemsUnderTest,
                  systemUnderTestValidator::getAssembledValidatorBuilder);
   }

}
