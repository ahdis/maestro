package net.ihe.gazelle.maestro.xml.extraction.step.business;

import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.test.SupportedInput;
import net.ihe.gazelle.maestro.api.business.test.SupportedTextInput;
import net.ihe.gazelle.maestro.spi.business.StepDefinition;

import java.util.List;
import java.util.Map;

public class XmlExtractorStepDefinition implements StepDefinition {

   // type
   public static final String TYPE = "XML_EXTRACTION";

   // inputs
   public static final String SOURCE_XML = "xmlFile";
   public static final String XML_PATH = "xmlPath";

   // outputs
   public static final String EXTRACTED_VALUE = "extractedValue";
   public static final String FAILURE_MESSAGE = "failureMessage";


   @Override
   public String getType() {
       return TYPE;
   }

   @Override
   public List<SupportedInput> getSupportedInputs() {
      return List.of(
            new SupportedTextInput().setId(XML_PATH).setLabel(SOURCE_XML).setRequired(true),
            new SupportedTextInput().setId(SOURCE_XML).setLabel(XML_PATH).setRequired(true)
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
