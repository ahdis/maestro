package net.ihe.gazelle.maestro.api.business.test;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;

public class SupportedValueSetInputValidator  extends SupportedInputValidator<SupportedValueSetInput> {

    public SupportedValueSetInputValidator() {
        super();
    }

    public SupportedValueSetInputValidator(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
    }

    @Override
    protected ValidatorBuilder<? extends SupportedValueSetInput> instantiateValidatorBuilder(
          ValidatorBuilderFactory validatorBuilderFactory) {
       return validatorBuilderFactory.getBuilder(SupportedValueSetInput.class);
    }

    @Override
    protected void addRulesAndMembers(ValidatorBuilder<? extends SupportedValueSetInput> validatorBuilder) {
       super.addRulesAndMembers(validatorBuilder);
       validatorBuilder
             .addRule(SupportedValueSetInput::isValueSetIdDefined,
                   "valueSetId must be set and not empty");
    }
}
