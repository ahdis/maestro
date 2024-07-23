package net.ihe.gazelle.business.step.assertion;

import net.ihe.gazelle.business.engine.runner.StepRunner;
import net.ihe.gazelle.business.engine.runner.StepRunnerFactory;
import net.ihe.gazelle.business.step.Step;

public class AssertionStepRunnerFactory implements StepRunnerFactory {


    @Override
    public String getSupportedStep() {
        return AssertionStep.class.getCanonicalName();
    }

    @Override
    public StepRunner createStepRunner(Step step) {
        return new AssertionStepRunner(new AssertionHandler());
    }
}
