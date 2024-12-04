package net.ihe.gazelle.testreport;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * mandatory fields : stepId, type, result
 *
 */
public class StepRunReport {

    private String stepId;
    private String type;
    private StepResult result;
    private List<Item> outputs;
    private List<UnexpectedError> unexpectedErrors;

    public StepRunReport() {
        result = StepResult.UNDEFINED;
        outputs = new ArrayList<>();
        unexpectedErrors = new ArrayList<>();
    }

    public StepRunReport(String stepId, String type, StepResult result) {
        this();
        this.stepId = stepId;
        this.type = type;
        this.result = result;
    }

    public String getStepId() {
        return stepId;
    }

    public StepRunReport setStepId(String stepId) {
        this.stepId = stepId;
        return this;
    }

    public String getType() {
        return type;
    }

    public StepRunReport setType(String type) {
        this.type = type;
        return this;
    }

    public StepResult getResult() {
        return result;
    }

    public StepRunReport setResult(StepResult result) {
        this.result = result;
        return this;
    }

    public List<Item> getOutputs() {
        return new ArrayList<>(outputs);
    }

    public StepRunReport setOutputs(List<Item> outputs) {
        this.outputs = new ArrayList<>(outputs);
        return this;
    }

    public StepRunReport addOutput(Item item) {
        outputs.add(item);
        return this;
    }

    public StepRunReport addOutputs(List<Item> outputs) {
        this.outputs.addAll(outputs);
        return this;
    }

    public List<UnexpectedError> getUnexpectedErrors() {
        return unexpectedErrors;
    }

    public StepRunReport setUnexpectedErrors(List<UnexpectedError> unexpectedErrors) {
        this.unexpectedErrors = unexpectedErrors;
        return this;
    }

    public StepRunReport addUnexpectedError(UnexpectedError unexpectedError) {
        unexpectedErrors.add(unexpectedError);
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

    public StepResult computeResult() {
        if (!unexpectedErrors.isEmpty()) {
            result = StepResult.UNDEFINED;
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StepRunReport stepRunReport)) return false;
        return Objects.equals(stepId, stepRunReport.stepId)
                && Objects.equals(type, stepRunReport.type)
                && result == stepRunReport.result
                && Objects.equals(outputs, stepRunReport.outputs)
                && Objects.equals(unexpectedErrors, stepRunReport.unexpectedErrors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stepId, type, result, outputs, unexpectedErrors);
    }
}
