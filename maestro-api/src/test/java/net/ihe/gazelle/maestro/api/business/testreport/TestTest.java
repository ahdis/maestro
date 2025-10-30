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

package net.ihe.gazelle.maestro.api.business.testreport;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestTest {

    @Test
    void test_valid() {
        net.ihe.gazelle.maestro.api.business.testreport.Test test = new net.ihe.gazelle.maestro.api.business.testreport.Test()
                .setId("id")
                .setName("name")
                .setVersion("version")
                .setDescription("description");
        assertEquals("id", test.getId());
        assertEquals("name", test.getName());
        assertEquals("version", test.getVersion());
        assertEquals("description", test.getDescription());
        assertTrue(test.isValid());
    }

    @Test
    void test_minimum_valid() {
        net.ihe.gazelle.maestro.api.business.testreport.Test test = new net.ihe.gazelle.maestro.api.business.testreport.Test("id");
        assertEquals("id", test.getId());
        assertTrue(test.isValid());
    }

    @Test
    void test_not_valid_without_id() {
        net.ihe.gazelle.maestro.api.business.testreport.Test test = new net.ihe.gazelle.maestro.api.business.testreport.Test()
                .setName("name")
                .setVersion("version")
                .setDescription("description");
        assertFalse(test.isValid());
    }
}
