package net.ihe.gazelle.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonGetter;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidationStepDTO extends StepDTO {
    private List<PropertyDTO> properties = new ArrayList<>();

    // Getters et Setters
    @JsonGetter("properties")
    public List<PropertyDTO> getProperties() { return properties; }
    public void addProperty(PropertyDTO property) { this.properties.add(property); }
}