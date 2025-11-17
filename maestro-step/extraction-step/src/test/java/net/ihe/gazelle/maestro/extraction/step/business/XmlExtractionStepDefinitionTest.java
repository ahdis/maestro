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

package net.ihe.gazelle.maestro.extraction.step.business;

import net.ihe.gazelle.maestro.api.business.test.SupportedInput;
import net.ihe.gazelle.maestro.extraction.step.business.xml.XmlExtractionStepDefinition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class XmlExtractionStepDefinitionTest {

    @Test
    void setPropertiesEmptyField(){
        XmlExtractionStepDefinition stepDefinition = new XmlExtractionStepDefinition();

        assertNotNull(stepDefinition.getType());
        assertNotNull(stepDefinition.getSupportedInputs());
        assertNotNull(stepDefinition.getOutputsDefinition());

        assertEquals(4, stepDefinition.getSupportedInputs().size());
        assertEquals(2, stepDefinition.getOutputsDefinition().size());
        assertEquals(2, stepDefinition.getSupportedInputs().stream().filter(SupportedInput::isRequired).count());
    }

}
