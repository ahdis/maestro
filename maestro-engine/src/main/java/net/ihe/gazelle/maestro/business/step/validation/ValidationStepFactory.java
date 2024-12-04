package net.ihe.gazelle.maestro.business.step.validation;

import net.ihe.gazelle.maestro.Property;
import net.ihe.gazelle.maestro.Step;
import net.ihe.gazelle.maestro.StepFactory;

import java.util.Map;

public class ValidationStepFactory implements StepFactory {

    @Override
    @SuppressWarnings("unchecked")
    public Step create(String name, Map<String, Property<?>> properties) {
        return new ValidationStep(
                name,
                (Property<String>) properties.get(ValidationStep.VALIDATION_SERVICE),
                (Property<String>) properties.get(ValidationStep.VALIDATION_PROFILE),
                (Property<byte[]>) properties.get(ValidationStep.CONTENT_TO_VALIDATE)
        );
    }

    @Override
    public String getSupportedStep() {
        return ValidationStep.TYPE;
    }
}
