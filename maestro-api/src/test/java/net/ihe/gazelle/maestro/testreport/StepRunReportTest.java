package net.ihe.gazelle.maestro.testreport;

import net.ihe.gazelle.testreport.Item;
import net.ihe.gazelle.testreport.StepResult;
import net.ihe.gazelle.testreport.StepRunReport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StepRunReportTest {

    @Test
    void stepRun_valid() {
        StepRunReport stepRunReport = new StepRunReport("id", "type", StepResult.PASSED)
                .addOutput(new Item("name", "reference"));
        assertEquals("id", stepRunReport.getStepId());
        assertEquals("type", stepRunReport.getType());
        assertEquals(StepResult.PASSED, stepRunReport.getResult());
        assertEquals(1, stepRunReport.getOutputs().size());
        assertTrue(stepRunReport.isValid());
    }

    @Test
    void stepRun_minimum_valid() {
        StepRunReport stepRunReport = new StepRunReport()
                .setStepId("id")
                .setType("type")
                .setResult(StepResult.PASSED);
        assertEquals("id", stepRunReport.getStepId());
        assertEquals("type", stepRunReport.getType());
        assertEquals(StepResult.PASSED, stepRunReport.getResult());
        assertTrue(stepRunReport.isValid());
    }

    @Test
    void stepRun_not_valid_without_type() {
        StepRunReport stepRunReport = new StepRunReport().setStepId("id");
        assertEquals(StepResult.UNDEFINED, stepRunReport.getResult());
        assertFalse(stepRunReport.isValid());
    }

    @Test
    void stepRun_not_valid_without_result() {
        StepRunReport stepRunReport = new StepRunReport()
                .setStepId("id")
                .setType("type")
                .addOutput(new Item("name", "reference"));
        stepRunReport.setResult(null);
        assertFalse(stepRunReport.isValid());
    }
}
