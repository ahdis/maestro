package net.ihe.gazelle.maestro.spi.business;

import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.test.SupportedInput;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface StepDefinition extends Serializable {

   String getType();

   List<SupportedInput> getSupportedInputs();

   Map<String, Class<? extends Property>> getOutputsDefinition();

}
