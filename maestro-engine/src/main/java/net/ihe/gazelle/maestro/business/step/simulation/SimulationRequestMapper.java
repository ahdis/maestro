package net.ihe.gazelle.maestro.business.step.simulation;

import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.technical.dto.UnsupportedPropertyTypeException;
import net.ihe.gazelle.simulation.business.setup.ParameterBuilder;
import net.ihe.gazelle.simulation.business.setup.ParameterType;
import net.ihe.gazelle.simulation.business.setup.SimulationRequest;
import net.ihe.gazelle.simulation.business.setup.SimulationRequestBuilder;

public class SimulationRequestMapper {

    private SimulationRequestMapper() {
        // Empty
    }

    public static SimulationRequest createSimulationRequest(SimulationStep simulationStep) {
        SimulationRequestBuilder simulationRequest = new SimulationRequestBuilder()
                .setSequenceId(simulationStep.getSequenceId().getValue())
                .setTimeoutSeconds(simulationStep.getTimeoutSeconds().getValue())
                .setSimulationParameters(simulationStep.getSimulationParameters().stream().map(
                        property -> new ParameterBuilder()
                                .setName(property.getName())
                                .setValue(String.valueOf(property.getValue()))
                                .setType(parameterTypeMapper(property))
                ).toList());
        return simulationRequest.build();
    }

    private static <T> ParameterType parameterTypeMapper(Property<T> property) {
        if (property.getValue() instanceof String) {
            return ParameterType.TEXT;
        } else if (property.getValue() instanceof Boolean) {
            return ParameterType.BOOLEAN;
        } else if (property.getValue() instanceof byte[]) {
            return ParameterType.FILE;
        }
        throw new UnsupportedPropertyTypeException("Unsupported property type : " + property.getValue());
    }
}
