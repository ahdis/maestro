package net.ihe.gazelle.maestro.api.business.message;

import net.ihe.gazelle.lang.Event;

import java.io.Serial;

@Event
public class TestSuiteRunStarted implements Message {
    @Serial
    private static final long serialVersionUID = 1L;
    private String testSuiteRunId;

    public TestSuiteRunStarted() {}

    public TestSuiteRunStarted(String testSuiteRunId) {
        this.testSuiteRunId = testSuiteRunId;
    }

    public String getTestSuiteRunId() {
        return testSuiteRunId;
    }

    public TestSuiteRunStarted setTestSuiteRunId(String testSuiteRunId) {
        this.testSuiteRunId = testSuiteRunId;
        return this;
    }
}
