package net.ihe.gazelle.maestro.technical.dto.testrun;

import com.fasterxml.jackson.annotation.JsonTypeName;
import net.ihe.gazelle.maestro.business.step.simulation.SimulationStep;

@JsonTypeName(SimulationStep.TYPE)
public class SimulationStepDTO extends StepDTO<SimulationStep> {

    public SimulationStepDTO() {
        super(new SimulationStep());
    }

    public SimulationStepDTO(SimulationStep simulationStep) {
        super(simulationStep);
    }

    @Override
    public Class<SimulationStep> getBusinessObjectClass() {
        return SimulationStep.class;
    }
}
