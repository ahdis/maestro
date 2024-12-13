package net.ihe.gazelle.maestro.business.step.validation;

import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.validation.api.domain.request.builder.ValidationItemBuilder;
import net.ihe.gazelle.validation.api.domain.request.builder.ValidationRequestBuilder;
import net.ihe.gazelle.validation.api.domain.request.structure.ValidationRequest;

/**
 * object used to create matching validation request with Validation API
 */
public class ValidationRequestMapper {

    private ValidationRequestMapper() {
        // Empty
    }

    public static ValidationRequest createValidationRequest(ValidationStep validationStep, ValidatorBuilderFactory validatorBuilderFactory) {
        ValidationRequestBuilder validationRequest = new ValidationRequestBuilder(validatorBuilderFactory)
                .setValidationProfileId(validationStep.getValidationProfile().getValue())
                .setValidationServiceName(validationStep.getValidationService().getValue())
                .addValidationItem(new ValidationItemBuilder(validatorBuilderFactory)
                        .setContent(validationStep.getContentToValidate().getValue())
                        .build()
                );
        return validationRequest.build();
    }
}
