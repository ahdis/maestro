package net.ihe.gazelle.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import net.ihe.gazelle.business.engine.marshaller.StepMarshaller;
import net.ihe.gazelle.business.engine.marshaller.StepMarshallerProvider;
import net.ihe.gazelle.business.exception.MissingArgumentException;
import net.ihe.gazelle.business.exception.ParsingErrorException;
import net.ihe.gazelle.business.step.Step;
import net.ihe.gazelle.business.test.Test;
import net.ihe.gazelle.business.test.TestRun;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TestRunParser{

    public List<TestRun> convertInputStreamToTestRuns(InputStream inputStream) throws IOException, MissingArgumentException {
        try{
            ObjectMapper mapper = new ObjectMapper();
            return parseTestRuns(inputStream, mapper);
        } catch (Exception e) {
            throw new ParsingErrorException(e);
        }
    }

    private static List<TestRun> parseTestRuns(InputStream inputStream, ObjectMapper mapper) throws IOException, MissingArgumentException {
        try {
            ArrayNode testRunNodes = (ArrayNode) mapper.readTree(inputStream);
            List<TestRun> testRuns = new ArrayList<>();
            for(JsonNode testRunNode : testRunNodes) {
                TestRun testRun = new TestRun();
                testRun.setTest(parseTest(testRunNode));
                testRun.addAllInputs(parseInputs(testRunNode, mapper));
                testRuns.add(testRun);
            }
            return testRuns;
        } catch (NullPointerException | MissingArgumentException e){
            throw new MissingArgumentException(e);
        }
    }

    private static Test parseTest(JsonNode testRunNode) throws MissingArgumentException {
        JsonNode testNode = testRunNode.get("test");
        try {
            Test test = new Test();
            test.addAllSteps(parseSteps(testNode));
            return test;
        } catch (NullPointerException e){
            throw new MissingArgumentException("Argument test is missing in testRun");
        }
    }

    private static List<Step> parseSteps(JsonNode testNode) throws MissingArgumentException {
        ArrayNode stepNodes = (ArrayNode) testNode.get("steps");
        try {
            List<Step> steps = new ArrayList<>();
            for (JsonNode stepNode : stepNodes) {
                String stepClass = stepNode.get("@class").textValue();
                StepMarshaller marshaller = new StepMarshallerProvider().getMarshaller(stepClass);
                Step step = marshaller.asObject(stepNode);
                steps.add(step);
            }
            return steps;
        }catch (NullPointerException e){
            throw new MissingArgumentException("Argument steps is missing in test");
        }
    }

    private static List<Input> parseInputs(JsonNode testRunNode, ObjectMapper mapper) throws JsonProcessingException, MissingArgumentException {
        ArrayNode inputsNodes = (ArrayNode) testRunNode.get("inputs");
        List<Input> inputs = new ArrayList<>();
        for(JsonNode inputNode : inputsNodes) {
            try{
                inputs.add(mapper.readValue(inputNode.toString(), Input.class));
            } catch (NullPointerException e){
                throw new MissingArgumentException("Argument inputs is missing in testRun");
            }
        }
        return inputs;
    }

}