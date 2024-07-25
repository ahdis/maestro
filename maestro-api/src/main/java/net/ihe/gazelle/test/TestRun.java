package net.ihe.gazelle.test;

import java.util.List;

public interface TestRun {
    Test getTest();
    List<Input> getInputs();
    void setTest(Test test);
    TestRun addInput(Input input);
    void addAllInputs(List<Input> input);
}
