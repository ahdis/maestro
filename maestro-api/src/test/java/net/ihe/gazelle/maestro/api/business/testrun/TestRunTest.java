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

package net.ihe.gazelle.maestro.api.business.testrun;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestRunTest {

    @Test
    void testSetInputs(){
        TestRun testRun = new TestRun();
        Property<Date> property = new Property<>("date", Date.from(Instant.now()));
        List<Property<?>> properties = List.of(property);
        testRun.setInputs(properties);
        assertEquals(properties, testRun.getInputs());
    }

    @Test
    void setInputsDefensivelyCopiesSourceList() {
        TestRun testRun = new TestRun();
        List<Property<?>> original = new ArrayList<>();
        Property<String> property = new Property<>("key", "value");
        original.add(property);

        testRun.setInputs(original);

        original.add(new Property<>("another", "entry"));

        List<Property<?>> actual = testRun.getInputs();
        assertEquals(1, actual.size());
        assertTrue(actual.contains(property));
    }

    @Test
    void getInputsReturnsCopy() {
        TestRun testRun = new TestRun();
        testRun.addInputs(new Property<>("key", "value"));

        List<Property<?>> firstCall = testRun.getInputs();
        List<Property<?>> secondCall = testRun.getInputs();

        assertNotSame(firstCall, secondCall);

        firstCall.add(new Property<>("unexpected", "data"));

        assertEquals(1, testRun.getInputs().size());
    }

    @Test
    void addInputsAppendsAndReturnsSameInstance() {
        TestRun testRun = new TestRun();
        Property<String> property = new Property<>("key", "value");

        TestRun returned = testRun.addInputs(property);

        assertSame(testRun, returned);
        assertEquals(1, testRun.getInputs().size());
        assertTrue(testRun.getInputs().contains(property));
    }

    @Test
    void setTestSetIdReturnsSameInstance() {
        TestRun testRun = new TestRun();

        TestRun returned = testRun.setTestSetId("identifier");

        assertSame(testRun, returned);
        assertEquals("identifier", testRun.getTestSetId());
    }

    @Test
    void setTestStoresProvidedInstance() {
        TestRun testRun = new TestRun();
        net.ihe.gazelle.maestro.api.business.testrun.Test domainTest = new net.ihe.gazelle.maestro.api.business.testrun.Test();

        testRun.setTest(domainTest);

        assertSame(domainTest, testRun.getTest());
    }

    @Test
    void equals_verifier_test_run() {
        EqualsVerifier.simple().forClass(TestRun.class).verify();
    }

}
