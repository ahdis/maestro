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

package net.ihe.gazelle.maestro.api.business.testreport;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestCountersTest {

    @Test
    void testCounters_valid() {
        TestCounters testCounters = new TestCounters();
        assertEquals(0, testCounters.getTotal());
        assertEquals(0, testCounters.getPassed());
        assertEquals(0, testCounters.getFailed());
        assertEquals(0, testCounters.getUndefined());
        assertEquals(0, testCounters.getUnexpectedErrors());
        assertTrue(testCounters.isValid());
    }

    @Test
    void testCounters_not_valid_bad_total() {
        TestCounters testCounters = new TestCounters()
                .setTotal(3)
                .setPassed(1)
                .setFailed(1);
        assertFalse(testCounters.isValid());
    }

    @Test
    void increment_counters_still_valid() {
        TestCounters testCounters = new TestCounters();
        testCounters.incrementPassed();
        testCounters.incrementFailed();
        testCounters.incrementUndefined();
        testCounters.incrementUnexpectedErrors();
        assertEquals(3, testCounters.getTotal());
        assertEquals(1, testCounters.getPassed());
        assertEquals(1, testCounters.getFailed());
        assertEquals(1, testCounters.getUndefined());
        assertEquals(1, testCounters.getUnexpectedErrors());
        assertTrue(testCounters.isValid());
    }

    @Test
    void increment_from_sub_counters() {
        TestCounters testCounters = new TestCounters();
        testCounters.incrementPassed();
        TestCounters subCounters = new TestCounters();
        subCounters.incrementFailed();
        testCounters.addNumbersFromSubCounters(subCounters);
        assertEquals(2, testCounters.getTotal());
        assertEquals(1, testCounters.getPassed());
        assertEquals(1, testCounters.getFailed());
        assertEquals(0, testCounters.getUndefined());
        assertEquals(0, testCounters.getUnexpectedErrors());
        assertTrue(testCounters.isValid());
    }
}
