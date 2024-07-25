package net.ihe.gazelle.business.test;

import net.ihe.gazelle.step.Step;
import net.ihe.gazelle.test.Test;

import java.util.ArrayList;
import java.util.List;

public class TestImpl implements Test {
    private final List<Step> steps = new ArrayList<>();

    @Override
    public List<Step> getSteps() {
        return new ArrayList<>(steps);
    }

    @Override
    public Test addStep(Step step) {
        steps.add(step);
        return this;
    }

    @Override
    public void addAllSteps(List<Step> steps) {
        this.steps.addAll(steps);
    }
}

