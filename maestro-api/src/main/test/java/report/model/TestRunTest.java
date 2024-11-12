package report.model;

import net.ihe.gazelle.report.model.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class TestRunTest {

    @Test
    void testRun_valid() {
        TestRun testRun = buildTestRun();
        assertEquals("id", testRun.getRunId());
        assertEquals(Result.UNDEFINED, testRun.getResult());
        assertEquals("id", testRun.getTest().getId());
        assertEquals(1, testRun.getInputs().size());
        assertEquals(1, testRun.getOutputs().size());
        assertEquals(1, testRun.getStepRuns().size());
        assertEquals("urlToTestRun", testRun.getUrlToTestRun());
        assertEquals(0, testRun.getUnexpectedErrors().size());
        testRun.computeResult();
        assertTrue(testRun.isValid());
    }

    @Test
    void testRun_valid_compute_result() {
        TestRun testRun = buildTestRun();
        testRun.computeResult();
        assertEquals(Result.PASSED, testRun.getResult());
        testRun.addStepRun(new StepRun("id", "type", StepResult.FAILED));
        testRun.computeResult();
        assertEquals(Result.FAILED, testRun.getResult());
        testRun.addStepRun(new StepRun("id", "type", StepResult.UNDEFINED));
        testRun.computeResult();
        assertEquals(Result.UNDEFINED, testRun.getResult());
        assertTrue(testRun.isValid());
    }

    @Test
    void testRun_valid_compute_result_with_done() {
        TestRun testRun = buildTestRun();
        testRun.computeResult();
        assertEquals(Result.PASSED, testRun.getResult());
        testRun.addStepRun(new StepRun("id", "type", StepResult.DONE));
        testRun.computeResult();
        assertEquals(Result.UNDEFINED, testRun.getResult());
        assertTrue(testRun.isValid());
    }

    @Test
    void testRun_valid_compute_result_with_errors() {
        TestRun testRun = buildTestRun();
        testRun.computeResult();
        assertEquals(Result.PASSED, testRun.getResult());
        testRun.addUnexpectedError(
                new UnexpectedError()
                        .setName("name")
                        .setMessage("message")
        );
        assertEquals(1, testRun.getUnexpectedErrors().size());
        testRun.computeResult();
        assertEquals(Result.UNDEFINED, testRun.getResult());
        assertTrue(testRun.isValid());
    }

    @Test
    void testRun_valid_with_no_result() {
        TestRun testRun = new TestRun()
                .setDateTime(new Date())
                .setTest(new net.ihe.gazelle.report.model.Test("id"));
        testRun.computeResult();
        assertEquals(Result.UNDEFINED, testRun.getResult());
        assertTrue(testRun.isValid());
    }

    @Test
    void testRun_not_valid() {
        TestRun testRun = new TestRun();
        assertFalse(testRun.isValid());
    }

    private TestRun buildTestRun() {
        return new TestRun()
                .setRunId("id")
                .setDateTime(new Date())
                .setTest(new net.ihe.gazelle.report.model.Test("id"))
                .addInput(new Item("input", "reference"))
                .addOutput(new Item("output", "reference"))
                .addStepRun(new StepRun("id", "type", StepResult.PASSED))
                .setUrlToTestRun("urlToTestRun");
    }
}
