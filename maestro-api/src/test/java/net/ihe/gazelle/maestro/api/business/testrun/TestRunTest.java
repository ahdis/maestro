package net.ihe.gazelle.maestro.api.business.testrun;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestRunTest {

    @Test
    void testSetInputs(){
        TestRun testRun = new TestRun();
        Property<Date> property = new Property<>("date", Date.from(Instant.now()));
        List<Property<?>> properties = List.of(property);
        testRun.setInputs(properties);
        assertEquals(properties, testRun.getInputs());
    }

    @Test
    void equals_verifier_test_run() {
        EqualsVerifier.simple().forClass(TestRun.class).verify();
    }

}
