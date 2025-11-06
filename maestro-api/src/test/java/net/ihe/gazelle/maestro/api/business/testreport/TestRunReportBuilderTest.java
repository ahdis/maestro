package net.ihe.gazelle.maestro.api.business.testreport;

import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TestRunReportBuilderTest {

    @Test
    void shouldBuildTestRunReportWithStepRuns() {
        Instant timestamp = Instant.parse("2025-01-01T00:00:00Z");

        StepRunReportBuilder stepRunReportBuilder = new StepRunReportBuilder()
                .setStepId("step-1")
                .setStepName("Validation step")
                .setType("VALIDATION")
                .setResult(StepResult.PASSED)
                .addOutput(new StringProperty("output", "value"));

        TestRunReportBuilder builder = new TestRunReportBuilder()
                .setRunId("run-42")
                .setDateTime(timestamp)
                .setTest(new TestBuilder()
                        .setId("test-1")
                        .setName("Sample test"))
                .addInput(new StringProperty("input", "example"))
                .addOutput(new StringProperty("result", "ok"))
                .addStepRunReport(stepRunReportBuilder)
                .setUrlToTestRun("http://example.test/run-42");

        TestRunReport report = builder.build();

        assertEquals("run-42", report.getRunId());
        assertEquals(timestamp, report.getDateTime());
        assertEquals("test-1", report.getTest().getId());
        assertEquals(1, report.getInputs().size());
        assertEquals("input", report.getInputs().getFirst().getName());
        assertEquals(1, report.getOutputs().size());
        assertEquals(Result.PASSED, report.getResult());

        assertEquals(1, report.getStepRunReports().size());
        StepRunReport stepRunReport = report.getStepRunReports().getFirst();
        assertEquals("step-1", stepRunReport.getStepId());
        assertEquals(StepResult.PASSED, stepRunReport.getResult());
        assertEquals(1, stepRunReport.getOutputs().size());
        assertNotNull(report.getUrlToTestRun());
    }
}
