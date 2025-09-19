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

package net.ihe.gazelle.maestro.api.technical.dto.testrun;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import net.ihe.gazelle.maestro.api.business.testreport.SystemUnderTest;
import net.ihe.gazelle.security.business.acl.AccessControlList;

import java.util.List;

@JsonPropertyOrder({"testSuiteName", "testRuns", "accessControlList", "systemsUnderTest"})
public class TestSetRunDTO {

    private String testSuiteName;
    private List<TestRunDTO> testRuns;
    private AccessControlList accessControlList;
    private List<SystemUnderTest> systemsUnderTest;
    private String testSetRunId;


    public TestSetRunDTO() {
        accessControlList = new AccessControlList();
    }

    @JsonProperty("testRuns")
    public List<TestRunDTO> getTestRuns() {
        return testRuns;
    }

    public TestSetRunDTO setTestRuns(List<TestRunDTO> testRuns) {
        this.testRuns = testRuns;
        return this;
    }

    @JsonProperty("accessControlList")
    public AccessControlList getAccessControlList() {
        return accessControlList;
    }

    public TestSetRunDTO setAccessControlList(AccessControlList accessControlList) {
        this.accessControlList = accessControlList;
        return this;
    }

    @JsonProperty("testSuiteName")
    public String getTestSuiteName() {
        return testSuiteName;
    }
    public TestSetRunDTO setTestSuiteName(String testSuiteName) {
        this.testSuiteName = testSuiteName;
        return this;
    }

    @JsonProperty("systemsUnderTest")
    public List<SystemUnderTest> getSystemsUnderTest() {
        return systemsUnderTest;
    }

    public TestSetRunDTO setSystemsUnderTest(List<SystemUnderTest> systemsUnderTest) {
        this.systemsUnderTest = systemsUnderTest;
        return this;
    }

    @JsonProperty("testSetRunId")
    public String getTestSetRunId() {
        return testSetRunId;
    }

    public void setTestSetRunId(String testSetRunId) {
        this.testSetRunId = testSetRunId;
    }
}
