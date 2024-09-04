package net.ihe.gazelle.business.step.validation.api;

import net.ihe.gazelle.context.Context;
import net.ihe.gazelle.business.step.validation.ValidationStep;
import net.ihe.gazelle.validation.api.domain.request.structure.ValidationItem;
import net.ihe.gazelle.validation.api.domain.request.structure.ValidationRequest;

import java.util.ArrayList;
import java.util.List;

public class ValidationRequestMapper {

    public static ValidationRequest createValidationRequest(ValidationStep validationStep, Context context) {
        ValidationRequest validationRequest = new ValidationRequest();
        validationRequest.setValidationProfileId(validationStep.getProperties().get(ValidationStep.VALIDATION_PROFILE));
        validationRequest.setValidationServiceName(validationStep.getProperties().get(ValidationStep.VALIDATION_SERVICE));
        validationRequest.setValidationItems(getItems(validationStep, context));
        return validationRequest;
    }

    public static List<ValidationItem> getItems(ValidationStep validationStep, Context context) {
        List<ValidationItem> items = new ArrayList<>();
        ValidationItem item = new ValidationItem();
        item.setItemId(validationStep.getProperties().get(ValidationStep.CONTENT_TO_VALIDATE));
        item.setContent(validationStep.getProperties().get(ValidationStep.CONTENT_TO_VALIDATE).getBytes());
        item.setRole("?");
        item.setLocation("?");
        items.add(item);
        return items;
    }
}
