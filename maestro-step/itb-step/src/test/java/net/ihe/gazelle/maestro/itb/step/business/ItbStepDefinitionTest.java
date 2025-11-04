/*
 * Copyright 2025 Kereval.
 */
package net.ihe.gazelle.maestro.itb.step.business;

import net.ihe.gazelle.maestro.api.business.testrun.Property;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItbStepDefinitionTest {

    @Test
    void constructor_and_getters() {
        Property<String> testCase = new Property<>(ItbStepDefinition.TEST_CASE, "TC-1");
        Property<String> systemId = new Property<>(ItbStepDefinition.SYSTEM_ID, "SYS");
        Property<String> actorId = new Property<>(ItbStepDefinition.ACTOR_ID, "ACT");
        List<Property<?>> inputs = new ArrayList<>();
        Property<byte[]> binaryInput = new Property<>("payload", new byte[]{1, 2});
        Property<String> textInput = new Property<>("note", "hello");
        inputs.add(binaryInput);
        inputs.add(textInput);

        ItbStepDefinition step = new ItbStepDefinition("itb", testCase, systemId, actorId, inputs);

        assertEquals("itb", step.getName());
        assertEquals(testCase, step.getTestCase());
        assertEquals(systemId, step.getSystemID());
        assertEquals(actorId, step.getActorID());
        assertEquals(ItbStepDefinition.TYPE, step.getType());

        List<Property<?>> props = step.getProperties();
        assertTrue(props.contains(testCase));
        assertTrue(props.contains(systemId));
        assertTrue(props.contains(actorId));
        assertTrue(props.contains(binaryInput));
        assertTrue(props.contains(textInput));
    }

    @Test
    void setProperties_updatesCoreFields_and_rejectsNull() {
        ItbStepDefinition step = new ItbStepDefinition();
        assertThrows(IllegalArgumentException.class, () -> step.setProperties(null));

        Property<String> testCase = new Property<>(ItbStepDefinition.TEST_CASE, "TC-2");
        Property<String> systemId = new Property<>(ItbStepDefinition.SYSTEM_ID, "SYS-2");
        Property<String> actorId = new Property<>(ItbStepDefinition.ACTOR_ID, "ACT-2");
        Property<byte[]> input = new Property<>("payload", new byte[]{3});
        Property<String> textInput = new Property<>("note", "world");

        step.setProperties(List.of(testCase, systemId, actorId, input, textInput));

        assertEquals(testCase, step.getTestCase());
        assertEquals(systemId, step.getSystemID());
        assertEquals(actorId, step.getActorID());
        List<Property<?>> properties = step.getProperties();
        assertTrue(properties.contains(input));
        assertTrue(properties.contains(textInput));
    }
}
