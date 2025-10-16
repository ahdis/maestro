package net.ihe.gazelle.maestro.api.business.message;

import java.io.Serial;
import net.ihe.gazelle.lang.Event;

@Event
public class TestRunStarted implements Message {
    @Serial
    private static final long serialVersionUID = 1L;
    private String testId;

    public TestRunStarted() {}

    public TestRunStarted(String testId) {
        this.testId = testId;
    }

    public String getTestId() {
        return testId;
    }

    public TestRunStarted setTestId(String testId) {
        this.testId = testId;
        return this;
    }
}
