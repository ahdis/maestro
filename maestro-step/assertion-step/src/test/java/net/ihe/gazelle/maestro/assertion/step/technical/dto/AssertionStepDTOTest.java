package net.ihe.gazelle.maestro.assertion.step.technical.dto;

import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import net.ihe.gazelle.modelmarshaller.technical.serialization.DeserializationException;
import net.ihe.gazelle.modelmarshaller.technical.serialization.TextSerDes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssertionStepDTOTest {

    private static final TextSerDes SER_DES = new JacksonSerDes();


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
}
