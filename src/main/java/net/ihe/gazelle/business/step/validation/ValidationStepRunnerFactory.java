package net.ihe.gazelle.business.step.validation;

import net.ihe.gazelle.business.engine.runner.StepRunner;
import net.ihe.gazelle.business.engine.runner.StepRunnerFactory;
import net.ihe.gazelle.business.step.Step;

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
