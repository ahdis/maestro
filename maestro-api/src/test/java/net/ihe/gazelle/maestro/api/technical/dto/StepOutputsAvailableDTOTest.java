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

package net.ihe.gazelle.maestro.api.technical.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ihe.gazelle.maestro.api.business.message.StepOutputsAvailable;
import net.ihe.gazelle.maestro.api.business.property.ByteArrayItemProperty;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.technical.dto.message.StepOutputsAvailableDTO;
import net.ihe.gazelle.maestro.api.technical.dto.property.ByteArrayItemPropertyDTO;
import net.ihe.gazelle.maestro.api.technical.dto.property.StringPropertyDTO;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StepOutputsAvailableDTOTest {

    @Test
    void mixedOutputsKeepTheirTypesAndRecordedReferences() throws Exception {
        ByteArrayItemProperty document = new ByteArrayItemProperty();
        document.setName("document");
        document.setReference("/attachments/document-1");
        StepOutputsAvailable message = new StepOutputsAvailable("test-1", "Upload",
                Instant.parse("2026-11-05T10:00:00Z"),
                List.of(new StringProperty("note", "Ready"), document));
        StepOutputsAvailableDTO dto = new StepOutputsAvailableDTO(message);

        assertInstanceOf(StringPropertyDTO.class, dto.getOutputs().getFirst());
        assertInstanceOf(ByteArrayItemPropertyDTO.class, dto.getOutputs().getLast());
        JsonNode json = new ObjectMapper().readTree(new JacksonSerDes().serializeAsString(dto));
        assertEquals("STEP_OUTPUTS_AVAILABLE", json.path("type").asText());
        assertEquals("test-1", json.path("testId").asText());
        assertEquals("Upload", json.path("stepName").asText());
        assertEquals("2026-11-05T10:00:00Z", json.path("dateTime").asText());
        assertEquals(2, json.path("outputs").size());
        assertEquals("STRING", json.path("outputs").get(0).path("type").asText());
        assertEquals("Ready", json.path("outputs").get(0).path("value").asText());
        assertEquals("BYTE_ARRAY_ITEM", json.path("outputs").get(1).path("type").asText());
        assertEquals("/attachments/document-1", json.path("outputs").get(1).path("reference").asText());
        assertFalse(json.path("outputs").get(1).has("value"));
    }

    @Test
    void emptyNotificationHasNoOutputsOrTimestamp() {
        StepOutputsAvailableDTO dto = new StepOutputsAvailableDTO();

        assertTrue(dto.getOutputs().isEmpty());
        assertNull(dto.getDateTime());
        assertEquals(StepOutputsAvailable.class, dto.getBusinessObjectClass());
    }
}
