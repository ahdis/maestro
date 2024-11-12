package report.model;

import net.ihe.gazelle.report.model.TestCounters;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestCountersTest {

    @Test
    void testCounters_valid() {
        TestCounters testCounters = new TestCounters();
        assertEquals(0, testCounters.getTotal());
        assertEquals(0, testCounters.getPassed());
        assertEquals(0, testCounters.getFailed());
        assertEquals(0, testCounters.getUndefined());
        assertEquals(0, testCounters.getUnexpectedErrors());
        assertTrue(testCounters.isValid());
    }

    @Test
    void testCounters_not_valid_bad_total() {
        TestCounters testCounters = new TestCounters()
                .setTotal(3)
                .setPassed(1)
                .setFailed(1);
        assertFalse(testCounters.isValid());
    }

    @Test
    void increment_counters_still_valid() {
        TestCounters testCounters = new TestCounters();
        testCounters.incrementPassed();
        testCounters.incrementFailed();
        testCounters.incrementUndefined();
        testCounters.incrementUnexpectedErrors();
        assertEquals(3, testCounters.getTotal());
        assertEquals(1, testCounters.getPassed());
        assertEquals(1, testCounters.getFailed());
        assertEquals(1, testCounters.getUndefined());
        assertEquals(1, testCounters.getUnexpectedErrors());
        assertTrue(testCounters.isValid());
    }

    @Test
    void increment_from_sub_counters() {
        TestCounters testCounters = new TestCounters();
        testCounters.incrementPassed();
        TestCounters subCounters = new TestCounters();
        subCounters.incrementFailed();
        testCounters.addNumbersFromSubCounters(subCounters);
        assertEquals(2, testCounters.getTotal());
        assertEquals(1, testCounters.getPassed());
        assertEquals(1, testCounters.getFailed());
        assertEquals(0, testCounters.getUndefined());
        assertEquals(0, testCounters.getUnexpectedErrors());
        assertTrue(testCounters.isValid());
    }
}
