package net.ihe.gazelle.maestro.extraction.step.conversion.step.business.base64.decode;

import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.test.SupportedInput;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DecodeBase64StepDefinitionTest {

    @Test
    void testCorrectType() {
        DecodeBase64StepDefinition stepDefinition = new DecodeBase64StepDefinition();
        assertEquals("DECODE_BASE64", stepDefinition.getType());
    }

    @Test
    void testCorrectSupportedInputs() {
        DecodeBase64StepDefinition stepDefinition = new DecodeBase64StepDefinition();

        assertNotNull(stepDefinition.getSupportedInputs());
        assertEquals(1, stepDefinition.getSupportedInputs().size());

        SupportedInput input = stepDefinition.getSupportedInputs().getFirst();
        assertEquals(DecodeBase64StepDefinition.CONTENT_TO_DECODE, input.getId());
        assertTrue(input.isRequired());
    }

    @Test
    void testCorrectOutputsDefinition() {
        DecodeBase64StepDefinition stepDefinition = new DecodeBase64StepDefinition();

        assertNotNull(stepDefinition.getOutputsDefinition());
        assertEquals(2, stepDefinition.getOutputsDefinition().size());

        assertTrue(stepDefinition.getOutputsDefinition().containsKey(DecodeBase64StepDefinition.DECODED_CONTENT));
        assertEquals(ByteArrayProperty.class, stepDefinition.getOutputsDefinition().get(DecodeBase64StepDefinition.DECODED_CONTENT));

        assertTrue(stepDefinition.getOutputsDefinition().containsKey(DecodeBase64StepDefinition.FAILURE_MESSAGE));
        assertEquals(StringProperty.class, stepDefinition.getOutputsDefinition().get(DecodeBase64StepDefinition.FAILURE_MESSAGE));
    }
}
