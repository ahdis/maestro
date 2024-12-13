package net.ihe.gazelle.maestro.technical.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import net.ihe.gazelle.maestro.technical.StepFactoryProvider;
import net.ihe.gazelle.maestro.Property;
import net.ihe.gazelle.maestro.Step;

import java.util.*;

public class StepMarshaller {

    public Step asObject(JsonNode stepNode, String stepType) {
        StepFactoryProvider provider = new StepFactoryProvider();
        ArrayNode propertyNodes = (ArrayNode) stepNode.get("properties");
        return provider.create(stepType, getNodeValueOrNull(stepNode), getPropertyMap(propertyNodes));
    }

    @SuppressWarnings("java:S1452")
    private Map<String, Property<?>> getPropertyMap(ArrayNode propertyNodes) {
        ReferenceParser referenceParser = new ReferenceParser();
        Map<String, Property<?>> map = new HashMap<>();
        for (JsonNode propertyNode : propertyNodes) {
            PropertyDTO propertyDTO = new ObjectMapper().convertValue(propertyNode, PropertyDTO.class);
            if ((propertyDTO.getValue() instanceof String string) && referenceParser.isReference(string)) {
                map.put(propertyDTO.getName(), propertyDTO.createPropertyRef(referenceParser.getReference(string)));
            } else  {
                map.put(propertyDTO.getName(), propertyDTO.createProperty());
            }
        }
        return map;
    }

    private String getNodeValueOrNull(JsonNode parent) {
        JsonNode node = parent.get("name");
        return node != null ? node.asText() : null;
    }

}
