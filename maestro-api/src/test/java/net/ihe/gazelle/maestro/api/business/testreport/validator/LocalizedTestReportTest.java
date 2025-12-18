package net.ihe.gazelle.maestro.api.business.testreport.validator;

import net.ihe.gazelle.maestro.api.business.testreport.LocalizedTestReport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LocalizedTestReportTest {

    @Test
    void testConstructorSetsLocation() {
        String location = "/path/to/report";
        LocalizedTestReport report = new LocalizedTestReport(location);

        assertEquals(location, report.getLocation());
        assertNotNull(report.getDateTime());
        assertNotNull(report.getReportVersion());
    }

    @Test
    void testSetLocation() {
        LocalizedTestReport report = new LocalizedTestReport("initial");
        String newLocation = "/new/path/report.json";

        report.setLocation(newLocation);
        assertEquals(newLocation, report.getLocation());
    }


    @Test
    void testEqualsWithNull() {
        LocalizedTestReport report = new LocalizedTestReport("/test");
        assertNotEquals(null, report);
    }


    @Test
    void testHashCodeWithDifferentLocation() {
        LocalizedTestReport report1 = new LocalizedTestReport("/location1");
        LocalizedTestReport report2 = new LocalizedTestReport("/location2");

        report1.setUuid("uuid1");
        report2.setUuid("uuid1");
        assertNotEquals(report1.hashCode(), report2.hashCode());
    }

    @Test
    void testLocationCanBeNull() {
        LocalizedTestReport report = new LocalizedTestReport(null);
        assertNull(report.getLocation());
    }
}
