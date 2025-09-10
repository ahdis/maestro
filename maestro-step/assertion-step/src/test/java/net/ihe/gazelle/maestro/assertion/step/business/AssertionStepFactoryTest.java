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

package net.ihe.gazelle.maestro.assertion.step.business;

import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.Step;
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
        Property<byte[]> assertion = new Property<>(AssertionStep.ASSERTION, "value".getBytes());
        Property<String> assertionService = new Property<>(AssertionStep.ASSERTION_SERVICE, "value");
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
