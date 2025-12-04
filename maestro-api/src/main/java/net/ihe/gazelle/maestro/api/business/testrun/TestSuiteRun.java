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

import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.test.Test;
import net.ihe.gazelle.maestro.api.business.test.TestReference;
import net.ihe.gazelle.maestro.api.business.test.TestSuite;
import net.ihe.gazelle.maestro.api.business.testreport.SystemUnderTest;
import net.ihe.gazelle.security.business.acl.AccessControlList;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A TestSuiteRun is a structure containing several TestRun that will be executed iteratively. {@link TestRun} testSetId
 * an id that will be spread to each element of testRuns <br> testRuns the list of testRuns to execute
 * {@link AccessControlList} accessControlList an ACL that will be spread to each element of testRuns <br>
 * systemsUnderTest the list of systems under test that will be spread to each element of testRuns <br> testSetRunId
 * today maps the TestInstance ID in Gazelle Test Management
 */
public class TestSuiteRun implements Serializable {

   @Serial
   private static final long serialVersionUID = -4027777114820632058L;

    private static final int TIMEOUT_MARGIN_OFFSET = 3000;

   private TestSuite testSuite;
   private List<Test> tests;
   private List<Property> inputs;
   private List<SystemUnderTest> systemsUnderTest;
   private AccessControlList accessControlList;

   public TestSuiteRun() {
      this.tests = new ArrayList<>();
      this.inputs = new ArrayList<>();
      this.accessControlList = new AccessControlList();
      this.systemsUnderTest = new ArrayList<>();
   }

    public long computeTestSuiteTimeout() {
        return testSuite.getTestReferences().stream()
                .map(testReference -> getTestById(testReference.getTestId()))
                .mapToLong(Test::computeTestRunTimeout)
                .sum() + TIMEOUT_MARGIN_OFFSET;
    }

    private Test getTestById(String testId) {
        return tests.stream()
                .filter(test -> testId.equals(test.getId()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No test with id " + testId + " found."));
    }

    public TestSuite getTestSuite() {
      return testSuite;
   }

   public TestSuiteRun setTestSuite(TestSuite testSuite) {
      this.testSuite = testSuite;
      return this;
   }

   public List<Test> getTests() {
      return tests;
   }

   public TestSuiteRun setTests(List<Test> tests) {
      this.tests = tests != null ? new ArrayList<>(tests) : new ArrayList<>();
      return this;
   }

   public List<Property> getInputs() {
      return inputs;
   }

   public TestSuiteRun setInputs(List<Property> inputs) {
      this.inputs = inputs != null ? new ArrayList<>(inputs) : new ArrayList<>();
      return this;
   }

   public List<SystemUnderTest> getSystemsUnderTest() {
      return systemsUnderTest;
   }

   public TestSuiteRun setSystemsUnderTest(List<SystemUnderTest> systemsUnderTest) {
      this.systemsUnderTest = systemsUnderTest != null ? new ArrayList<>(systemsUnderTest) : new ArrayList<>();
      return this;
   }

   public AccessControlList getAccessControlList() {
      return accessControlList;
   }

   public TestSuiteRun setAccessControlList(AccessControlList accessControlList) {
      this.accessControlList = accessControlList != null ? accessControlList : new AccessControlList();
      return this;
   }

   public Test getTest(String id) {
      return tests.stream()
            .filter(test -> test.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("No test with id " + id + " found in this TestSuiteRun"));
   }

   public boolean isTestSuiteDefined() {
      return testSuite != null;
   }

   public boolean areAllReferencedTestsAttached() {
      if (testSuite == null || tests == null) {
         return false;
      }
      List<String> includedTestIds = tests.stream().map(Test::getId).toList();
      for (TestReference testRef : testSuite.getTestReferences()) {
         if (!includedTestIds.contains(testRef.getTestId())) {
            return false;
         }
      }
      return true;
   }

}
