package net.ihe.gazelle.maestro.business.step.validation.api;

import net.ihe.gazelle.maestro.Property;
import net.ihe.gazelle.maestro.business.step.validation.ValidationRequestMapper;
import net.ihe.gazelle.maestro.business.step.validation.ValidationStep;
import net.ihe.gazelle.validation.api.domain.request.structure.ValidationItem;
import net.ihe.gazelle.validation.api.domain.request.structure.ValidationRequest;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ValidationRequestMapperTest {

    @Test
    void createValidationRequestTest(){
        ValidationStep step = new ValidationStep("validationStep",
                new Property<>(ValidationStep.VALIDATION_SERVICE, "matchbox"),
                new Property<>(ValidationStep.VALIDATION_PROFILE, "default"),
                new Property<>(ValidationStep.CONTENT_TO_VALIDATE, "saucisse".getBytes()));
        ValidationRequest request = ValidationRequestMapper.createValidationRequest(step);
        assertNotNull(request);
        assertEquals(step.getValidationProfile().getValue(), request.getValidationProfileId());
        assertEquals(step.getValidationService().getValue(), request.getValidationServiceName());
        assertEquals(1, request.getValidationItems().size());
        ValidationItem item = request.getValidationItems().getFirst();
        assertNotNull(item);
        assertEquals(step.getContentToValidate().getName(), item.getItemId());
        String expectedContent = new String(Base64.getEncoder().encode(step.getContentToValidate().getValue()), StandardCharsets.UTF_8);
        String actualContent = new String(item.getContent(), StandardCharsets.UTF_8);
        assertEquals(expectedContent, actualContent);
        assertEquals("?", item.getRole());
        assertEquals("?", item.getLocation());
    }

}
