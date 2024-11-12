package report.model;

import net.ihe.gazelle.report.model.Item;
import net.ihe.gazelle.report.model.StepResult;
import net.ihe.gazelle.report.model.StepRun;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StepRunTest {

    @Test
    void stepRun_valid() {
        StepRun stepRun = new StepRun("id", "type", StepResult.PASSED)
                .addOutput(new Item("name", "reference"));
        assertEquals("id", stepRun.getStepId());
        assertEquals("type", stepRun.getType());
        assertEquals(StepResult.PASSED, stepRun.getResult());
        assertEquals(1, stepRun.getOutputs().size());
        assertTrue(stepRun.isValid());
    }

    @Test
    void stepRun_minimum_valid() {
        StepRun stepRun = new StepRun()
                .setStepId("id")
                .setType("type")
                .setResult(StepResult.PASSED);
        assertEquals("id", stepRun.getStepId());
        assertEquals("type", stepRun.getType());
        assertEquals(StepResult.PASSED, stepRun.getResult());
        assertTrue(stepRun.isValid());
    }

    @Test
    void stepRun_not_valid_without_type() {
        StepRun stepRun = new StepRun().setStepId("id");
        assertEquals(StepResult.UNDEFINED, stepRun.getResult());
        assertFalse(stepRun.isValid());
    }

    @Test
    void stepRun_not_valid_without_result() {
        StepRun stepRun = new StepRun()
                .setStepId("id")
                .setType("type")
                .addOutput(new Item("name", "reference"));
        stepRun.setResult(null);
        assertFalse(stepRun.isValid());
    }
}
