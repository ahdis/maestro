package net.ihe.gazelle.maestro.extraction.step.conversion.step.business.base64.encode;

import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.test.SupportedInput;
import net.ihe.gazelle.maestro.api.business.test.SupportedTextInput;
import net.ihe.gazelle.maestro.spi.business.StepDefinition;

import java.util.List;
import java.util.Map;

public class EncodeBase64StepDefinition implements StepDefinition {

    // type
    public static final String TYPE = "ENCODE_BASE64";

    // inputs
    public static final String CONTENT_TO_ENCODE = "contentToEncode";

    // outputs
    public static final String ENCODED_CONTENT = "encodedContent";

    public EncodeBase64StepDefinition() {
        // Default constructor
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public List<SupportedInput> getSupportedInputs() {
        return List.of(
                new SupportedTextInput().setId(CONTENT_TO_ENCODE).setLabel("The content to encode into base64").setRequired(true)
        );
    }

    @Override
    public Map<String, Class<? extends Property>> getOutputsDefinition() {
        return Map.of(ENCODED_CONTENT, StringProperty.class);
    }
}
