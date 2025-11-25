package net.ihe.gazelle.maestro.extraction.step.conversion.step.business.base64.decode;

import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.test.SupportedInput;
import net.ihe.gazelle.maestro.api.business.test.SupportedTextInput;
import net.ihe.gazelle.maestro.spi.business.StepDefinition;

import java.util.List;
import java.util.Map;

public class DecodeBase64StepDefinition implements StepDefinition {

    // type
    public static final String TYPE = "DECODE_BASE64";

    // inputs
    public static final String CONTENT_TO_DECODE = "contentToDecode";

    // outputs
    public static final String DECODED_CONTENT = "decodedContent";

    public DecodeBase64StepDefinition() {
        // Default constructor
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public List<SupportedInput> getSupportedInputs() {
        return List.of(
                new SupportedTextInput().setId(CONTENT_TO_DECODE).setLabel("The content to decode from base64").setRequired(true)
        );
    }

    @Override
    public Map<String, Class<? extends Property>> getOutputsDefinition() {
        return Map.of(DECODED_CONTENT, ByteArrayProperty.class);
    }
}
