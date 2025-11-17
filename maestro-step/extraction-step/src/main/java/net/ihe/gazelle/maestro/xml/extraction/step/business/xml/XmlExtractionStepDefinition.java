package net.ihe.gazelle.maestro.xml.extraction.step.business.xml;

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
   public static final String TYPE = "EXTRACTION_XML";

   // inputs
   public static final String XML_FILE = "xmlFile";
   public static final String XML_PATH = "xmlPath";

   // outputs
   public static final String EXTRACTED_VALUE = "extractedValue";
   public static final String FAILURE_MESSAGE = "failureMessage";

    public XmlExtractionStepDefinition() {}

   @Override
   public String getType() {
       return TYPE;
   }

   @Override
   public List<SupportedInput> getSupportedInputs() {
      return List.of(
            new SupportedTextInput().setId(XML_PATH).setLabel(XML_FILE).setRequired(true),
            new SupportedFileInput().setId(XML_FILE).setLabel(XML_PATH).setRequired(true)
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
