package net.ihe.gazelle.maestro.api.business.test;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.maestro.api.business.property.PropertyValidator;

public class TestReferenceValidator extends AbstractValidator<TestReference> {

   private final PropertyValidator propertyValidator;

   public TestReferenceValidator() {
      super();
      this.propertyValidator = new PropertyValidator();
   }

   public TestReferenceValidator(ValidatorBuilderFactory validatorBuilderFactory) {
      super(validatorBuilderFactory);
      this.propertyValidator = new PropertyValidator(validatorBuilderFactory);
   }

   @Override
   protected ValidatorBuilder<? extends TestReference> instantiateValidatorBuilder(
         ValidatorBuilderFactory validatorBuilderFactory) {
      return validatorBuilderFactory.getBuilder(TestReference.class);
   }

   @Override
   protected void addRulesAndMembers(ValidatorBuilder<? extends TestReference> validatorBuilder) {
      validatorBuilder
            .addRule(TestReference::isTestIdDefined, "Test ID must be set and not empty")
            .addMember("properties", TestReference::getProperties, propertyValidator::getAssembledValidatorBuilder);
   }
}
