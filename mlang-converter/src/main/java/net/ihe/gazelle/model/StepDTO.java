package net.ihe.gazelle.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonGetter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = InstructionStepDTO.class, name = "INSTRUCTION"),
        @JsonSubTypes.Type(value = ValidationStepDTO.class, name = "VALIDATION")
})
public abstract class StepDTO {
    private String id;
    private String description;

    // Getters et Setters
    @JsonGetter("id")
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @JsonGetter("description")
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}