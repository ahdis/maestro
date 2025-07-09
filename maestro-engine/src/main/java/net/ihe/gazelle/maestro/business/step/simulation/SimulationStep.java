package net.ihe.gazelle.maestro.business.step.simulation;

import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.Step;

import java.util.ArrayList;
import java.util.List;

public class SimulationStep implements Step {

    public static final String TYPE = "SIMULATION";

    public static final String SIMULATION_SERVICE = "simulationService";
    public static final String SEQUENCE_ID = "sequenceId";
    public static final String TIMEOUT_SECONDS = "timeoutSeconds";

    private String name;
    private Property<String> simulationService;
    private Property<String> sequenceId;
    private Property<Integer> timeoutSeconds;
    private List<Property<?>> simulationParameters;

    public SimulationStep() {
        simulationParameters = new ArrayList<>();
    }

    public SimulationStep(String name, Property<String> simulationService, Property<String> sequenceId, Property<Integer> timeoutSeconds, List<Property<?>> simulationParameters) {
        this.name = name;
        this.simulationService = simulationService;
        this.sequenceId = sequenceId;
        this.timeoutSeconds = timeoutSeconds;
        this.simulationParameters = simulationParameters;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<Property<?>> getProperties() {
        List<Property<?>> props = new ArrayList<>();
        props.add(simulationService);
        props.add(sequenceId);
        props.add(timeoutSeconds);
        props.addAll(simulationParameters);
        return props;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setProperties(List<Property<?>> properties) {
        if (properties == null) {
            throw new IllegalArgumentException("Property shouldn't be null");
        }
        properties.forEach(property -> {
            switch (property.getName()) {
                case SIMULATION_SERVICE -> simulationService = (Property<String>) property;
                case SEQUENCE_ID -> sequenceId = (Property<String>) property;
                case TIMEOUT_SECONDS -> timeoutSeconds = (Property<Integer>) property;
                default -> {
                    boolean exists = simulationParameters.stream()
                            .anyMatch(prop -> prop.getName().equals(property.getName()));
                    if (!exists) {
                        simulationParameters.add(property);
                    }
                }
            }
        });
    }

    public Property<String> getSimulationService() {
        return simulationService;
    }

    public Property<String> getSequenceId() {
        return sequenceId;
    }

    public Property<Integer> getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public List<Property<?>> getSimulationParameters() {
        return simulationParameters;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
