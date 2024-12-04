package net.ihe.gazelle.maestro.business.step.assertion;

import net.ihe.gazelle.framework.modelvalidator.interlay.adapter.BValidatorBuilderFactory;
import net.ihe.gazelle.maestro.Step;
import net.ihe.gazelle.maestro.runner.Handler;
import net.ihe.gazelle.maestro.runner.StepRunner;
import net.ihe.gazelle.maestro.runner.StepRunnerFactory;

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
        return new AssertionStepRunner(new BValidatorBuilderFactory(), new AssertionHandler());
    }

}
