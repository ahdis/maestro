package net.ihe.gazelle.maestro.api.business.testreport;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SystemUnderTestTest {

    @Test
    void systemUnderTest_valid() {
        SystemUnderTest systemUnderTest = new SystemUnderTest()
                .setSystemIdentification(new EntityIdentification("name"))
                .addMacAddress("mac")
                .addIpAddress("ip")
                .addHostName("host");
        assertEquals(new EntityIdentification("name"), systemUnderTest.getSystemIdentification());
        assertEquals(List.of("mac"), systemUnderTest.getMacAddresses());
        assertEquals(List.of("ip"), systemUnderTest.getIpAddresses());
        assertEquals(List.of("host"), systemUnderTest.getHostNames());
        assertTrue(systemUnderTest.isValid());
    }

    @Test
    void systemUnderTest_minimum_valid() {
        SystemUnderTest systemUnderTest = new SystemUnderTest(new EntityIdentification("name"));
        assertEquals(new EntityIdentification("name"), systemUnderTest.getSystemIdentification());
        assertTrue(systemUnderTest.isValid());
    }

    @Test
    void systemUnderTest_not_valid_without_identification() {
        SystemUnderTest systemUnderTest = new SystemUnderTest()
                .addMacAddress("mac")
                .addIpAddress("ip")
                .addHostName("host");
        assertFalse(systemUnderTest.isValid());
    }
}
