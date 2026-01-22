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
