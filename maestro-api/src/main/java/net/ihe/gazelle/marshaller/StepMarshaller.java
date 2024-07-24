package net.ihe.gazelle.marshaller;

import com.fasterxml.jackson.databind.JsonNode;
import net.ihe.gazelle.step.Step;

public interface StepMarshaller {

    Step asObject(JsonNode node);
    String getSupportedStep();

}
