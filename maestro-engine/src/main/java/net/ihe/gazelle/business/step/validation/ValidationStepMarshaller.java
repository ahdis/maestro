package net.ihe.gazelle.business.step.validation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ihe.gazelle.marshaller.StepMarshaller;
import net.ihe.gazelle.step.Step;
import net.ihe.gazelle.technical.ws.MissingArgumentException;

import java.util.Map;

public class ValidationStepMarshaller implements StepMarshaller {
    @Override
    public Step asObject(JsonNode node) {
        try {
            ValidationStep validationStep = new ValidationStep();
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> properties = mapper.convertValue(node.get("properties"), new TypeReference<>(){});
            validationStep.setProperties(properties);
            validationStep.setName(getNodeValueOrNull(node, "name"));
            return validationStep;
        } catch (Exception e){
            throw new MissingArgumentException("A properties field is required and should be a <key, value> Map", e);
        }
    }

    @Override
    public String getSupportedStep() {
        return "net.ihe.gazelle.business.step.validation.ValidationStep";
    }
    

    private String getNodeValueOrNull(JsonNode parent, String attributeName) {
        JsonNode node = parent.get(attributeName);
        return node != null ? node.asText() : null;
    }
}
