package net.ihe.gazelle.business.engine.runner;

import net.ihe.gazelle.step.Step;
import net.ihe.gazelle.runner.StepRunner;

public interface StepRunnerProvider {
    StepRunner create(Step step);
}
