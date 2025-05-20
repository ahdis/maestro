package net.ihe.gazelle.maestro.api.business.testreport;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestServiceTest {

    @Test
    void testService_valid() {
        TestService testService = new TestService()
                .setServiceIdentification(
                        new EntityIdentification()
                                .setName("name")
                                .setVersion("1.0"))
                .setDisclaimer("disclaimer");
        assertEquals("name", testService.getServiceIdentification().getName());
        assertEquals("1.0", testService.getServiceIdentification().getVersion());
        assertEquals("disclaimer", testService.getDisclaimer());
        assertTrue(testService.isValid());
    }

    @Test
    void testService_valid_with_unknown_version() {
        TestService testService = new TestService(new EntityIdentification("name"), "disclaimer");
        assertEquals("unknown", testService.getServiceIdentification().getVersion());
        assertTrue(testService.isValid());
    }

    @Test
    void testService_not_valid() {
        TestService testService = new TestService().setDisclaimer("disclaimer");
        assertFalse(testService.isValid());
    }

    @Test
    void testService_not_valid_blank_version() {
        TestService testService = new TestService(new EntityIdentification("name"), "disclaimer");
        testService.getServiceIdentification().setVersion("  ");
        assertFalse(testService.isValid());
    }
}
