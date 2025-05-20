package net.ihe.gazelle.maestro.business.step.assertion;

import net.ihe.gazelle.maestro.api.business.testrun.Step;
import net.ihe.gazelle.maestro.api.business.testrun.runner.Handler;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRunner;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRunnerFactory;

import java.util.Map;
import java.util.Set;

/**
 * Assertion Step is not implemented. It exists just for test purpose
 */
public class AssertionStepRunnerFactory implements StepRunnerFactory {


    @Override
    public String getSupportedStep() {
        return AssertionStep.class.getCanonicalName();
    }

    @Override
    public Map<String, Class<? extends Handler>> getRequiredServices(Step step) {
        return Map.of();
    }

    @Override
    public StepRunner createStepRunner(Step step, Set<Handler> handlers) {
        return new AssertionStepRunner(new AssertionHandler());
    }

}
