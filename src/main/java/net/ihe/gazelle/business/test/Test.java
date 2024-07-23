package net.ihe.gazelle.business.test;

import net.ihe.gazelle.business.step.Step;

import java.util.ArrayList;
import java.util.List;

public class Test {
    private final List<Step> steps = new ArrayList<>();

    public List<Step> getSteps() {
        return steps;
    }

    public Test addStep(Step step) {
        steps.add(step);
        return this;
    }

    public void addAllSteps(List<Step> steps) {
        this.steps.addAll(steps);
    }
}

