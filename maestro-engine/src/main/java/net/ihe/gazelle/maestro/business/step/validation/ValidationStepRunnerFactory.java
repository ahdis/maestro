package net.ihe.gazelle.maestro.business.step.validation;

import net.ihe.gazelle.maestro.api.business.testrun.Step;
import net.ihe.gazelle.maestro.api.business.testrun.runner.Handler;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRunner;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRunnerFactory;
import net.ihe.gazelle.maestro.technical.serialization.ValidationReportSerializerImpl;

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
            requiredHandlers.put(validationStep.getValidationService().getValue(), ValidationHandler.class);
        return requiredHandlers;
    }

    @Override
    public StepRunner createStepRunner(Step step, Set<Handler> handlers) {
        return handlers.stream().filter(ValidationHandler.class::isInstance).findFirst()
                .map(handler -> new ValidationStepRunner(new ValidationReportSerializerImpl(), (ValidationHandler) handler))
                .orElseThrow(() -> new IllegalArgumentException("No validation handler provided"));
    }
}
