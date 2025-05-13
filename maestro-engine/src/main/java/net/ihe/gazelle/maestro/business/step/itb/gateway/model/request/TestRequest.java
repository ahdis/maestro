package net.ihe.gazelle.maestro.business.step.itb.gateway.model.request;

import java.util.List;

/**
 * Model for an incoming request
 */
public class TestRequest {
    private String testID;

    private List<Parameter> parameters;

    private TestSuite testSuite;

    private List<TestCase> testCases;

    private String callbackURL;

    private String callbackAuthorization;

    public TestRequest() {}

    public TestRequest(String testID) {
        this.testID = testID;
    }

    public TestRequest(String testID, List<Parameter> parameters) {
        this.testID = testID;
        this.parameters = parameters;
    }

    public String getTestID() {
        return testID;
    }

    public void setTestID(String testID) {
        this.testID = testID;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public TestSuite getTestSuite() {
        return testSuite;
    }

    public void setTestSuite(TestSuite testSuite) {
        this.testSuite = testSuite;
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<TestCase> testCases) {
        this.testCases = testCases;
    }

    public String getCallbackURL() { return callbackURL; }

    public void setCallbackURL(String callbackURL) { this.callbackURL = callbackURL; }

    public String getCallbackAuthorization() { return callbackAuthorization; }

    public void setCallbackAuthorization(String callbackAuthorization) { this.callbackAuthorization = callbackAuthorization; }
}
