package net.ihe.gazelle.runner;

import net.ihe.gazelle.step.Step;

import java.util.Map;
import java.util.Set;

public interface StepRunnerFactory {

    /**
     * @return canonical name of the supported Step
     */
    String getSupportedStep();

    Map<String, Class<? extends Handler>> getRequiredServices(Step step);

    /**
     * @param step step to run
     * @return an instance of corresponding testRunner
     */
    StepRunner createStepRunner(Step step, Set<Handler> handlers);
}
