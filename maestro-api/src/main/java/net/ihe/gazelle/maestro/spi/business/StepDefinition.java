package net.ihe.gazelle.maestro.spi.business;

import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.test.SupportedInput;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Defines a step in a test, including its type, supported inputs, and outputs.
 */
public interface StepDefinition extends Serializable {

   /**
    * Returns the type of the step.
    *
    * @return the step type
    */
   String getType();

   /**
    * Returns the list of inputs supported by this step.
    *
    * @return a list of supported inputs
    */
   List<SupportedInput> getSupportedInputs();

   /**
    * Returns the definition of outputs produced by this step.
    *
    * @return a map of output names to their corresponding property classes
    */
   Map<String, Class<? extends Property>> getOutputsDefinition();

}
