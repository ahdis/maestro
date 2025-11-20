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

package net.ihe.gazelle.maestro.simulation.step.technical.factory;

import net.ihe.gazelle.maestro.api.business.property.BooleanProperty;
import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.property.IntegerProperty;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.simulation.step.business.SimulationStepDefinition;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimulationStepExecutorFactoryTest {

    private final SimulationStepExecutorFactory factory = new SimulationStepExecutorFactory();

    @Test
    void canonicalNameTest(){
        assertEquals(SimulationStepDefinition.TYPE, factory.getSupportedStep());
    }

    @Test
    void getRequiredServicesOfSimulationStep(){
        Step simulationStep = new Step()
                .setId("sim-step")
                .setName("simulationStep")
                .setType(SimulationStepDefinition.TYPE)
                .setProperties(List.of(
                        new StringProperty(SimulationStepDefinition.SIMULATION_SERVICE, "MCSD"),
                        new StringProperty(SimulationStepDefinition.SEQUENCE_ID, "sequencId"),
                        new IntegerProperty(SimulationStepDefinition.TIMEOUT_SECONDS, 60)
                ))
                .addProperties(List.of(
                                new StringProperty("Patient Id", "1.1.1.1"),
                                new BooleanProperty("Boolean prop", true),
                                new ByteArrayProperty("File prop", "test".getBytes())
                        )
                );
        assertEquals(2, factory.getRequiredServices(simulationStep).size());
    }
}
