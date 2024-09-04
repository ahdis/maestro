package net.ihe.gazelle.business.step.assertion;

import net.ihe.gazelle.runner.Handler;
import net.ihe.gazelle.step.Step;
import net.ihe.gazelle.runner.StepRunner;
import net.ihe.gazelle.runner.StepRunnerFactory;

import java.util.Map;
import java.util.Set;

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
        return null;
    }

}
