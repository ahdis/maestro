package net.ihe.gazelle.business.step.validation;

import net.ihe.gazelle.business.step.StepFactory;
import net.ihe.gazelle.step.Step;

import java.util.Map;

public class ValidationStepFactory implements StepFactory {
    @Override
    public Step create(Map<String, String> properties, String name) {
        return new ValidationStep(name, properties);
    }

    @Override
    public String getSupportedStep() {
        return ValidationStep.class.getCanonicalName();
    }
}
