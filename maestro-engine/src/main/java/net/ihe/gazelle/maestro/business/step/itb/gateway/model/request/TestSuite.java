package net.ihe.gazelle.maestro.business.step.itb.gateway.model.request;

import java.util.List;

/**
 * Model for the test suite of a test request
 */
public class TestSuite {
    private String suiteID;

    private List<Parameter> parameters;

    public TestSuite(String suiteID) {
        this.suiteID = suiteID;
    }

    public TestSuite(String suiteID, List<Parameter> parameters) {
        this.suiteID = suiteID;
        this.parameters = parameters;
    }

    public String getSuiteID() {
        return suiteID;
    }

    public void setSuiteID(String suiteID) {
        this.suiteID = suiteID;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }
}
