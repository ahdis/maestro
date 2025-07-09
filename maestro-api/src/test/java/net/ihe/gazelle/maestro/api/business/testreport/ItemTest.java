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

class ItemTest {

    @Test
    void item_valid_with_bytes() {
        Item item = new Item("name", "bytes".getBytes());
        assertEquals("name", item.getName());
        assertArrayEquals("bytes".getBytes(), item.getContent());
        assertTrue(item.isValid());
    }

    @Test
    void item_valid_with_reference() {
        Item item = new Item("name", "reference");
        assertEquals("name", item.getName());
        assertEquals("reference", item.getReference());
        assertTrue(item.isValid());
    }

    @Test
    void item_not_valid_with_all_fields() {
        Item item = new Item()
                .setName("name")
                .setContent("bytes".getBytes())
                .setReference("reference");
        assertEquals("name", item.getName());
        assertArrayEquals("bytes".getBytes(), item.getContent());
        assertEquals("reference", item.getReference());
        assertFalse(item.isValid());
    }

    @Test
    void item_not_valid_with_only_name() {
        Item item = new Item().setName("name");
        assertEquals("name", item.getName());
        assertFalse(item.isValid());
    }
}
