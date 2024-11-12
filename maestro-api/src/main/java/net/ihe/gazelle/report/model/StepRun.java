package net.ihe.gazelle.report.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * mandatory fields : stepId, type, result
 *
 */
public class StepRun {

    private String stepId;
    private String type;
    private StepResult result;
    private List<Item> outputs;

    public StepRun() {
        result = StepResult.UNDEFINED;
        outputs = new ArrayList<>();
    }

    public StepRun(String stepId, String type, StepResult result) {
        this();
        this.stepId = stepId;
        this.type = type;
        this.result = result;
    }

    public String getStepId() {
        return stepId;
    }

    public StepRun setStepId(String stepId) {
        this.stepId = stepId;
        return this;
    }

    public String getType() {
        return type;
    }

    public StepRun setType(String type) {
        this.type = type;
        return this;
    }

    public StepResult getResult() {
        return result;
    }

    public StepRun setResult(StepResult result) {
        this.result = result;
        return this;
    }

    public List<Item> getOutputs() {
        return new ArrayList<>(outputs);
    }

    public StepRun setOutputs(List<Item> outputs) {
        this.outputs = new ArrayList<>(outputs);
        return this;
    }

    public StepRun addOutput(Item item) {
        outputs.add(item);
        return this;
    }

    public StepRun addOutputs(List<Item> outputs) {
        this.outputs.addAll(outputs);
        return this;
    }

    public boolean isStepIdValid() {
        return stepId != null && !stepId.isBlank();
    }

    public boolean isTypeValid() {
        return type != null && !type.isBlank();
    }

    public boolean isResultValid() {
        return result != null;
    }

    public boolean isValid() {
        return isStepIdValid() && isTypeValid() && isResultValid();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StepRun stepRun)) return false;
        return Objects.equals(stepId, stepRun.stepId)
                && Objects.equals(type, stepRun.type)
                && result == stepRun.result
                && Objects.equals(outputs, stepRun.outputs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stepId, type, result, outputs);
    }
}
