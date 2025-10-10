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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;
import net.ihe.gazelle.maestro.api.technical.dto.report.SystemUnderTestDTO;
import net.ihe.gazelle.modelmarshaller.technical.dto.DTO;
import net.ihe.gazelle.security.technical.acl.AccessControlListDTO;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "testSuiteName",
        "testRuns",
        "accessControlList",
        "systemsUnderTest",
        "testSetRunId"
})
public class TestSuiteRunDTO implements DTO<TestSuiteRun> {

    @JsonIgnore
    private final TestSuiteRun testSuiteRun;

    public TestSuiteRunDTO() {
        this(new TestSuiteRun());
    }

    public TestSuiteRunDTO(TestSuiteRun testSuiteRun) {
        this.testSuiteRun = testSuiteRun;
    }

    @Override
    public TestSuiteRun getBusinessObject() {
        return testSuiteRun;
    }

    @JsonProperty("testSuiteName")
    public String getTestSuiteName() {
        return getBusinessObject().getTestSuiteName();
    }

    public void setTestSuiteName(String testSuiteName) {
        getBusinessObject().setTestSuiteName(testSuiteName);
    }

    @JsonProperty("testRuns")
    public List<TestRunDTO> getTestRuns() {
        return getBusinessObject().getTestRuns().stream().map(TestRunDTO::new).toList();
    }

    public void setTestRuns(List<TestRunDTO> testRuns) {
        getBusinessObject().setTestRuns(
                testRuns.stream().map(TestRunDTO::getBusinessObject).toList()
        );
    }

    @JsonProperty("accessControlList")
    public AccessControlListDTO getAccessControlList() {
        return new AccessControlListDTO(getBusinessObject().getAccessControlList());
    }

    public void setAccessControlList(AccessControlListDTO accessControlList) {
        getBusinessObject().setAccessControlList(accessControlList.getBusinessObject());
    }

    @JsonProperty("systemsUnderTest")
    public List<SystemUnderTestDTO> getSystemsUnderTest() {
        return getBusinessObject().getSystemsUnderTest().stream().map(SystemUnderTestDTO::new).toList();
    }

    public void setSystemsUnderTest(List<SystemUnderTestDTO> systemsUnderTest) {
        getBusinessObject().setSystemsUnderTest(
                systemsUnderTest.stream().map(SystemUnderTestDTO::getBusinessObject).toList()
        );
    }

    @JsonProperty("testSetRunId")
    public String getTestSetRunId() {
        return getBusinessObject().getTestSuiteClientId();
    }

    public void setTestSetRunId(String testSetRunId) {
        getBusinessObject().setTestSuiteClientId(testSetRunId);
    }
}
