/*
 * Copyright 2025 Kereval.
 */
package net.ihe.gazelle.maestro.itb.step.business;

import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItbStepDefinitionTest {

    @Test
    void simulation_step_definition_test() {
        ItbStepDefinition definition = new ItbStepDefinition();
        assertEquals(ItbStepDefinition.TYPE, definition.getType());
        assertEquals(2, definition.getSupportedInputs().size());
        assertEquals(ItbStepDefinition.TEST_CASE, definition.getSupportedInputs().getFirst().getId());
        assertEquals(ItbStepDefinition.ACTOR_ID, definition.getSupportedInputs().get(1).getId());
        assertEquals(3, definition.getOutputsDefinition().size());
        assertEquals(ByteArrayProperty.class, definition.getOutputsDefinition().get(ItbStepDefinition.XML_REPORT));
        assertEquals(ByteArrayProperty.class, definition.getOutputsDefinition().get(ItbStepDefinition.PDF_REPORT));
        assertEquals(StringProperty.class, definition.getOutputsDefinition().get(ItbStepDefinition.LOGS));
    }
}
