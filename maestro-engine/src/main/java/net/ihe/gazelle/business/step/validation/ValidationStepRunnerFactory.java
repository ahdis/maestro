package net.ihe.gazelle.business.step.validation;

import net.ihe.gazelle.runner.Handler;
import net.ihe.gazelle.step.Step;
import net.ihe.gazelle.runner.StepRunner;
import net.ihe.gazelle.runner.StepRunnerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ValidationStepRunnerFactory implements StepRunnerFactory {
    @Override
    public String getSupportedStep() {
        return ValidationStep.class.getCanonicalName();
    }

    @Override
    public Map<String, Class<? extends Handler>> getRequiredServices(Step step) {
        Map<String, Class<? extends Handler>> requiredHandlers = new HashMap<>();
        if(step instanceof ValidationStep validationStep)
            requiredHandlers.put(validationStep.getProperties().get(ValidationStep.VALIDATION_SERVICE), ValidationHandler.class);
        return requiredHandlers;
    }

    @Override
    public StepRunner createStepRunner(Step step, Set<Handler> handlers) {
        return handlers.stream().filter(handler -> handler instanceof ValidationHandler).findFirst()
                .map(handler -> new ValidationStepRunner((ValidationHandler) handler))
                .orElseThrow(() -> new IllegalArgumentException("No validation handler provided"));
    }
}
