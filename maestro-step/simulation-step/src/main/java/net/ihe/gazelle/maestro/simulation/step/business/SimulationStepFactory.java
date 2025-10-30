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
import net.ihe.gazelle.maestro.spi.business.runner.StepFactory;

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
