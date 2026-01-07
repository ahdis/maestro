package net.ihe.gazelle.maestro.api.business.testreport.validator;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.property.ByteArrayItemProperty;

/**
 * Validator implementation for {@link ByteArrayItemProperty}, responsible for defining
 */
public class ByteArrayItemPropertyValidator extends PropertyInReportValidator {

   /**
    * Default constructor
    */
   public ByteArrayItemPropertyValidator() {
      super();
   }

   /**
    * Constructs a new ByteArrayItemPropertyValidator with the specified ValidatorBuilderFactory.
    *
    * @param validatorBuilderFactory the factory used to create ValidatorBuilder instances for validation logic
    */
   public ByteArrayItemPropertyValidator(
         ValidatorBuilderFactory validatorBuilderFactory) {
      super(validatorBuilderFactory);
   }

   @Override
   protected ValidatorBuilder<? extends Property> instantiateValidatorBuilder(
         ValidatorBuilderFactory validatorBuilderFactory) {
      return validatorBuilderFactory.getBuilder(ByteArrayItemProperty.class);
   }

   @Override
   @SuppressWarnings("unchecked")
   protected void addRulesAndMembers(ValidatorBuilder<? extends Property> validatorBuilder) {
      super.addRulesAndMembers(validatorBuilder);
      ((ValidatorBuilder<ByteArrayItemProperty>) validatorBuilder)
            .addRule(ByteArrayItemProperty::isReferenceDefinedIfValueNull,
                  "Byte array property must either have a value or be a reference to an item");
   }
}
