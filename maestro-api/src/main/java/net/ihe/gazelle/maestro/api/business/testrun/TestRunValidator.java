package net.ihe.gazelle.maestro.api.business.testrun;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.maestro.api.business.property.PropertyValidator;
import net.ihe.gazelle.maestro.api.business.test.TestValidator;
import net.ihe.gazelle.maestro.api.business.testreport.validator.SystemUnderTestValidator;
import net.ihe.gazelle.security.business.acl.AccessControlListValidator;

public class TestRunValidator extends AbstractValidator<TestRun> {

   private final TestValidator testValidator;
   private final PropertyValidator propertyValidator;
   private final SystemUnderTestValidator systemUnderTestValidator;
   private final AccessControlListValidator accessControlListValidator;

   public TestRunValidator() {
      super();
      this.testValidator = new TestValidator();
      this.propertyValidator = new PropertyValidator();
      this.systemUnderTestValidator = new SystemUnderTestValidator();
      this.accessControlListValidator = new AccessControlListValidator();
   }

   public TestRunValidator(ValidatorBuilderFactory validatorBuilderFactory) {
      super(validatorBuilderFactory);
      this.testValidator = new TestValidator(validatorBuilderFactory);
      this.propertyValidator = new PropertyValidator(validatorBuilderFactory);
      this.systemUnderTestValidator = new SystemUnderTestValidator(validatorBuilderFactory);
      this.accessControlListValidator = new AccessControlListValidator(validatorBuilderFactory);
   }

   @Override
   protected ValidatorBuilder<? extends TestRun> instantiateValidatorBuilder(
         ValidatorBuilderFactory validatorBuilderFactory) {
      return validatorBuilderFactory.getBuilder(TestRun.class);
   }

   @Override
   @SuppressWarnings("unchecked")
   protected void addRulesAndMembers(ValidatorBuilder<? extends TestRun> validatorBuilder) {
      validatorBuilder
            .addRule(TestRun::isTestDefined, "Test must be defined")
            .addMember("test", TestRun::getTest, testValidator::getAssembledValidatorBuilder)
            .addMember("inputs", TestRun::getInputs, propertyValidator::getAssembledValidatorBuilder)
            .addMember("systemsUnderTest", TestRun::getSystemsUnderTest,
                  systemUnderTestValidator::getAssembledValidatorBuilder)
            .addMember("accessControlList", TestRun::getAccessControlList,
                  accessControlListValidator::getAssembledValidatorBuilder)
      ;
   }
}
