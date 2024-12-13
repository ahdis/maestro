package net.ihe.gazelle.maestro;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestRunTest {

    @Test
    void testSetInputs(){
        TestRun testRun = new TestRun();
        Property<Date> property = new Property<>("date", Date.from(Instant.now()));
        List<Property<?>> properties = Arrays.asList(property);
        testRun.setInputs(properties);
        assertEquals(properties, testRun.getInputs());
    }

}
