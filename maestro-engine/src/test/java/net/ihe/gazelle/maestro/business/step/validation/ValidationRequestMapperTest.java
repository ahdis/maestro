package net.ihe.gazelle.maestro.business.step.validation;

import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.business.step.validation.ValidationRequestMapper;
import net.ihe.gazelle.maestro.business.step.validation.ValidationStep;
import net.ihe.gazelle.validation.v2.api.business.request.ValidationItem;
import net.ihe.gazelle.validation.v2.api.business.request.ValidationRequest;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class ValidationRequestMapperTest {

    @Test
    void createValidationRequestTest() {
        ValidationStep step = new ValidationStep("validationStep",
                new Property<>(ValidationStep.VALIDATION_SERVICE, "matchbox"),
                new Property<>(ValidationStep.VALIDATION_PROFILE, "default"),
                new Property<>(ValidationStep.CONTENT_TO_VALIDATE, "content".getBytes()));
        ValidationRequest request = ValidationRequestMapper.createValidationRequest(step);
        assertNotNull(request);
        assertEquals(step.getValidationProfile().getValue(), request.getValidationProfileId());
        assertEquals(1, request.getValidationItems().size());
        ValidationItem item = request.getValidationItems().getFirst();
        assertNotNull(item);
        assertNull(item.getItemId());
        String actualContent = new String(item.getContent(), StandardCharsets.UTF_8);
        assertEquals("content", actualContent);
        assertNull(item.getRole());
        assertNull(item.getLocation());
    }

}
