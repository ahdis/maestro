package net.ihe.gazelle.maestro.extraction.step.conversion.step.business.cert;

import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.extraction.step.conversion.step.business.cert.pem.ConvertCertificateDerToPemStepDefinition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConvertCertificateDerToPemStepDefinitionTest {

    @Test
    void testCorrectType() {
        ConvertCertificateDerToPemStepDefinition stepDefinition = new ConvertCertificateDerToPemStepDefinition();
        assertEquals("CONVERT_DER_TO_PEM", stepDefinition.getType());
    }

    @Test
    void testCorrectSupportedInputs() {
        ConvertCertificateDerToPemStepDefinition stepDefinition = new ConvertCertificateDerToPemStepDefinition();
        assertEquals(1, stepDefinition.getSupportedInputs().size());
        assertEquals("derCertificate", stepDefinition.getSupportedInputs().getFirst().getId());
    }

    @Test
    void testCorrectOutputsDefinition() {
        ConvertCertificateDerToPemStepDefinition stepDefinition = new ConvertCertificateDerToPemStepDefinition();
        assertEquals(2, stepDefinition.getOutputsDefinition().size());
        assertEquals(ByteArrayProperty.class, stepDefinition.getOutputsDefinition().get("pemCertificate"));
        assertEquals(StringProperty.class, stepDefinition.getOutputsDefinition().get("failureMessage"));
    }
}
