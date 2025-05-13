package net.ihe.gazelle.maestro.business.step.itb;

import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.interlay.adapter.BValidatorBuilderFactory;
import net.ihe.gazelle.maestro.Step;
import net.ihe.gazelle.maestro.business.step.validation.ValidationStep;
import net.ihe.gazelle.maestro.runner.Handler;
import net.ihe.gazelle.maestro.runner.StepRunner;
import net.ihe.gazelle.maestro.runner.StepRunnerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ItbStepRunnerFactory implements StepRunnerFactory {
    @Override
    public String getSupportedStep() {
        return ValidationStep.class.getCanonicalName();
    }

    @Override
    public Map<String, Class<? extends Handler>> getRequiredServices(Step step) {
        Map<String, Class<? extends Handler>> requiredHandlers = new HashMap<>();
        if (step instanceof ItbStep)
            requiredHandlers.put("itb", ItbHandler.class);
        return requiredHandlers;
    }

    @Override
    public StepRunner createStepRunner(Step step, Set<Handler> handlers) {
        return handlers.stream().filter(ItbHandler.class::isInstance).findFirst()
                .map(handler -> new ItbStepRunner(getValidatorBuilderFactory(), (ItbHandler) handler))
                .orElseThrow(() -> new IllegalArgumentException("No validation handler provided"));
    }

    public ValidatorBuilderFactory getValidatorBuilderFactory() {
        return new BValidatorBuilderFactory();
    }

}
