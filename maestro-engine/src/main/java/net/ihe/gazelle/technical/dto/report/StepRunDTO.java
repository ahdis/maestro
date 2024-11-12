package net.ihe.gazelle.technical.dto.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import net.ihe.gazelle.report.model.StepRun;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"stepId", "type", "result", "outputs"})
public record StepRunDTO(StepRun domain) {

    @JsonProperty(value = "stepId")
    public String getStepId() {
        return domain.getStepId();
    }

    @JsonProperty(value = "type")
    public String getType() {
        return domain.getType();
    }

    @JsonProperty(value = "result")
    public String getResult() {
        return domain.getResult().name();
    }

    @JsonProperty(value = "outputs")
    public List<ItemDTO> outputs() {
        return domain.getOutputs().stream()
                .map(ItemDTO::new)
                .toList();
    }

    @JsonIgnore
    public boolean isStepIdValid() {
        return domain.isStepIdValid();
    }

    @JsonIgnore
    public boolean isTypeValid() {
        return domain.isTypeValid();
    }

    @JsonIgnore
    public boolean isResultValid() {
        return domain.isResultValid();
    }

    @JsonIgnore
    public boolean isValid() {
        return domain.isValid();
    }
}
