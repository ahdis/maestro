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

import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.business.testrun.Property;

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
    private List<Property<Object>> simulationParameters;

    public SimulationStep() {
        simulationParameters = new ArrayList<>();
    }

    public SimulationStep(String name, Property<String> simulationService, Property<String> sequenceId, Property<Integer> timeoutSeconds, List<Property<?>> simulationParameters) {
        this.name = name;
        this.simulationService = simulationService;
        this.sequenceId = sequenceId;
        this.timeoutSeconds = timeoutSeconds;
        // Normalize to Property<Object> for heterogeneous parameter values
        this.simulationParameters = new ArrayList<>();
        if (simulationParameters != null) {
            for (Property<?> p : simulationParameters) {
                this.simulationParameters.add(toObjectProperty(p));
            }
        }
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
                        simulationParameters.add(toObjectProperty(property));
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

    public List<Property<Object>> getSimulationParameters() {
        return simulationParameters;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    private static Property<Object> toObjectProperty(Property<?> property) {
        return new Property<>(property.getName(), property.getValue());
    }
}
