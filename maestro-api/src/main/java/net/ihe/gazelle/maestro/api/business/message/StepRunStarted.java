package net.ihe.gazelle.maestro.api.business.message;

import net.ihe.gazelle.lang.Event;

import java.io.Serial;

@Event
public class StepRunStarted implements Message {

    @Serial
    private static final long serialVersionUID = 1L;
    private String testId;
    private int stepIndex;

    public StepRunStarted() {}

    public StepRunStarted(String testId, int stepIndex) {
        this.testId = testId;
        this.stepIndex = stepIndex;
    }

    public String getTestId() {
        return testId;
    }

    public StepRunStarted setTestId(String testId) {
        this.testId = testId;
        return this;
    }

    public int getStepIndex() {
        return stepIndex;
    }

    public StepRunStarted setStepIndex(int stepIndex) {
        this.stepIndex = stepIndex;
        return this;
    }
}
