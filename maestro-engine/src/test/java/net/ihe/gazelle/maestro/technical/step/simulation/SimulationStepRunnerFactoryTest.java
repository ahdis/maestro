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

package net.ihe.gazelle.maestro.technical.step.simulation;

import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.Step;
import net.ihe.gazelle.maestro.business.step.assertion.AssertionStep;
import net.ihe.gazelle.maestro.business.step.simulation.SimulationStep;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimulationStepRunnerFactoryTest {

    private final SimulationStepRunnerFactory factory = new SimulationStepRunnerFactory();

    @Test
    void canonicalNameTest(){
        assertEquals(SimulationStep.class.getCanonicalName(), factory.getSupportedStep());
    }

    @Test
    void getRequiredServicesOfAssertionStep(){
        Step assertionStep = new AssertionStep(
                "saucisse",
                new Property<>(AssertionStep.ASSERTION, "equals(puré)".getBytes(StandardCharsets.UTF_8)),
                new Property<>(AssertionStep.ASSERTION_SERVICE, "cuisine")
        );
        assertEquals(1, factory.getRequiredServices(assertionStep).size());
    }
}
