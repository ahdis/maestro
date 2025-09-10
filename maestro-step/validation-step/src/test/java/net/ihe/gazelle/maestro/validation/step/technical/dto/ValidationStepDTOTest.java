package net.ihe.gazelle.maestro.validation.step.technical.dto;

import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import net.ihe.gazelle.modelmarshaller.technical.serialization.DeserializationException;
import net.ihe.gazelle.modelmarshaller.technical.serialization.TextSerDes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationStepDTOTest {

    private static final TextSerDes SER_DES = new JacksonSerDes();


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
        de.printStackTrace();
        assertEquals(IllegalArgumentException.class, de.getCause().getCause().getClass());
        IllegalArgumentException iae = (IllegalArgumentException) de.getCause().getCause();
        assertTrue(iae.getMessage().contains("is not a validation step property"));
    }

}
