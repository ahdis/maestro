package net.ihe.gazelle.maestro.api.business.message;

import net.ihe.gazelle.lang.Event;

import java.io.Serial;

@Event
public class StepRunStarted implements Message {
    @Serial
    private static final long serialVersionUID = 1L;
    private String testId;
    private String stepId;

    public StepRunStarted() {}

    public StepRunStarted(String testId, String stepId) {
        this.testId = testId;
        this.stepId = stepId;
    }

    public String getTestId() {
        return testId;
    }

    public StepRunStarted setTestId(String testId) {
        this.testId = testId;
        return this;
    }

    public String getStepId() {
        return stepId;
    }

    public StepRunStarted setStepId(String stepId) {
        this.stepId = stepId;
        return this;
    }
}
