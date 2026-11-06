/*
 * Copyright 2025-2026 IHE International.
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

package net.ihe.gazelle.maestro.api.business.property;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ByteArrayItemPropertyTest {
    @Test
    void copyPreservesRecordedReferenceSubtypeAndMetadata() {
        Instant date = Instant.parse("2026-11-03T12:00:00Z");
        var original = new ByteArrayItemProperty("report", null, "reviewer", date)
                .setItemType("VALIDATION_REPORT").setReference("/items/report-1");
        original.setFileName("validation.json");
        original.setMimeType("application/json");
        var copy = assertInstanceOf(ByteArrayItemProperty.class, original.copy());
        assertNotSame(original, copy);
        assertNull(copy.getValue());
        assertEquals(original.getReference(), copy.getReference());
        assertEquals(original.getItemType(), copy.getItemType());
        assertEquals(original.getFileName(), copy.getFileName());
        assertEquals(original.getMimeType(), copy.getMimeType());
        assertEquals(original.getUserId(), copy.getUserId());
        assertEquals(date, copy.getDateTime());
        copy.setReference("/items/other");
        assertEquals("/items/report-1", original.getReference());
    }
}
