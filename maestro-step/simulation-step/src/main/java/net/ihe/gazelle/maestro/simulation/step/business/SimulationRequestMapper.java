/*
 * Copyright 2025 Kereval.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ihe.gazelle.maestro.simulation.step.business;

import net.ihe.gazelle.maestro.api.business.UnsupportedPropertyTypeException;
import net.ihe.gazelle.maestro.api.business.testrun.Property;
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
