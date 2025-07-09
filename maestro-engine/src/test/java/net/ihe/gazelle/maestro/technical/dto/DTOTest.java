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

package net.ihe.gazelle.maestro.technical.dto;

import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.PropertyRef;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.technical.dto.testrun.AssertionStepDTO;
import net.ihe.gazelle.maestro.technical.dto.testrun.PropertyDTO;
import net.ihe.gazelle.maestro.technical.dto.testrun.TestRunDTO;
import net.ihe.gazelle.maestro.technical.dto.testrun.ValidationStepDTO;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import net.ihe.gazelle.modelmarshaller.technical.serialization.DeserializationException;
import net.ihe.gazelle.modelmarshaller.technical.serialization.TextSerDes;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DTOTest {

    private static final TextSerDes SER_DES = new JacksonSerDes();

    @Test
    void should_deserialize_serialize() {
        List<TestRun> testRuns = assertDoesNotThrow(() -> Arrays.stream(SER_DES.deserialize(getTestRun("valid"), TestRunDTO[].class))
                .map(TestRunDTO::getDomain)
                .toList());

        assertEquals(1, testRuns.size());
        assertEquals(5, testRuns.getFirst().getTest().getSteps().size());

        List<TestRunDTO> testRunDTOS = testRuns.stream().map(TestRunDTO::new).toList();
        assertDoesNotThrow(() -> SER_DES.serializeAsString(testRunDTOS));
    }

    @Test
    void should_throw_unsupported_property_type_on_deserialization() throws IOException {
        String json = getTestRun("wrong_property_type");
        DeserializationException de = assertThrows(DeserializationException.class, () -> SER_DES.deserialize(json, TestRunDTO[].class));
        assertEquals(UnsupportedPropertyTypeException.class, de.getCause().getCause().getClass());
        UnsupportedPropertyTypeException upe = (UnsupportedPropertyTypeException) de.getCause().getCause();
        assertTrue(upe.getMessage().contains("Unsupported property type"));
    }

    @Test
    void should_throw_unsupported_property_type_on_constructor() {
        Property<Object> property = new Property<>()
                .setName("testProperty")
                .setValue(new PropertyDTO());
        UnsupportedPropertyTypeException upe = assertThrows(UnsupportedPropertyTypeException.class, () -> new PropertyDTO(property));
        assertTrue(upe.getMessage().contains("Unsupported property type"));
    }

    @Test
    void should_throw_unsupported_property_type_on_reference() {
        PropertyRef<Object> property = new PropertyRef<>("testProperty", "reference", Object.class);
        UnsupportedPropertyTypeException upe = assertThrows(UnsupportedPropertyTypeException.class, () -> new PropertyDTO(property));
        assertTrue(upe.getMessage().contains("Unsupported property type for reference"));
    }

    @Test
    void should_throw_unsupported_validation_property_name() {
        String json = """
                {
                  "type": "VALIDATION",
                  "name": "test",
                  "properties": [
                    {
                      "name": "wrong",
                      "value": "string",
                      "type": "string"
                    }
                  ]
                }
                """;
        DeserializationException de = assertThrows(DeserializationException.class, () -> SER_DES.deserialize(json, ValidationStepDTO.class));
        assertEquals(IllegalArgumentException.class, de.getCause().getCause().getClass());
        IllegalArgumentException iae = (IllegalArgumentException) de.getCause().getCause();
        assertTrue(iae.getMessage().contains("is not a validation step property"));
    }

    @Test
    void should_throw_unsupported_assertion_property_name() {
        String json = """
                {
                  "type": "ASSERTION",
                  "name": "test",
                  "properties": [
                    {
                      "name": "wrong",
                      "value": "string",
                      "type": "string"
                    }
                  ]
                }
                """;
        DeserializationException de = assertThrows(DeserializationException.class, () -> SER_DES.deserialize(json, AssertionStepDTO.class));
        assertEquals(IllegalArgumentException.class, de.getCause().getCause().getClass());
        IllegalArgumentException iae = (IllegalArgumentException) de.getCause().getCause();
        assertTrue(iae.getMessage().contains("is not an assertion step property"));
    }

    @Test
    void should_throw_on_wrong_date_format() {
        String json = """
                {
                  "type": "MOCK",
                  "name": "dateProperty",
                  "properties": [
                    {
                      "name": "dateProperty",
                      "value": "20/05/25",
                      "type": "date"
                    }
                  ]
                }
                """;
        DeserializationException de = assertThrows(DeserializationException.class, () -> SER_DES.deserialize(json, MockStepDTO.class));
        assertEquals(IllegalArgumentException.class, de.getCause().getCause().getClass());
        IllegalArgumentException iae = (IllegalArgumentException) de.getCause().getCause();
        assertTrue(iae.getMessage().contains("Invalid date format"));
    }

    private static String getTestRun(String name) throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/test/resources/testRun/" + name + ".json")));
    }
}
