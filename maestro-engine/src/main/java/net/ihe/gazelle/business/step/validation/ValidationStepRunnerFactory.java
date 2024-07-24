package net.ihe.gazelle.business.step.validation;

import net.ihe.gazelle.step.Step;
import net.ihe.gazelle.step_runner.StepRunner;
import net.ihe.gazelle.step_runner.StepRunnerFactory;

public class ValidationStepRunnerFactory implements StepRunnerFactory {
    @Override
    public String getSupportedStep() {
        return ValidationStep.class.getCanonicalName();
    }

    @Override
    public StepRunner createStepRunner(Step step) {
        return new ValidationStepRunner();
    }
}
