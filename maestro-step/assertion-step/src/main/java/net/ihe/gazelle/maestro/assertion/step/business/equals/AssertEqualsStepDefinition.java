package net.ihe.gazelle.maestro.assertion.step.business.equals;

import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.test.SupportedInput;
import net.ihe.gazelle.maestro.api.business.test.SupportedTextInput;
import net.ihe.gazelle.maestro.spi.business.StepDefinition;

import java.io.Serial;
import java.util.List;
import java.util.Map;

public class AssertEqualsStepDefinition implements StepDefinition {

    @Serial
    private static final long serialVersionUID = 9207233191257118044L;

   public static final String TYPE = "ASSERT_EQUALS";

   // inputs
   public static final String EXPECTED = "expected";
   public static final String ACTUAL = "actual";

   // outputs
   public static final String FAILURE_MESSAGE = "failureMessage";

    @Override
   public String getType() {
      return TYPE;
   }

   @Override
   public List<SupportedInput> getSupportedInputs() {
      return List.of(
            new SupportedTextInput().setId(EXPECTED).setLabel("Expected").setRequired(true),
            new SupportedTextInput().setId(ACTUAL).setLabel("Actual").setRequired(true)
      );
   }

   @Override
   public Map<String, Class<? extends Property>> getOutputsDefinition() {
      return Map.of(
            FAILURE_MESSAGE, StringProperty.class
      );
   }
}
