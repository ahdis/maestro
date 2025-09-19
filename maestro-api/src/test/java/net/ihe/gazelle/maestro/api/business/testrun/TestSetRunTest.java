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
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestSetRunTest {

    @Test
    void defaultConstructorInitializesFields() {
        TestSetRun tsr = new TestSetRun();
        assertNotNull(tsr.getTestSetId(), "testSetId should be generated");
        assertNull(tsr.getTestRuns(), "testRuns should be null until explicitly set");
        assertNotNull(tsr.getAccessControlList(), "ACL should be initialized");
        assertNotNull(tsr.getSystemsUnderTest(), "systemsUnderTest should be initialized");
        assertTrue(tsr.getSystemsUnderTest().isEmpty(), "systemsUnderTest should start empty");
        assertNull(tsr.getTestSetRunId(), "testSetRunId should be null by default");
        assertNull(tsr.getTestSuiteName(), "testSuiteName should be null by default");
    }

    @Test
    void parameterizedConstructorSetsFields() {
        String id = "custom-id";
        List<TestRun> runs = new ArrayList<>();
        runs.add(new TestRun());
        TestSetRun tsr = new TestSetRun(id, runs);
        assertEquals(id, tsr.getTestSetId());
        assertEquals(runs, tsr.getTestRuns());
    }

    @Test
    void fluentSettersReturnSameInstance() {
        TestSetRun tsr = new TestSetRun();
        AccessControlList acl = new AccessControlList();
        List<SystemUnderTest> systems = new ArrayList<>();
        systems.add(new SystemUnderTest());
        List<TestRun> runs = new ArrayList<>();
        runs.add(new TestRun());

        TestSetRun returned = tsr
                .setTestSuiteName("suite")
                .setAccessControlList(acl)
                .setSystemsUnderTest(systems)
                .setTestRuns(runs)
                .setTestSetRunId("tsr-id");

        assertSame(tsr, returned, "Fluent setters should return same instance");
        assertEquals("suite", tsr.getTestSuiteName());
        assertSame(acl, tsr.getAccessControlList());
        assertSame(systems, tsr.getSystemsUnderTest());
        assertSame(runs, tsr.getTestRuns());
        assertEquals("tsr-id", tsr.getTestSetRunId());
    }

    @Test
    void settersStoreProvidedInstances() {
        TestSetRun tsr = new TestSetRun();
        AccessControlList acl = new AccessControlList();
        List<SystemUnderTest> systems = new ArrayList<>();
        List<TestRun> runs = new ArrayList<>();

        tsr.setAccessControlList(acl);
        tsr.setSystemsUnderTest(systems);
        tsr.setTestRuns(runs);
        tsr.setTestSuiteName("suiteName");
        tsr.setTestSetRunId("runId");

        assertSame(acl, tsr.getAccessControlList());
        assertSame(systems, tsr.getSystemsUnderTest());
        assertSame(runs, tsr.getTestRuns());
        assertEquals("suiteName", tsr.getTestSuiteName());
        assertEquals("runId", tsr.getTestSetRunId());
    }
}

