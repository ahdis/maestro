package net.ihe.gazelle.business.step.assertion;

import com.fasterxml.jackson.databind.JsonNode;
import net.ihe.gazelle.marshaller.StepMarshaller;
import net.ihe.gazelle.step.Step;

public class AssertionStepMarshaller implements StepMarshaller {
    @Override
    public Step asObject(JsonNode node) {
        return new AssertionStep(
                getNodeValueOrNull(node, "name"),
                getNodeValueOrNull(node, "assertion"),
                getNodeValueOrNull(node, "assertionService")
        );
    }

    @Override
    public String getSupportedStep() {
        return "net.ihe.gazelle.step.assertion.AssertionStep";
    }

    private String getNodeValueOrNull(JsonNode parent, String attributeName) {
        JsonNode node = parent.get(attributeName);
        return node != null ? node.asText() : null;
    }
}
