package net.ihe.gazelle.maestro.business.step.assertion;

import net.ihe.gazelle.maestro.Property;
import net.ihe.gazelle.maestro.Step;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AssertionStepFactoryTest {

    AssertionStepFactory assertionStepFactory;

    @BeforeEach
    public void setUp() {
        assertionStepFactory = new AssertionStepFactory();
    }

    @Test
    void createTest(){
        String name = "test";
        Property<byte[]> assertion = new Property<>(AssertionStep.ASSERTION, "saucisse".getBytes());
        Property<String> assertionService = new Property<>(AssertionStep.ASSERTION_SERVICE, "puré");
        Property<Integer> dummyProperty = new Property<>("dummyField", 1);
        Map<String, Property<?>> properties = new HashMap<>();
        properties.put(AssertionStep.ASSERTION, assertion);
        properties.put(AssertionStep.ASSERTION_SERVICE, assertionService);
        properties.put(dummyProperty.getName(), dummyProperty);
        Step assertionStep =  assertionStepFactory.create(name, properties);
        assertNotNull(assertionStep);
        assertEquals(name, assertionStep.getName());
        assertInstanceOf(AssertionStep.class, assertionStep);
        List<Property<?>> propertiesRes = assertionStep.getProperties();
        assertTrue(propertiesRes.contains(assertion));
        assertTrue(propertiesRes.contains(assertionService));
        assertFalse(propertiesRes.contains(dummyProperty));
    }

    @Test
    void getSupportedStepTest(){
        assertEquals(AssertionStep.TYPE, assertionStepFactory.getSupportedStep());
    }

}
