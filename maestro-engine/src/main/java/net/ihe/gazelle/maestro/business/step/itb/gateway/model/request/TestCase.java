package net.ihe.gazelle.maestro.business.step.itb.gateway.model.request;

import java.util.List;

/**
 * Model for a single test case of a test suite
 */
public class TestCase {
    private String caseID;

    private List<Parameter> parameters;

    public TestCase() {
    }

    public TestCase(String testCase) {
        this.caseID = testCase;
    }

    public TestCase(String caseID, List<Parameter> inputs) {
        this.caseID = caseID;
        this.parameters = inputs;
    }

    public String getCaseID() {
        return caseID;
    }

    public void setTestCase(String caseID) {
        this.caseID = caseID;
    }

    @Override
    public String toString() {
        return "TestCase{" +
                "caseID='" + caseID + '\'' +
                ", parameters=" + parameters +
                '}';
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> inputs) {
        this.parameters = inputs;
    }
}
