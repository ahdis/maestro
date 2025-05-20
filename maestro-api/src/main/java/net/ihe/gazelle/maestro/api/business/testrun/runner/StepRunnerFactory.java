package net.ihe.gazelle.maestro.api.business.testrun.runner;

import net.ihe.gazelle.maestro.api.business.testrun.Step;

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
     * @param handlers a set of handlers
     * @return a StepRunner instance that can run step using a Handler from handlers
     * @throws IllegalArgumentException if no handler from handlers can execute step
     */
    StepRunner createStepRunner(Step step, Set<Handler> handlers);
}
