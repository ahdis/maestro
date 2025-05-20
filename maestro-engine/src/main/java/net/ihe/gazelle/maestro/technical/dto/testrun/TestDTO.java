package net.ihe.gazelle.maestro.technical.dto.testrun;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import net.ihe.gazelle.maestro.api.business.testrun.Step;
import net.ihe.gazelle.maestro.api.business.testrun.Test;
import net.ihe.gazelle.modelmarshaller.technical.dto.DTO;
import net.ihe.gazelle.modelmarshaller.technical.dto.SubTypingDTO;

import java.util.List;

@JsonPropertyOrder({"id", "name", "steps"})
public class TestDTO {

    @JsonIgnore
    private final Test domain;

    public TestDTO() {
        domain = new Test();
    }

    public TestDTO(Test domain) {
        this.domain = domain;
    }

    @JsonGetter("id")
    public String getId() {
        return domain.getId();
    }

    @JsonSetter("id")
    public void setId(String id) {
        domain.setId(id);
    }

    @JsonGetter("name")
    public String getName() {
        return domain.getName();
    }

    @JsonSetter("name")
    public void setName(String name) {
        domain.setName(name);
    }

    @SuppressWarnings("rawtypes")
    @JsonGetter("steps")
    public List<StepDTO> getSteps() {
        return domain.getSteps().stream()
                .map(SubTypingDTO::fromBusinessObject)
                .map(StepDTO.class::cast)
                .toList();
    }

    @SuppressWarnings("rawtypes")
    @JsonSetter("steps")
    public void setSteps(List<StepDTO> steps) {
        domain.setSteps(steps.stream()
                .map(DTO::getBusinessObject)
                .map(Step.class::cast)
                .toList());
    }

    @JsonIgnore
    public Test getDomain() {
        return domain;
    }
}
