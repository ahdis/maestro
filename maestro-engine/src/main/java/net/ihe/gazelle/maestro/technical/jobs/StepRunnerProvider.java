package net.ihe.gazelle.maestro.technical.jobs;

import net.ihe.gazelle.maestro.Step;
import net.ihe.gazelle.maestro.runner.StepRunner;

public interface StepRunnerProvider {
    /**
     * @param step The step we want StepRunner to execute
     * @return A StepRunner able to run step
     */
    StepRunner create(Step step);
}
