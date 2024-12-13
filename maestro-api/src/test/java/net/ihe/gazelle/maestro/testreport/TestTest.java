package net.ihe.gazelle.maestro.testreport;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestTest {

    @Test
    void test_valid() {
        net.ihe.gazelle.testreport.Test test = new net.ihe.gazelle.testreport.Test()
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
        net.ihe.gazelle.testreport.Test test = new net.ihe.gazelle.testreport.Test("id");
        assertEquals("id", test.getId());
        assertTrue(test.isValid());
    }

    @Test
    void test_not_valid_without_id() {
        net.ihe.gazelle.testreport.Test test = new net.ihe.gazelle.testreport.Test()
                .setName("name")
                .setVersion("version")
                .setDescription("description");
        assertFalse(test.isValid());
    }
}
