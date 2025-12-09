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
import net.ihe.gazelle.maestro.api.business.testreport.SystemUnderTest;
import net.ihe.gazelle.security.business.acl.AccessControlList;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * TestRun is the execution of a Test with specific inputs.
 */
public class TestRun implements Serializable {

   @Serial
   private static final long serialVersionUID = -6069446503327802519L;

   private Test test = null;
   private List<Property> inputs;
   private List<SystemUnderTest> systemsUnderTest;
   private AccessControlList accessControlList;

   public TestRun() {
      this.inputs = new ArrayList<>();
      this.systemsUnderTest = new ArrayList<>();
   }

   public TestRun(TestRun other) {
      this.test = other.test;
      this.inputs = new ArrayList<>(other.inputs);
      this.systemsUnderTest = new ArrayList<>(other.systemsUnderTest);
      this.accessControlList = other.accessControlList;
   }

   public Test getTest() {
      return test;
   }

   public TestRun setTest(Test test) {
      this.test = test;
      return this;
   }

   public List<Property> getInputs() {
      return new ArrayList<>(inputs);
   }

   public TestRun setInputs(List<Property> inputs) {
      this.inputs = new ArrayList<>(inputs);
      return this;
   }

   public TestRun addInput(Property property) {
      inputs.add(property);
      return this;
   }

   public List<SystemUnderTest> getSystemsUnderTest() {
      return new ArrayList<>(systemsUnderTest);
   }

   public TestRun setSystemsUnderTest(List<SystemUnderTest> systemsUnderTest) {
      this.systemsUnderTest = systemsUnderTest != null
              ? new ArrayList<>(systemsUnderTest)
              : new ArrayList<>();
      return this;
   }

   public AccessControlList getAccessControlList() {
      return accessControlList;
   }

   public TestRun setAccessControlList(AccessControlList accessControlList) {
      this.accessControlList = accessControlList;
      return this;
   }

   public boolean isTestDefined() {
      return test != null;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof TestRun testRun)) {
         return false;
      }
      return Objects.equals(test, testRun.test) && Objects.equals(inputs,
            testRun.inputs) && Objects.equals(systemsUnderTest,
            testRun.systemsUnderTest) && Objects.equals(accessControlList, testRun.accessControlList);
   }

   @Override
   public int hashCode() {
      return Objects.hash(test, inputs, systemsUnderTest, accessControlList);
   }
}

