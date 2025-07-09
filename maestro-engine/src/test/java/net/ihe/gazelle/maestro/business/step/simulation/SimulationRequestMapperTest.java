/*
 * Copyright 2025 IHE International.
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

package net.ihe.gazelle.maestro.business.step.simulation;

import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.technical.dto.UnsupportedPropertyTypeException;
import net.ihe.gazelle.simulation.business.setup.Parameter;
import net.ihe.gazelle.simulation.business.setup.SimulationRequest;
import net.ihe.gazelle.simulation.business.setup.SimulationRequestValidator;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimulationRequestMapperTest {

    private final SimulationRequestValidator validator = new SimulationRequestValidator();

    @Test
    void should_map_step_to_request() {
        SimulationStep step = new SimulationStep("simulationStep",
                new Property<>(SimulationStep.SIMULATION_SERVICE, "MCSD"),
                new Property<>(SimulationStep.SEQUENCE_ID, "sequencId"),
                new Property<>(SimulationStep.TIMEOUT_SECONDS, 60),
                List.of(
                        new Property<>("Patient Id", "1.1.1.1"),
                        new Property<>("Boolean prop", true),
                        new Property<>("File prop", "test".getBytes(StandardCharsets.UTF_8))
                ));
        SimulationRequest request = SimulationRequestMapper.createSimulationRequest(step);
        assertNotNull(request);
        assertDoesNotThrow(() -> validator.assertValid(request));
        assertEquals(step.getSequenceId().getValue(), request.getSequenceId());
        assertEquals(step.getTimeoutSeconds().getValue(), request.getTimeoutSeconds());
        assertEquals(3, request.getSimulationParameters().size());
        List<Parameter> simulationParams = request.getSimulationParameters();
        Parameter patientId = simulationParams.getFirst();
        assertEquals(step.getSimulationParameters().getFirst().getName(), patientId.getName());
        assertEquals(step.getSimulationParameters().getFirst().getValue(), patientId.getValue());
    }

    @Test
    void should_throw_unsupported_property_type() {
        SimulationStep step = new SimulationStep("simulationStep",
                new Property<>(SimulationStep.SIMULATION_SERVICE, "MCSD"),
                new Property<>(SimulationStep.SEQUENCE_ID, "sequencId"),
                new Property<>(SimulationStep.TIMEOUT_SECONDS, 60),
                List.of(
                        new Property<>("Patient Id", 10),
                        new Property<>("Registry URL", "/test")
                ));
        assertThrows(UnsupportedPropertyTypeException.class, () -> SimulationRequestMapper.createSimulationRequest(step));
    }

    @Test
    void blank_sequence_id_should_create_invalid_request() {
        SimulationStep step = new SimulationStep("simulationStep",
                new Property<>(SimulationStep.SIMULATION_SERVICE, "MCSD"),
                new Property<>(SimulationStep.SEQUENCE_ID, ""),
                new Property<>(SimulationStep.TIMEOUT_SECONDS, 60),
                List.of());
        assertThrows(IllegalArgumentException.class, () -> SimulationRequestMapper.createSimulationRequest(step));
    }
}
