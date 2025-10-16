package net.ihe.gazelle.maestro.api.business.testrun.validator;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.maestro.api.business.testreport.validator.SystemUnderTestValidator;
import net.ihe.gazelle.security.business.acl.AccessControlListValidator;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;

public class TestSuiteRunValidator extends AbstractValidator<TestSuiteRun> {

   private final TestRunValidator testRunValidator;
   private final AccessControlListValidator accessControlListValidator;
   private final SystemUnderTestValidator systemUnderTestValidator;

   public TestSuiteRunValidator() {
      super();
      this.testRunValidator = new TestRunValidator();
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
            .addRule(TestSuiteRun::isTestSuiteNameValid, "Test Suite name must be set and not empty")
            .addRule(TestSuiteRun::isTestSetRunIdValid, "Test Set Run ID must be set and not empty")
            .addRule(TestSuiteRun::hasAtLeastOneTestRun, "There must be at least one Test Run in the Test Suite Run")
            .addMember("testRuns", TestSuiteRun::getTestRuns,
                  testRunValidator::getAssembledValidatorBuilder)
            .addMember("accessControlList", TestSuiteRun::getAccessControlList,
                  accessControlListValidator::getAssembledValidatorBuilder)
            .addMember("systemUnderTest", TestSuiteRun::getSystemsUnderTest,
                  systemUnderTestValidator::getAssembledValidatorBuilder)
      ;
   }
}
