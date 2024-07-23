package net.ihe.gazelle.business.step.validation;

import com.fasterxml.jackson.databind.JsonNode;
import net.ihe.gazelle.business.engine.marshaller.StepMarshaller;
import net.ihe.gazelle.business.step.Step;

public class ValidationStepMarshaller implements StepMarshaller {
    @Override
    public Step asObject(JsonNode node) {
        return new ValidationStep(
                getNodeValueOrNull(node, "name"),
                getNodeValueOrNull(node, "validationService"),
                getNodeValueOrNull(node, "validationProfile"),
                getNodeValueOrNull(node, "contentToValidate")
        );
    }

    @Override
    public String getSupportedStep() {
        return "net.ihe.gazelle.step.validation.ValidationStep";
    }

    private String getNodeValueOrNull(JsonNode parent, String attributeName) {
        JsonNode node = parent.get(attributeName);
        return node != null ? node.asText() : null;
    }
}
