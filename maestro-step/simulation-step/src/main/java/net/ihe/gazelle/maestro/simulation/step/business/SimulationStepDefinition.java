package net.ihe.gazelle.maestro.simulation.step.business;

import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.test.SupportedInput;
import net.ihe.gazelle.maestro.api.business.test.SupportedTextInput;
import net.ihe.gazelle.maestro.spi.business.StepDefinition;

import java.io.Serial;
import java.util.List;
import java.util.Map;

public class SimulationStepDefinition implements StepDefinition {

   public static final String TYPE = "SIMULATION";

   // inputs
   public static final String SIMULATION_SERVICE = "simulationService";
   public static final String SEQUENCE_ID = "sequenceId";

   // outputs
   public static final String REPORT = "report";

   @Serial
   private static final long serialVersionUID = -2808674613571801798L;

   @Override
   public String getType() {
      return TYPE;
   }

   @Override
   public List<SupportedInput> getSupportedInputs() {
      return List.of(
            new SupportedTextInput().setId(SIMULATION_SERVICE).setLabel("Simulation service").setRequired(true),
            new SupportedTextInput().setId(SEQUENCE_ID).setLabel("Sequence ID").setRequired(true)
      );
   }

   @Override
   public Map<String, Class<? extends Property>> getOutputsDefinition() {
      return Map.of(
            REPORT, ByteArrayProperty.class
      );
   }
}
