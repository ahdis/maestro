/*
 * Copyright 2025 IHE International.
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
