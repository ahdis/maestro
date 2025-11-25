package net.ihe.gazelle.maestro.extraction.step.conversion.step.business.base64.encode;

import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.test.SupportedInput;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EncodeBase64StepDefinitionTest {

    @Test
    void testCorrectType() {
        EncodeBase64StepDefinition stepDefinition = new EncodeBase64StepDefinition();
        assertEquals("ENCODE_BASE64", stepDefinition.getType());
    }

    @Test
    void testCorrectSupportedInputs() {
        EncodeBase64StepDefinition stepDefinition = new EncodeBase64StepDefinition();

        assertNotNull(stepDefinition.getSupportedInputs());
        assertEquals(1, stepDefinition.getSupportedInputs().size());

        SupportedInput input = stepDefinition.getSupportedInputs().getFirst();
        assertEquals(EncodeBase64StepDefinition.CONTENT_TO_ENCODE, input.getId());
        assertTrue(input.isRequired());
    }

    @Test
    void testCorrectOutputsDefinition() {
        EncodeBase64StepDefinition stepDefinition = new EncodeBase64StepDefinition();

        assertNotNull(stepDefinition.getOutputsDefinition());
        assertEquals(1, stepDefinition.getOutputsDefinition().size());

        assertTrue(stepDefinition.getOutputsDefinition().containsKey(EncodeBase64StepDefinition.ENCODED_CONTENT));
        assertEquals(StringProperty.class, stepDefinition.getOutputsDefinition().get(EncodeBase64StepDefinition.ENCODED_CONTENT));
    }
}
