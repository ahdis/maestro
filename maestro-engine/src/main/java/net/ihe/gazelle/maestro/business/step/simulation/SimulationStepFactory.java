package net.ihe.gazelle.maestro.business.step.simulation;

import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.Step;
import net.ihe.gazelle.maestro.api.business.testrun.StepFactory;

import java.util.Map;

// TODO : to delete when StepFactoryProvider will be deleted
public class SimulationStepFactory implements StepFactory {

    @Override
    @SuppressWarnings("unchecked")
    public Step create(String name, Map<String, Property<?>> properties) {
        return new SimulationStep(name,
                (Property<String>) properties.get(SimulationStep.SIMULATION_SERVICE),
                (Property<String>) properties.get(SimulationStep.SEQUENCE_ID),
                (Property<Integer>) properties.get(SimulationStep.TIMEOUT_SECONDS),
                properties.values().stream().toList()
        );
    }

    @Override
    public String getSupportedStep() {
        return SimulationStep.TYPE;
    }

}
