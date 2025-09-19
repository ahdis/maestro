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

import static org.junit.jupiter.api.Assertions.*;

class OutputTest {

    @Test
    void item_valid_with_bytes() {
        Output output = new Output("name", "bytes".getBytes(), "format");
        assertEquals("name", output.getName());
        assertArrayEquals("bytes".getBytes(), output.getContent());
        assertTrue(output.isValid());
    }

    @Test
    void item_valid_with_reference() {
        Output output = new Output("name", "reference", "format");
        assertEquals("name", output.getName());
        assertEquals("reference", output.getReference());
        assertTrue(output.isValid());
    }

    @Test
    void item_not_valid_with_all_fields() {
        Output output = new Output()
                .setName("name")
                .setContent("bytes".getBytes())
                .setReference("reference");
        assertEquals("name", output.getName());
        assertArrayEquals("bytes".getBytes(), output.getContent());
        assertEquals("reference", output.getReference());
        assertFalse(output.isValid());
    }

    @Test
    void item_not_valid_with_only_name() {
        Output output = new Output().setName("name");
        assertEquals("name", output.getName());
        assertFalse(output.isValid());
    }
}
