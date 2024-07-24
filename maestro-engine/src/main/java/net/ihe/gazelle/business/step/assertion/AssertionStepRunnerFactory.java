package net.ihe.gazelle.business.step.assertion;

import net.ihe.gazelle.step.Step;
import net.ihe.gazelle.step_runner.StepRunner;
import net.ihe.gazelle.step_runner.StepRunnerFactory;

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
