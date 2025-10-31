package net.ihe.gazelle.maestro.simulation.step.business;

import net.ihe.gazelle.maestro.api.business.test.SupportedInput;
import net.ihe.gazelle.maestro.api.business.test.SupportedTextInput;
import net.ihe.gazelle.maestro.spi.business.runner.StepDefinition;

import java.io.Serial;
import java.util.List;

public class SimulationStepDefinition implements StepDefinition {

   public static final String TYPE = "SIMULATION";
   public static final String SIMULATION_SERVICE = "simulationService";
   public static final String SEQUENCE_ID = "sequenceId";
   public static final String TIMEOUT_SECONDS = "timeoutSeconds";

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
            new SupportedTextInput().setId(SEQUENCE_ID).setLabel("Sequence ID").setRequired(true),
            new SupportedTextInput().setId(TIMEOUT_SECONDS).setLabel("Timeout (seconds)").setRequired(false)
      );
   }
}
