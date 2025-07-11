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
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void equals_verifier_test_run() {
        EqualsVerifier.simple().forClass(TestRun.class).verify();
    }

}
