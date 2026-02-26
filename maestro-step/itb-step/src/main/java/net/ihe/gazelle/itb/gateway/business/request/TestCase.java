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
 * Model for a single test case of a test suite
 */
public class TestCase {

    private String caseID;
    private List<Parameter> parameters;

    /**
     * Creates an empty test case.
     */
    public TestCase() {
    }

    /**
     * Creates a test case with identifier.
     *
     * @param testCase test case identifier
     */
    public TestCase(String testCase) {
        this.caseID = testCase;
    }

    /**
     * Creates a test case with identifier and parameters.
     *
     * @param caseID test case identifier
     * @param inputs test case parameters
     */
    public TestCase(String caseID, List<Parameter> inputs) {
        this.caseID = caseID;
        this.parameters = inputs;
    }

    /**
     * Returns test case identifier.
     *
     * @return test case identifier
     */
    public String getCaseID() {
        return caseID;
    }

    /**
     * Sets test case identifier.
     *
     * @param caseID test case identifier
     */
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

    /**
     * Returns test case parameters.
     *
     * @return test case parameters
     */
    public List<Parameter> getParameters() {
        return parameters;
    }

    /**
     * Sets test case parameters.
     *
     * @param inputs test case parameters
     */
    public void setParameters(List<Parameter> inputs) {
        this.parameters = inputs;
    }
}
