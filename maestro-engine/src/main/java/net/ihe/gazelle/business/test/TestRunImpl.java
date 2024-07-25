package net.ihe.gazelle.business.test;

import net.ihe.gazelle.test.Input;
import net.ihe.gazelle.test.Test;
import net.ihe.gazelle.test.TestRun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TestRunImpl implements TestRun {

    private Test test = null;

    private final List<Input> inputs = new ArrayList<>();

    private final Map<String, Object> report = new ConcurrentHashMap<>();

    public Map<String, Object> getReport() {
        return new HashMap<>(report);
    }

    public void setReport(Map<String, Object> report) {
        this.report.putAll(report);
    }

    @Override
    public Test getTest() {
        Test res = new TestImpl();
        res.addAllSteps(test.getSteps());
        return res;
    }

    @Override
    public List<Input> getInputs() {
        return new ArrayList<>(inputs);
    }

    @Override
    public void setTest(Test test) {
        this.test = test;
    }

    @Override
    public TestRun addInput(Input input) {
        inputs.add(input);
        return this;
    }

    @Override
    public void addAllInputs(List<Input> inputs) {
        this.inputs.addAll(inputs);
    }
}

