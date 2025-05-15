package net.ihe.gazelle.maestro.business.step.validation;

import net.ihe.gazelle.validation.v2.api.business.request.ValidationItemBuilder;
import net.ihe.gazelle.validation.v2.api.business.request.ValidationRequest;
import net.ihe.gazelle.validation.v2.api.business.request.ValidationRequestBuilder;

/**
 * object used to create matching validation request with Validation API
 */
public class ValidationRequestMapper {

    private ValidationRequestMapper() {
        // Empty
    }

    public static ValidationRequest createValidationRequest(ValidationStep validationStep) {
        ValidationRequestBuilder validationRequest = new ValidationRequestBuilder()
                .setValidationProfileId(validationStep.getValidationProfile().getValue())
                .addValidationItem(new ValidationItemBuilder()
                        .setContent(validationStep.getContentToValidate().getValue())
                        .build()
                );
        return validationRequest.build();
    }
}
