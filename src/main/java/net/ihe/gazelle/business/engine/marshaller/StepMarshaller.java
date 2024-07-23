package net.ihe.gazelle.business.engine.marshaller;

import com.fasterxml.jackson.databind.JsonNode;
import net.ihe.gazelle.business.step.Step;

public interface StepMarshaller {

    Step asObject(JsonNode node);
    String getSupportedStep();

}
