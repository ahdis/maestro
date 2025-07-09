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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SystemUnderTestTest {

    @Test
    void systemUnderTest_valid() {
        SystemUnderTest systemUnderTest = new SystemUnderTest()
                .setSystemIdentification(new EntityIdentification("name"))
                .addMacAddress("mac")
                .addIpAddress("ip")
                .addHostName("host");
        assertEquals(new EntityIdentification("name"), systemUnderTest.getSystemIdentification());
        assertEquals(List.of("mac"), systemUnderTest.getMacAddresses());
        assertEquals(List.of("ip"), systemUnderTest.getIpAddresses());
        assertEquals(List.of("host"), systemUnderTest.getHostNames());
        assertTrue(systemUnderTest.isValid());
    }

    @Test
    void systemUnderTest_minimum_valid() {
        SystemUnderTest systemUnderTest = new SystemUnderTest(new EntityIdentification("name"));
        assertEquals(new EntityIdentification("name"), systemUnderTest.getSystemIdentification());
        assertTrue(systemUnderTest.isValid());
    }

    @Test
    void systemUnderTest_not_valid_without_identification() {
        SystemUnderTest systemUnderTest = new SystemUnderTest()
                .addMacAddress("mac")
                .addIpAddress("ip")
                .addHostName("host");
        assertFalse(systemUnderTest.isValid());
    }
}
