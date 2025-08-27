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

package net.ihe.gazelle.maestro.api.business.testrun;

import net.ihe.gazelle.maestro.api.business.testreport.SystemUnderTest;
import net.ihe.gazelle.security.business.acl.AccessControlList;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A TestSetRun is a structure containing several TestRun that will be executed iteratively.
 * {@link TestRun}
 * testSetId an id that will be spread to each element of testRuns <br>
 * testRuns the list of testRuns to execute
 * {@link AccessControlList} accessControlList an ACL that will be spread to each element of testRuns <br>
 * systemsUnderTest the list of systems under test that will be spread to each element of testRuns <br>
 * testSetRunId today maps the TestInstance ID in Gazelle Test Management
 */
public class TestSetRun {

    private final String testSetId;
    private String testSuiteName;
    private List<TestRun> testRuns;
    private AccessControlList accessControlList;
    private List<SystemUnderTest> systemsUnderTest;
    private String testSetRunId;


    public TestSetRun() {
        this.testSetId = UUID.randomUUID().toString();
        this.accessControlList = new AccessControlList();
        this.systemsUnderTest = new ArrayList<>();
    }

    public TestSetRun(String testSetId, List<TestRun> testRuns) {
        this.testSetId = testSetId;
        this.testRuns = testRuns;
    }

    public String getTestSetId() {
        return testSetId;
    }

    public String getTestSuiteName() {
        return testSuiteName;
    }

    public TestSetRun setTestSuiteName(String testSuiteName) {
        this.testSuiteName = testSuiteName;
        return this;
    }

    public List<TestRun> getTestRuns() {
        return testRuns;
    }

    public TestSetRun setTestRuns(List<TestRun> testRuns) {
        this.testRuns = testRuns;
        return this;
    }

    public AccessControlList getAccessControlList() {
        return accessControlList;
    }

    public TestSetRun setAccessControlList(AccessControlList accessControlList) {
        this.accessControlList = accessControlList;
        return this;
    }

    public List<SystemUnderTest> getSystemsUnderTest() {
        return systemsUnderTest;
    }

    public void setSystemsUnderTest(List<SystemUnderTest> systemsUnderTest) {
        this.systemsUnderTest = systemsUnderTest;
    }

    public String getTestSetRunId() {
        return testSetRunId;
    }

    public void setTestSetRunId(String testSetRunId) {
        this.testSetRunId = testSetRunId;
    }
}
