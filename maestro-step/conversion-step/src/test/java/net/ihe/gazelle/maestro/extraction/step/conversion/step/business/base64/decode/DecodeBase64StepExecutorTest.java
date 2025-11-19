package net.ihe.gazelle.maestro.extraction.step.conversion.step.business.base64.decode;

import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.spi.business.StepRun;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static net.ihe.gazelle.maestro.api.business.testreport.StepResult.DONE;
import static net.ihe.gazelle.maestro.api.business.testreport.StepResult.UNDEFINED;
import static net.ihe.gazelle.maestro.extraction.step.conversion.step.business.base64.decode.DecodeBase64StepDefinition.*;
import static org.junit.jupiter.api.Assertions.*;

class DecodeBase64StepExecutorTest {

    private final DecodeBase64StepExecutor stepExecutor = new DecodeBase64StepExecutor();

    @Test
    void executeStepWithValidBase64Content() {
        String base64Content = "SGVsbG8gV29ybGQh"; // "Hello World!" encoded in Base64
        Step step = buildStep(base64Content);
        StepRun stepRun = new StepRun(step, List.of());

        // When: Executing the step
        StepRunReport stepRunReport = stepExecutor.execute(stepRun).join();

        // Then: The step should succeed with decoded content
        assertEquals(DONE, stepRunReport.getResult());
        assertEquals(1, stepRunReport.getOutputs().size());
        assertTrue(stepRunReport.getOutputs().stream().anyMatch(output ->
                isPropertyEqualsTo(output,"Hello World!")
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
                output.getValue().toString().contains("Content to decode is empty")
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
                output.getValue().toString().contains("Content to decode is empty")
        ));
    }

    @Test
    void executeStepWithInvalidBase64Content() {
        String invalidBase64 = "This is not valid Base64!@#$%";
        Step step = buildStep(invalidBase64);
        StepRun stepRun = new StepRun(step, List.of());

        // When: Executing the step
        StepRunReport stepRunReport = stepExecutor.execute(stepRun).join();

        // Then: The step should fail with a failure message
        assertEquals(UNDEFINED, stepRunReport.getResult());
        assertEquals(1, stepRunReport.getOutputs().size());
        assertTrue(stepRunReport.getOutputs().stream().anyMatch(output ->
                output.getName().equals(FAILURE_MESSAGE) &&
                output.getValue().toString().contains("Failed to decode Base64 content")
        ));
    }

    @Test
    void executeStepWithPaddedBase64Content() {
        String base64WithPadding = "VGVzdA=="; // "Test" encoded in Base64
        Step step = buildStep(base64WithPadding);
        StepRun stepRun = new StepRun(step, List.of());

        // When: Executing the step
        StepRunReport stepRunReport = stepExecutor.execute(stepRun).join();

        // Then: The step should succeed with decoded content
        assertEquals(DONE, stepRunReport.getResult());
        assertEquals(1, stepRunReport.getOutputs().size());
        assertTrue(stepRunReport.getOutputs().stream()
                .anyMatch(output -> isPropertyEqualsTo(output, "Test"))
        );
    }

    @Test
    void executeStepWithSpecialCharacters() {
        String base64SpecialChars = "w6nDqMOgw6fDtQ=="; // "éèàçõ" encoded in Base64
        Step step = buildStep(base64SpecialChars);
        StepRun stepRun = new StepRun(step, List.of());

        // When: Executing the step
        StepRunReport stepRunReport = stepExecutor.execute(stepRun).join();

        // Then: The step should succeed with decoded content
        assertEquals(DONE, stepRunReport.getResult());
        assertEquals(1, stepRunReport.getOutputs().size());
        assertTrue(stepRunReport.getOutputs().stream().anyMatch(output ->isPropertyEqualsTo(output, "éèàçõ")
        ));
    }

    private Step buildStep(String contentToDecode) {
        return new Step()
                .setId("decodeBase64Step")
                .setName("decodeBase64Step")
                .setType(TYPE)
                .setProperties(List.of(
                        new StringProperty(CONTENT_TO_DECODE, contentToDecode)
                ));
    }

    private static boolean isPropertyEqualsTo(Property output, String expectedValue) {
        if (output.getName().equals(DECODED_CONTENT)) {
            String result = new String(output.getValue(), StandardCharsets.UTF_8);
            return result.equals(expectedValue);
        }
        return false;
    }
}

