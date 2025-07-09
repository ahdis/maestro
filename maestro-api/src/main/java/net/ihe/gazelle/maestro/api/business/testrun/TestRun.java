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

package net.ihe.gazelle.maestro.api.business.testrun;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A structure contains in a TestSet, defining all the data to execute an automated step suit
 * <p>
 *     - <b>testSetId</b> the testSet's id <br>
 *     - <b>test</b> structure containing step suit {@link Test} <br>
 *     - <b>inputs</b> a list of properties that can be referenced in each step {@link Property}
 * </p>
 */
public class TestRun {

    private String testSetId;
    private Test test = null;
    private List<Property<?>> inputs = new ArrayList<>();

    public String getTestSetId() {
        return testSetId;
    }

    public TestRun setTestSetId(String testSetId) {
        this.testSetId = testSetId;
        return this;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TestRun testRun)) return false;
        return Objects.equals(testSetId, testRun.testSetId)
                && Objects.equals(test, testRun.test)
                && Objects.equals(inputs, testRun.inputs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(testSetId, test, inputs);
    }
}

