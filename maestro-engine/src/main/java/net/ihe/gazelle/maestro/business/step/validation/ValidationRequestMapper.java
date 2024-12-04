package net.ihe.gazelle.maestro.business.step.validation;

import net.ihe.gazelle.validation.api.domain.request.structure.ValidationItem;
import net.ihe.gazelle.validation.api.domain.request.structure.ValidationRequest;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * object used to create matching validation request with Validation API
 */
public class ValidationRequestMapper {

    private ValidationRequestMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static ValidationRequest createValidationRequest(ValidationStep validationStep) {
        ValidationRequest validationRequest = new ValidationRequest();
        validationRequest.setValidationProfileId(validationStep.getValidationProfile().getValue());
        validationRequest.setValidationServiceName(validationStep.getValidationService().getValue());
        validationRequest.setValidationItems(getItems(validationStep));
        return validationRequest;
    }

    public static List<ValidationItem> getItems(ValidationStep validationStep) {
        List<ValidationItem> items = new ArrayList<>();
        ValidationItem item = new ValidationItem();
        item.setItemId(validationStep.getContentToValidate().getName());
        item.setContent(Base64.getEncoder().encode(validationStep.getContentToValidate().getValue()));
        item.setRole("?");
        item.setLocation("?");
        items.add(item);
        return items;
    }
}
