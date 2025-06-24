package net.ihe.gazelle.maestro.business.step.validation;

import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.Step;
import net.ihe.gazelle.maestro.api.business.testrun.StepFactory;

import java.util.Map;

// TODO : to delete when StepFactoryProvider will be deleted
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
