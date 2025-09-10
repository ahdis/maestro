/*
 * Copyright 2025 Kereval.
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

package net.ihe.gazelle.maestro.itb.step.business.request;

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
