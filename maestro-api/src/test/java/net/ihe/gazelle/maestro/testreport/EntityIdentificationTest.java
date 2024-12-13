package net.ihe.gazelle.maestro.testreport;

import net.ihe.gazelle.testreport.EntityIdentification;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EntityIdentificationTest {

    @Test
    void entity_identification_valid() {
        EntityIdentification entityIdentification = new EntityIdentification()
                .setVersion("1.0")
                .setName("name");
        assertEquals("1.0", entityIdentification.getVersion());
        assertEquals("name", entityIdentification.getName());
        assertTrue(entityIdentification.isValid());
    }

    @Test
    void entity_identification_minimum_valid() {
        EntityIdentification entityIdentification = new EntityIdentification()
                .setName("name");
        assertEquals("name", entityIdentification.getName());
        assertTrue(entityIdentification.isValid());
    }

    @Test
    void entity_identification_not_valid() {
        EntityIdentification entityIdentification = new EntityIdentification()
                .setVersion("1.0");
        assertEquals("1.0", entityIdentification.getVersion());
        assertFalse(entityIdentification.isValid());
    }
}
