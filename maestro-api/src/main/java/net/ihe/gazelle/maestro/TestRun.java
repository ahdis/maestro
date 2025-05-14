package net.ihe.gazelle.maestro;

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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestRun testRun = (TestRun) o;
        return Objects.equals(test, testRun.test) && Objects.equals(inputs, testRun.inputs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(test, inputs);
    }
}

