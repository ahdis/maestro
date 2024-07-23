package net.ihe.gazelle.business.test;

import net.ihe.gazelle.dao.Input;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TestRun {

    private Test test = null;

    private final List<Input> inputs = new ArrayList<>();

    private final Map<String, Object> report = new ConcurrentHashMap<>();

    public Map<String, Object> getReport() {
        return report;
    }

    public void setReport(Map<String, Object> report) {
        this.report.putAll(report);
    }

    public Test getTest() {
        return test;
    }

    public List<Input> getInputs() {
        return inputs;
    }
    public void setTest(Test test) {
        this.test = test;
    }

    public TestRun addInput(Input input) {
        inputs.add(input);
        return this;
    }

    public void addAllInputs(List<Input> inputs) {
        this.inputs.addAll(inputs);
    }
}

