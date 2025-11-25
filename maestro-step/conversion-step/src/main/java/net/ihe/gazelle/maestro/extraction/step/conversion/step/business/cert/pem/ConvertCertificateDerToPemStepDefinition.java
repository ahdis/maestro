package net.ihe.gazelle.maestro.extraction.step.conversion.step.business.cert.pem;

import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.test.SupportedFileInput;
import net.ihe.gazelle.maestro.api.business.test.SupportedInput;
import net.ihe.gazelle.maestro.spi.business.StepDefinition;

import java.util.List;
import java.util.Map;

public class ConvertCertificateDerToPemStepDefinition implements StepDefinition {

    // type
    public static final String TYPE = "CONVERT_DER_TO_PEM";

    // inputs
    public static final String DER_CERTIFICATE = "derCertificate";

    // outputs
    public static final String PEM_CERTIFICATE = "pemCertificate";

    public ConvertCertificateDerToPemStepDefinition() {
        // Default constructor
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public List<SupportedInput> getSupportedInputs() {
        return List.of(
                new SupportedFileInput().setId(DER_CERTIFICATE).setLabel("The certificate in DER format that will be converted to PEM").setRequired(true)
                );
    }

    @Override
    public Map<String, Class<? extends Property>> getOutputsDefinition() {
        return Map.of(PEM_CERTIFICATE, ByteArrayProperty.class);
    }
}
