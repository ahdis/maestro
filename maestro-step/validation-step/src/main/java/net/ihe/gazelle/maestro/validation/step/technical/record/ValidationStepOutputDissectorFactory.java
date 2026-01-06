package net.ihe.gazelle.maestro.validation.step.technical.record;

import net.ihe.gazelle.maestro.spi.business.recording.StepOutputDissector;
import net.ihe.gazelle.maestro.spi.business.recording.StepOutputDissectorFactory;
import net.ihe.gazelle.maestro.validation.step.business.ValidationStepDefinition;

/**
 * Factory for creating ValidationStepOutputDissector instances.
 * This factory creates dissectors for validation step outputs.
 */
public class ValidationStepOutputDissectorFactory implements StepOutputDissectorFactory {

    /**
     * Default constructor.
     */
    public ValidationStepOutputDissectorFactory() { /* Default constructor */ }

    @Override
    public boolean supports(final String stepType) {
        return ValidationStepDefinition.TYPE.equals(stepType);
    }

    @Override
    public StepOutputDissector create() {
        return new ValidationStepOutputDissector();
    }

}
