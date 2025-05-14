package net.ihe.gazelle.maestro.technical.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import net.ihe.gazelle.maestro.Property;
import net.ihe.gazelle.maestro.Step;
import net.ihe.gazelle.maestro.Test;
import net.ihe.gazelle.maestro.TestRun;
import net.ihe.gazelle.maestro.technical.dto.PropertyDTO;
import net.ihe.gazelle.maestro.technical.dto.ReferenceParser;
import net.ihe.gazelle.maestro.technical.dto.StepMarshaller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestRunParser {

    public List<TestRun> convertJsonToTestRuns(JsonNode node) throws MissingArgumentException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return parseTestRuns(node, mapper);
        } catch (Exception e) {
            throw new ParsingErrorException(e);
        }
    }

    private static List<TestRun> parseTestRuns(JsonNode node, ObjectMapper mapper) throws IOException, MissingArgumentException {
        try {
            ArrayNode testRunNodes;
            if (node.isArray()) {
                testRunNodes = (ArrayNode) node;
            } else {
                throw new MissingArgumentException("Argument testRuns is missing in testRun");
            }
            List<TestRun> testRuns = new ArrayList<>();
            for(JsonNode testRunNode : testRunNodes) {
                TestRun testRun = new TestRun();
                testRun.setTest(parseTest(testRunNode));
                testRun.setInputs(parseInputs(testRunNode, mapper));
                testRuns.add(testRun);
            }
            return testRuns;
        } catch (NullPointerException | MissingArgumentException e) {
            throw new MissingArgumentException(e);
        }
    }

    private static Test parseTest(JsonNode testRunNode) throws MissingArgumentException {
        try {
            JsonNode testNode = testRunNode.get("test");
            Test test = new Test();
            getId(test, testNode);
            getName(test, testNode);
            test.setSteps(parseSteps(testNode));
            return test;
        } catch (NullPointerException e) {
            throw new MissingArgumentException("Argument test is missing in testRun");
        }
    }

    private static void getName(Test test, JsonNode testNode) {
        try {
            test.setName(testNode.get("name").asText());
        } catch (NullPointerException e) {
            throw new MissingArgumentException("Argument name not found in test");
        }
    }

    private static void getId(Test test, JsonNode testNode) {
        try {
            test.setId(testNode.get("id").asText());
        } catch (NullPointerException e) {
            throw new MissingArgumentException("Argument id not found in test");
        }
    }

    private static List<Step> parseSteps(JsonNode testNode) throws MissingArgumentException {
        ArrayNode stepNodes = (ArrayNode) testNode.get("steps");
        try {
            List<Step> steps = new ArrayList<>();
            for (JsonNode stepNode : stepNodes) {
                String stepClass = stepNode.get("type").textValue();
                StepMarshaller marshaller = new StepMarshaller();
                Step step = marshaller.asObject(stepNode, stepClass);
                steps.add(step);
            }
            return steps;
        } catch (NullPointerException e) {
            throw new MissingArgumentException("Argument steps is missing in test");
        }
    }

    private static List<Property<?>> parseInputs(JsonNode testRunNode, ObjectMapper mapper) throws JsonProcessingException, MissingArgumentException {
        ArrayNode inputsNodes = (ArrayNode) testRunNode.get("inputs");
        List<Property<?>> inputs = new ArrayList<>();
        ReferenceParser referenceParser = new ReferenceParser();
        for (JsonNode inputNode : inputsNodes) {
            try {
                PropertyDTO propertyDTO = mapper.readValue(inputNode.toString(), PropertyDTO.class);
                if ((propertyDTO.getValue() instanceof String string) && referenceParser.isReference(string)) {
                    inputs.add(propertyDTO.createPropertyRef(referenceParser.getReference(string)));
                } else  {
                    inputs.add(propertyDTO.createProperty());
                }
            } catch (NullPointerException e) {
                throw new MissingArgumentException("Argument inputs is missing in testRun");
            }
        }
        return inputs;
    }

}