package net.ihe.gazelle.mlang.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kereval.mlang.api.model.*;
import com.kereval.mlang.api.visitor.MLangGeneratorVisitor;
import net.ihe.gazelle.model.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Optional;

public class JSONMLangConverter implements Converter {
    @Override
    public void convert(InputStreamReader is, OutputStreamWriter os) throws ConverterException, ParserException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            TestCaseDTO testCase = mapper.readValue(is, TestCaseDTO.class);
            MTest test = buildTest(testCase);

            MLangGeneratorVisitor visitor = new MLangGeneratorVisitor(os);
            test.accept(visitor);
        } catch (IOException e) {
            throw new ConverterException(e.getMessage());
        }
    }

    private MTest buildTest(TestCaseDTO testDTO) throws ConverterException {
        MTest test = new MTest(testDTO.getName());
        test.setDescription(testDTO.getDescription());
        for (var inputDTO : testDTO.getInputs()) {
            convertInputDTO(test, inputDTO);
        }

        for (var stepDTO : testDTO.getSteps()) {
            convertStepDTO(test, stepDTO);
        }

        return test;
    }

    private void convertInputDTO(MTest test, InputDTO inputDTO) throws ConverterException {
        switch (inputDTO.getType()) {
            case "file":
                test.addRequire(buildRequireFile(inputDTO));
                break;
            default:
                throw new ConverterException("Input type " + inputDTO.getType() + " not handle by the converter");
        }
    }

    private void convertStepDTO(MTest test, StepDTO stepDTO) throws ConverterException {
        if (stepDTO instanceof ValidationStepDTO) {
            test.addStep(buildAssertValid((ValidationStepDTO) stepDTO));
        }
        else {
            throw new ConverterException("Kind of Step DTO not handle by the converter detected");
        }
    }


    private RequireFile buildRequireFile(InputDTO input) {
        return new RequireFile(input.getKey());
    }

    private AssertMStep buildAssertValid(ValidationStepDTO validationStepDTO) throws ConverterException {
        Optional<PropertyDTO> validationServiceProp = validationStepDTO.getProperties().stream().
                filter(p -> p.getKey().equals("validationService")).findFirst();
        if (validationServiceProp.isEmpty())
            throw new ConverterException("No validation service property detected into validation step");
        Optional<PropertyDTO> profileNameProp = validationStepDTO.getProperties().stream().
                filter(p -> p.getKey().equals("validationProfile")).findFirst();
        if (profileNameProp.isEmpty())
            throw new ConverterException("No profile name property detected into validation step");
        Optional<PropertyDTO> contentToValidateProp = validationStepDTO.getProperties().stream().
                filter(p -> p.getKey().equals("contentToValidate")).findFirst();
        if (contentToValidateProp.isEmpty())
            throw new ConverterException("No content to validate property detected into validation step");

        String contentToValidate = contentToValidateProp.get().getValue();
        if (!contentToValidate.startsWith("${") && !contentToValidate.endsWith("}"))
            throw new ConverterException("Content to validate property of validation step malformed");
        String value = contentToValidate.substring(2, contentToValidate.length() - 1);
        List<Param> params = List.of(new VariableParam(value));

        return new AssertMStep(AssertMStep.Type.VALID,
                params,
                validationServiceProp.get().getValue(),
                profileNameProp.get().getValue()
        );
    }
 }
