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

package net.ihe.gazelle.maestro.grading.step.business;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GradingStepDefinitionTest {

    @Test
    void grading_step_definition_test() {
        GradingStepDefinition definition = new GradingStepDefinition();
        assertEquals(GradingStepDefinition.TYPE, definition.getType());
        assertEquals(1, definition.getSupportedInputs().size());
        assertEquals(GradingStepDefinition.CRITERIA, definition.getSupportedInputs().getFirst().getId());
        assertTrue(definition.getOutputsDefinition().isEmpty());
    }
}
