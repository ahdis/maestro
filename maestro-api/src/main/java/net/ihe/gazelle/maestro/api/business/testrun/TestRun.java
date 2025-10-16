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

import net.ihe.gazelle.maestro.api.business.test.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * A structure contains in a TestSet, defining all the data to execute an automated step suit
 * <p>
 * - <b>testSetId</b> the testSet's id <br> - <b>test</b> structure containing step suit {@link Test} <br> -
 * <b>inputs</b> a list of properties that can be referenced in each step {@link Property}
 * </p>
 */
public class TestRun {

   private final String internalId; // internal use only
   private Test test = null;
   private List<Property<?>> inputs;

   public TestRun() {
      this.internalId = UUID.randomUUID().toString();
      this.inputs = new ArrayList<>();
   }

   public TestRun(TestRun other) {
      this.internalId = other.internalId;
      this.test = other.test;
      this.inputs = new ArrayList<>(other.inputs);
   }

   public String getInternalId() {
      return internalId;
   }

   public Test getTest() {
      return test;
   }

   public TestRun setTest(Test test) {
      this.test = test;
      return this;
   }

   // Suppress generic wildcard warning
   @SuppressWarnings("java:S1452")
   public List<Property<?>> getInputs() {
      return new ArrayList<>(inputs);
   }

   public TestRun setInputs(List<Property<?>> inputs) {
      this.inputs = new ArrayList<>(inputs);
      return this;
   }

   public TestRun addInputs(Property<?> property) {
      inputs.add(property);
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
      return Objects.equals(internalId, testRun.internalId) && Objects.equals(test,
            testRun.test) && Objects.equals(inputs, testRun.inputs);
   }

   @Override
   public int hashCode() {
      return Objects.hash(internalId, test, inputs);
   }
}

