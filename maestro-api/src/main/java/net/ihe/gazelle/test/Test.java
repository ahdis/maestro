package net.ihe.gazelle.test;

import net.ihe.gazelle.step.Step;

import java.util.List;

public interface Test {
    List<Step> getSteps();
    Test addStep(Step step);
    void addAllSteps(List<Step> steps);
}
