/*
 * Copyright 2025 Kereval.
 */
package net.ihe.gazelle.maestro.itb.step.business;

import net.ihe.gazelle.maestro.api.business.testrun.Property;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItbStepTest {

    @Test
    void constructor_and_getters() {
        Property<String> testCase = new Property<>(ItbStep.TEST_CASE, "TC-1");
        Property<String> systemId = new Property<>(ItbStep.SYSTEM_ID, "SYS");
        Property<String> actorId = new Property<>(ItbStep.ACTOR_ID, "ACT");
        List<Property<byte[]>> inputs = new ArrayList<>();
        Property<byte[]> input = new Property<>("payload", new byte[]{1, 2});
        inputs.add(input);

        ItbStep step = new ItbStep("itb", testCase, systemId, actorId, inputs);

        assertEquals("itb", step.getName());
        assertEquals(testCase, step.getTestCase());
        assertEquals(systemId, step.getSystemID());
        assertEquals(actorId, step.getActorID());
        assertEquals(ItbStep.TYPE, step.getType());

        List<Property<?>> props = step.getProperties();
        assertTrue(props.contains(testCase));
        assertTrue(props.contains(systemId));
        assertTrue(props.contains(actorId));
        assertTrue(props.contains(input));
    }

    @Test
    void setProperties_updatesCoreFields_and_rejectsNull() {
        ItbStep step = new ItbStep();
        assertThrows(IllegalArgumentException.class, () -> step.setProperties(null));

        Property<String> testCase = new Property<>(ItbStep.TEST_CASE, "TC-2");
        Property<String> systemId = new Property<>(ItbStep.SYSTEM_ID, "SYS-2");
        Property<String> actorId = new Property<>(ItbStep.ACTOR_ID, "ACT-2");
        Property<byte[]> input = new Property<>("payload", new byte[]{3});

        step.setProperties(List.of(testCase, systemId, actorId, input));

        assertEquals(testCase, step.getTestCase());
        assertEquals(systemId, step.getSystemID());
        assertEquals(actorId, step.getActorID());
    }
}
