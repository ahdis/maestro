package net.ihe.gazelle.maestro.technical.ws.tmintegration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import net.ihe.gazelle.maestro.Property;
import net.ihe.gazelle.maestro.Step;
import net.ihe.gazelle.maestro.Test;
import net.ihe.gazelle.maestro.TestRun;
import net.ihe.gazelle.maestro.technical.StepFactoryProvider;
import net.ihe.gazelle.maestro.technical.dto.PropertyDTO;
import net.ihe.gazelle.maestro.technical.dto.ReferenceParser;
import net.ihe.gazelle.maestro.technical.ws.MissingArgumentException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is temporary, used for converting TM script to maestro script
 * We store the clientCallback URL and authorization token until the end of engine execution
 */
public class TMScriptMapper {

    public static List<TestRun> convertToTestRun(JsonNode jsonNode, TMTestRunStore testRunDAO) {
        ArrayNode testRunNodes = (ArrayNode) jsonNode;
        List<TestRun> testRuns = new ArrayList<>();
        for (JsonNode testRunNode : testRunNodes) {
            try {
                testRunDAO.setClientCallback(testRunNode.get("clientCallback").asText());
                testRunDAO.setCallbackAuthorization(testRunNode.get("callbackAuthorization").asText());
                TestRun testRun = new TestRun();
                JsonNode testNode = testRunNode.get("test");
                parseTest(testNode, testRun);
                ArrayNode inputNodes = (ArrayNode) testRunNode.get("inputs");
                parseInputs(inputNodes, testRun);
                testRun.setTestSetId(testRun.getTest().getName());
                testRuns.add(testRun);
            } catch (NullPointerException e) {
                throw new MissingArgumentException("Argument inputs is missing in testRun");
            }
        }
        return testRuns;
    }

    private static void parseTest(JsonNode testNode, TestRun testRun) {
        try {
            Test test = new Test();
            String name = testNode.get("name").asText();
            test.setId(name);
            test.setName(name);
            ArrayNode stepNodes = (ArrayNode) testNode.get("steps");
            parseStep(stepNodes, test);
            testRun.setTest(test);
        } catch (NullPointerException e) {
            throw new MissingArgumentException("Argument inputs is missing in test");
        }
    }

    private static void parseStep(ArrayNode stepNodes, Test test) {
        for (JsonNode stepNode : stepNodes) {
            try {
                String name = stepNode.get("id").asText();
                String type = stepNode.get("@type").asText();
                ArrayNode propertyNodes = (ArrayNode) stepNode.get("properties");
                StepFactoryProvider provider = new StepFactoryProvider();
                Step step = provider.create(type, name, parseProperties(propertyNodes));
                test.addStep(step);
            } catch (NullPointerException e) {
                throw new MissingArgumentException("Argument inputs is missing in steps");
            }
        }
    }

    private static Map<String, Property<?>> parseProperties(ArrayNode propertyNodes) {
        Map<String, Property<?>> properties = new HashMap<>();
        for (JsonNode propertyNode : propertyNodes) {
            try {
                String key = propertyNode.get("key").asText();
                String value = propertyNode.get("value").asText();
                ReferenceParser referenceParser = new ReferenceParser();
                if (referenceParser.isReference(value)) {
                    properties.put(key, new PropertyDTO(key, value, "byteArray").createPropertyRef(referenceParser.getReference(value)));
                } else  {
                    properties.put(key, new PropertyDTO(key, value, "string").createProperty());
                }
            } catch (NullPointerException e) {
                throw new MissingArgumentException("Argument inputs is missing in properties");
            }
        }
        return properties;
    }

    private static void parseInputs(ArrayNode inputNodes, TestRun testRun) {
        for (JsonNode inputNode : inputNodes) {
            try {
                PropertyDTO propertyDTO = new PropertyDTO(
                        inputNode.get("key").asText(),
                        inputNode.get("value").asText(),
                        "byteArray");
                testRun.addInputs(propertyDTO.createProperty());
            } catch (NullPointerException e) {
                throw new MissingArgumentException("Argument inputs is missing in inputs");
            }
        }
    }
}
