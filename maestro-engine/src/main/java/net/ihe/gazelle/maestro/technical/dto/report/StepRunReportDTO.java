package net.ihe.gazelle.maestro.technical.dto.report;

import com.fasterxml.jackson.annotation.*;
import net.ihe.gazelle.testreport.StepResult;
import net.ihe.gazelle.testreport.StepRunReport;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"stepId", "type", "result", "outputs"})
public class StepRunReportDTO {

    @JsonIgnore
    private final StepRunReport domain;

    public StepRunReportDTO() {
        this.domain = new StepRunReport();
    }

    public StepRunReportDTO(StepRunReport domain) {
        this.domain = domain;
    }

    @JsonGetter(value = "stepId")
    public String getStepId() {
        return domain.getStepId();
    }

    @JsonSetter(value = "stepId")
    public void setStepId(String stepId) {
        domain.setStepId(stepId);
    }

    @JsonGetter(value = "type")
    public String getType() {
        return domain.getType();
    }

    @JsonSetter(value = "type")
    public void setType(String type) {
        domain.setType(type);
    }

    @JsonGetter(value = "result")
    public String getResult() {
        return domain.getResult().name();
    }

    @JsonSetter(value = "result")
    public void setResult(String result) {
        domain.setResult(StepResult.valueOf(result));
    }

    @JsonGetter(value = "outputs")
    public List<ItemDTO> outputs() {
        return domain.getOutputs().stream()
                .map(ItemDTO::new)
                .toList();
    }

    @JsonSetter(value = "outputs")
    public void setOutputs(List<ItemDTO> outputs) {
        domain.setOutputs(outputs.stream().map(
                ItemDTO::getDomain
        ).toList());
    }

    @JsonGetter(value = "unexpectedErrors")
    public List<UnexpectedErrorDTO> getUnexpectedErrors() {
        return domain.getUnexpectedErrors().stream()
                .map(UnexpectedErrorDTO::new)
                .toList();
    }

    @JsonSetter(value = "unexpectedErrors")
    public void setUnexpectedErrors(List<UnexpectedErrorDTO> unexpectedErrors) {
        domain.setUnexpectedErrors(unexpectedErrors.stream().map(
                UnexpectedErrorDTO::getDomain
        ).toList());
    }

    @JsonIgnore
    public StepRunReport getDomain() {
        return domain;
    }
}
