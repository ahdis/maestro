/*
 * Copyright 2025-2026 IHE International.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ihe.gazelle.itb.gateway.business.request;

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

    /**
     * Creates an empty request.
     */
    public TestRequest() {}

    /**
     * Creates a request with test identifier.
     *
     * @param testID test identifier
     */
    public TestRequest(String testID) {
        this.testID = testID;
    }

    /**
     * Creates a request with test identifier and parameters.
     *
     * @param testID test identifier
     * @param parameters request parameters
     */
    public TestRequest(String testID, List<Parameter> parameters) {
        this.testID = testID;
        this.parameters = parameters;
    }

    /**
     * Returns test identifier.
     *
     * @return test identifier
     */
    public String getTestID() {
        return testID;
    }

    /**
     * Sets test identifier.
     *
     * @param testID test identifier
     */
    public void setTestID(String testID) {
        this.testID = testID;
    }

    /**
     * Returns request-level parameters.
     *
     * @return request parameters
     */
    public List<Parameter> getParameters() {
        return parameters;
    }

    /**
     * Sets request-level parameters.
     *
     * @param parameters request parameters
     */
    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    /**
     * Returns optional test suite selection.
     *
     * @return test suite
     */
    public TestSuite getTestSuite() {
        return testSuite;
    }

    /**
     * Sets optional test suite selection.
     *
     * @param testSuite test suite
     */
    public void setTestSuite(TestSuite testSuite) {
        this.testSuite = testSuite;
    }

    /**
     * Returns optional test case selections.
     *
     * @return test case list
     */
    public List<TestCase> getTestCases() {
        return testCases;
    }

    /**
     * Sets optional test case selections.
     *
     * @param testCases test case list
     */
    public void setTestCases(List<TestCase> testCases) {
        this.testCases = testCases;
    }

    /**
     * Returns callback URL used by ITB.
     *
     * @return callback URL
     */
    public String getCallbackURL() { return callbackURL; }

    /**
     * Sets callback URL used by ITB.
     *
     * @param callbackURL callback URL
     */
    public void setCallbackURL(String callbackURL) { this.callbackURL = callbackURL; }

    /**
     * Returns callback authorization header value.
     *
     * @return callback authorization
     */
    public String getCallbackAuthorization() { return callbackAuthorization; }

    /**
     * Sets callback authorization header value.
     *
     * @param callbackAuthorization callback authorization
     */
    public void setCallbackAuthorization(String callbackAuthorization) { this.callbackAuthorization = callbackAuthorization; }
}
