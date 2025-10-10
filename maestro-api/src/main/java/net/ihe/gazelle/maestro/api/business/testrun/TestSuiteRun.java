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
 * A TestSuiteRun is a structure containing several TestRun that will be executed iteratively. {@link TestRun} testSetId
 * an id that will be spread to each element of testRuns <br> testRuns the list of testRuns to execute
 * {@link AccessControlList} accessControlList an ACL that will be spread to each element of testRuns <br>
 * systemsUnderTest the list of systems under test that will be spread to each element of testRuns <br> testSetRunId
 * today maps the TestInstance ID in Gazelle Test Management
 */
public class TestSuiteRun {

   private String internalId; // internal use only
   private String testSuiteName;
   private String testSuiteClientId;
   private List<TestRun> testRuns;
   private AccessControlList accessControlList;
   private List<SystemUnderTest> systemsUnderTest;

   public TestSuiteRun() {
      this.internalId = UUID.randomUUID().toString();
      this.accessControlList = new AccessControlList();
      this.systemsUnderTest = new ArrayList<>();
   }

   public TestSuiteRun(List<TestRun> testRuns) {
      this();
      this.testRuns = testRuns;
   }

   public String getInternalId() {
      return internalId;
   }

   public void setInternalId(String internalId) {
      this.internalId = internalId;
   }

   public String getTestSuiteName() {
      return testSuiteName;
   }

   public TestSuiteRun setTestSuiteName(String testSuiteName) {
      this.testSuiteName = testSuiteName;
      return this;
   }

   public List<TestRun> getTestRuns() {
      return testRuns;
   }

   public TestSuiteRun setTestRuns(List<TestRun> testRuns) {
      this.testRuns = testRuns;
      return this;
   }

   public AccessControlList getAccessControlList() {
      return accessControlList;
   }

   public TestSuiteRun setAccessControlList(AccessControlList accessControlList) {
      this.accessControlList = accessControlList;
      return this;
   }

   public List<SystemUnderTest> getSystemsUnderTest() {
      return systemsUnderTest;
   }

   public TestSuiteRun setSystemsUnderTest(List<SystemUnderTest> systemsUnderTest) {
      this.systemsUnderTest = systemsUnderTest;
      return this;
   }

   public String getTestSuiteClientId() {
      return testSuiteClientId;
   }

   public TestSuiteRun setTestSuiteClientId(String testSuiteClientId) {
      this.testSuiteClientId = testSuiteClientId;
      return this;
   }

   public boolean isTestSuiteNameValid() {
      return testSuiteName != null && !testSuiteName.isBlank();
   }

   public boolean isTestSetRunIdValid() {
      return testSuiteClientId != null && !testSuiteClientId.isBlank();
   }
}
