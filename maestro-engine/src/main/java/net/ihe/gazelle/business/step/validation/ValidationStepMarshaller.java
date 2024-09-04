package net.ihe.gazelle.business.step.validation;

import com.fasterxml.jackson.databind.JsonNode;
import net.ihe.gazelle.marshaller.StepMarshaller;
import net.ihe.gazelle.step.Step;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidationStepMarshaller implements StepMarshaller {
    @Override
    public Step asObject(JsonNode node) {
        ValidationStep validationStep = new ValidationStep();
        List<String> keys = validationStep.getMandatoryKeys();
        Map<String, String> properties = new HashMap<>();
        for(String key : keys){
            properties.put(key, getNodeValueOrNull(node, key));
        }
        validationStep.setProperties(properties);
        validationStep.setName(getNodeValueOrNull(node, "name"));
        return validationStep;
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
