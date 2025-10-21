package net.ihe.gazelle.maestro.spi.business.runner;

import net.ihe.gazelle.maestro.api.business.test.SupportedInput;

import java.util.List;

public interface StepDefinition {

   String getType();

   List<SupportedInput> getSupportedInputs();

}
