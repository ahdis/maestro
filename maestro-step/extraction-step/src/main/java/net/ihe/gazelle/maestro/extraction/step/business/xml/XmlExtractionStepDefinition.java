package net.ihe.gazelle.maestro.extraction.step.business.xml;

import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.test.SupportedFileInput;
import net.ihe.gazelle.maestro.api.business.test.SupportedInput;
import net.ihe.gazelle.maestro.api.business.test.SupportedTextInput;
import net.ihe.gazelle.maestro.spi.business.StepDefinition;

import java.util.List;
import java.util.Map;

public class XmlExtractionStepDefinition implements StepDefinition {

    // type
    public static final String TYPE = "EXTRACT_XML";

    // inputs
    public static final String XML_FILE = "xmlFile";
    public static final String XML_PATH = "xmlPath";
    public static final String NAMESPACES = "namespaces";
    public static final String DEFAULT_NAMESPACE = "defaultNamespace";

    // outputs
    public static final String EXTRACTED_VALUE = "extractedValue";
    public static final String FAILURE_MESSAGE = "failureMessage";

    public XmlExtractionStepDefinition() {
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public List<SupportedInput> getSupportedInputs() {
        return List.of(
                new SupportedTextInput().setId(XML_PATH).setLabel("Xpath that describe what should be extracted").setRequired(true),
                new SupportedFileInput().setId(XML_FILE).setLabel("Source XML file for the extraction").setRequired(true),
                new SupportedTextInput().setId(DEFAULT_NAMESPACE).setLabel("Default namespace under key=value format").setRequired(false),
                new SupportedTextInput().setId(NAMESPACES).setLabel("List of namespaces comma separated (key1=value1,key2=value2)").setRequired(false)
        );
    }

    @Override
    public Map<String, Class<? extends Property>> getOutputsDefinition() {
        return Map.of(
                FAILURE_MESSAGE, StringProperty.class,
                EXTRACTED_VALUE, StringProperty.class
        );
    }
}
