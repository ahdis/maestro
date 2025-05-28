package net.ihe.gazelle.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonGetter;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestCaseDTO {
    private String name;
    private String description;
    private List<InputDTO> inputs = new ArrayList<>();
    private List<StepDTO> steps = new ArrayList<>();

    // Getters et Setters
    @JsonGetter("name")
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @JsonGetter("description")
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @JsonGetter("inputs")
    public List<InputDTO> getInputs() { return inputs; }
    public void addInputs(InputDTO input) { this.inputs.add(input); }

    @JsonGetter("steps")
    public List<StepDTO> getSteps() { return steps; }
    public void addStep(StepDTO step) { this.steps.add(step); }
}