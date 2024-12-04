package net.ihe.gazelle.maestro.technical.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ihe.gazelle.maestro.Property;
import net.ihe.gazelle.maestro.Step;
import net.ihe.gazelle.maestro.TestRun;
import net.ihe.gazelle.maestro.business.step.validation.ValidationStep;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class TestRunParserTest {

    TestRunParser testRunParser = new TestRunParser();

    @Test
    void testParse() throws IOException {

        Scanner scanner = new Scanner(new File("src/it/resources/Testrun.json"), StandardCharsets.UTF_8);

        String content = scanner.useDelimiter("\\Z").next();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readTree(content);
        List<TestRun> testRuns = testRunParser.convertJsonToTestRuns(node);

        assertNotNull(testRuns);
        assertFalse(testRuns.isEmpty());
        TestRun testRun = testRuns.getFirst();
        assertNotNull(testRun);
        assertNotNull(testRun.getTest());
        assertEquals("1", testRun.getTest().getId());
        assertEquals("name1", testRun.getTest().getName());
        List<Step> steps = testRun.getTest().getSteps();
        assertNotNull(steps);
        assertFalse(steps.isEmpty());
        Step step = steps.getFirst();
        assertNotNull(step);
        assertInstanceOf(ValidationStep.class, step);
        assertEquals(ValidationStep.TYPE, step.getType());
        assertEquals("step1", step.getName());
        List<Property<?>> properties = step.getProperties();
        assertNotNull(properties);
        assertEquals(3, properties.size());
        ValidationStep validationStep = (ValidationStep) step;
        Property<String> validationService = validationStep.getValidationService();
        Property<String> validationProfile = validationStep.getValidationProfile();
        Property<byte[]> contentToValidate = validationStep.getContentToValidate();
        assertEquals(ValidationStep.VALIDATION_SERVICE, validationService.getName());
        assertEquals("matchbox", validationService.getValue());
        assertEquals(ValidationStep.VALIDATION_PROFILE, validationProfile.getName());
        assertEquals("parameters", validationProfile.getValue());
        assertEquals(ValidationStep.CONTENT_TO_VALIDATE, contentToValidate.getName());
        assertThrows(IllegalStateException.class, contentToValidate::getValue);

    }

    @Test
    void testParseInvalid() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readTree("");

        assertThrows(ParsingErrorException.class, () -> testRunParser.convertJsonToTestRuns(node));
    }

}
