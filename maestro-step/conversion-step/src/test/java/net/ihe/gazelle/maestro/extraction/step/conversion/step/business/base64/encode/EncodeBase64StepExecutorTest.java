package net.ihe.gazelle.maestro.extraction.step.conversion.step.business.base64.encode;

import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.spi.business.StepRun;
import org.junit.jupiter.api.Test;

import java.util.List;

import static net.ihe.gazelle.maestro.api.business.testreport.StepResult.DONE;
import static net.ihe.gazelle.maestro.api.business.testreport.StepResult.UNDEFINED;
import static net.ihe.gazelle.maestro.extraction.step.conversion.step.business.base64.encode.EncodeBase64StepDefinition.*;
import static org.junit.jupiter.api.Assertions.*;

class EncodeBase64StepExecutorTest {

    private final EncodeBase64StepExecutor stepExecutor = new EncodeBase64StepExecutor();

    @Test
    void executeStepWithValidContent() {
        String content = "Hello World!";
        Step step = buildStep(content);
        StepRun stepRun = new StepRun(step, List.of());

        // When: Executing the step
        StepRunReport stepRunReport = stepExecutor.execute(stepRun).join();

        // Then: The step should succeed with encoded content
        assertEquals(DONE, stepRunReport.getResult());
        assertEquals(1, stepRunReport.getOutputs().size());
        assertTrue(stepRunReport.getOutputs().stream().anyMatch(output ->
                output.getName().equals(ENCODED_CONTENT) &&
                output.getValue().equals("SGVsbG8gV29ybGQh")
        ));
    }

    @Test
    void executeStepWithEmptyContent() {
        Step step = buildStep("");
        StepRun stepRun = new StepRun(step, List.of());

        // When: Executing the step
        StepRunReport stepRunReport = stepExecutor.execute(stepRun).join();

        // Then: The step should fail with a failure message
        assertEquals(UNDEFINED, stepRunReport.getResult());
        assertEquals(1, stepRunReport.getOutputs().size());
        assertTrue(stepRunReport.getOutputs().stream().anyMatch(output ->
                output.getName().equals(FAILURE_MESSAGE) &&
                output.getValue().toString().contains("Content to encode is empty")
        ));
    }

    @Test
    void executeStepWithNullContent() {
        Step step = buildStep(null);
        StepRun stepRun = new StepRun(step, List.of());

        // When: Executing the step
        StepRunReport stepRunReport = stepExecutor.execute(stepRun).join();

        // Then: The step should fail with a failure message
        assertEquals(UNDEFINED, stepRunReport.getResult());
        assertEquals(1, stepRunReport.getOutputs().size());
        assertTrue(stepRunReport.getOutputs().stream().anyMatch(output ->
                output.getName().equals(FAILURE_MESSAGE) &&
                output.getValue().toString().contains("Content to encode is empty")
        ));
    }

    @Test
    void executeStepWithSpecialCharacters() {
        String content = "éèàçõ";
        Step step = buildStep(content);
        StepRun stepRun = new StepRun(step, List.of());

        // When: Executing the step
        StepRunReport stepRunReport = stepExecutor.execute(stepRun).join();

        // Then: The step should succeed with encoded content
        assertEquals(DONE, stepRunReport.getResult());
        assertEquals(1, stepRunReport.getOutputs().size());
        assertTrue(stepRunReport.getOutputs().stream().anyMatch(output ->
                output.getName().equals(ENCODED_CONTENT) &&
                output.getValue().equals("w6nDqMOgw6fDtQ==")
        ));
    }

    @Test
    void executeStepWithNumericContent() {
        String content = "1234567890";
        Step step = buildStep(content);
        StepRun stepRun = new StepRun(step, List.of());

        // When: Executing the step
        StepRunReport stepRunReport = stepExecutor.execute(stepRun).join();

        // Then: The step should succeed with encoded content
        assertEquals(DONE, stepRunReport.getResult());
        assertEquals(1, stepRunReport.getOutputs().size());
        assertTrue(stepRunReport.getOutputs().stream().anyMatch(output ->
                output.getName().equals(ENCODED_CONTENT) &&
                output.getValue().equals("MTIzNDU2Nzg5MA==")
        ));
    }

    @Test
    void executeStepWithMultilineContent() {
        String content = "Line 1\nLine 2\nLine 3";
        Step step = buildStep(content);
        StepRun stepRun = new StepRun(step, List.of());

        // When: Executing the step
        StepRunReport stepRunReport = stepExecutor.execute(stepRun).join();

        // Then: The step should succeed with encoded content
        assertEquals(DONE, stepRunReport.getResult());
        assertEquals(1, stepRunReport.getOutputs().size());
        assertTrue(stepRunReport.getOutputs().stream().anyMatch(output ->
                output.getName().equals(ENCODED_CONTENT) &&
                output.getValue() != null &&
                !output.getValue().toString().isEmpty()
        ));
    }

    private Step buildStep(String contentToEncode) {
        return new Step()
                .setId("encodeBase64Step")
                .setName("encodeBase64Step")
                .setType(TYPE)
                .setProperties(List.of(
                        new StringProperty(CONTENT_TO_ENCODE, contentToEncode)
                ));
    }
}

