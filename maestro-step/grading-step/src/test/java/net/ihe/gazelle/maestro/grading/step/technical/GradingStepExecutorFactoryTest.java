/*
 * Copyright 2025-2026 IHE International.
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

package net.ihe.gazelle.maestro.grading.step.technical;

import net.ihe.gazelle.maestro.api.business.property.StringListProperty;
import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.business.userinteract.GradingHandler;
import net.ihe.gazelle.maestro.api.business.userinteract.stub.GradingHandlerStub;
import net.ihe.gazelle.maestro.grading.step.business.GradingStepDefinition;
import net.ihe.gazelle.maestro.grading.step.business.GradingStepExecutor;
import net.ihe.gazelle.maestro.spi.business.Handler;
import net.ihe.gazelle.maestro.spi.business.StepExecutor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GradingStepExecutorFactoryTest {

    private final GradingStepExecutorFactory factory = new GradingStepExecutorFactory();

    @Test
    void stepTypeTest() {
        assertEquals(GradingStepDefinition.TYPE, factory.getSupportedStep());
    }

    @Test
    void getRequiredServicesOfGradingStep() {
        Step gradingStep = getGradingStep();
        assertEquals(1, factory.getRequiredServices(gradingStep).size());
    }

    @Test
    void createStepExecutorTest() {
        Step gradingStep = getGradingStep();
        GradingHandler handler = new GradingHandlerStub();
        Map<String, Handler> emptyHandlers = Map.of();
        assertThrows(IllegalArgumentException.class, () -> factory.createStepExecutor(gradingStep, emptyHandlers));

        StepExecutor executor = factory.createStepExecutor(gradingStep, Map.of(
                GradingHandler.SERVICE_NAME,
                handler)
        );

        assertInstanceOf(GradingStepExecutor.class, executor);
    }

    private Step getGradingStep() {
        return new Step()
                .setName("gradingStep")
                .setType(GradingStepDefinition.TYPE)
                .setProperties(List.of(
                        new StringListProperty(GradingStepDefinition.CRITERIA, List.of("Check A", "Check B"))
                ));
    }
}
