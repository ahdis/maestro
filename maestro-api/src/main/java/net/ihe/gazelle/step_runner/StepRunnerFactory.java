package net.ihe.gazelle.step_runner;

import net.ihe.gazelle.step.Step;

public interface StepRunnerFactory {

    /**
     * @return canonical name of the supported Step
     */
    String getSupportedStep();

    /**
     * @param step step to run
     * @return an instance of corresponding testRunner
     */
    StepRunner createStepRunner(Step step);
}
