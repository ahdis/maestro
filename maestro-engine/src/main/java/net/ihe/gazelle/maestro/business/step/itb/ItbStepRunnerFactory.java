package net.ihe.gazelle.maestro.business.step.itb;

import net.ihe.gazelle.maestro.api.business.testrun.Step;
import net.ihe.gazelle.maestro.api.business.testrun.runner.Handler;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRunner;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRunnerFactory;
import net.ihe.gazelle.maestro.technical.step.itb.ItbHandlerImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ItbStepRunnerFactory implements StepRunnerFactory {
    @Override
    public String getSupportedStep() {
        return ItbStep.class.getCanonicalName();
    }

    @Override
    public Map<String, Class<? extends Handler>> getRequiredServices(Step step) {
        Map<String, Class<? extends Handler>> requiredHandlers = new HashMap<>();
        if (step instanceof ItbStep)
            requiredHandlers.put("itb", ItbHandlerImpl.class);
        return requiredHandlers;
    }

    @Override
    public StepRunner createStepRunner(Step step, Set<Handler> handlers) {
        return handlers.stream().filter(ItbHandlerImpl.class::isInstance).findFirst()
                .map(handler -> new ItbStepRunner((ItbHandlerImpl) handler))
                .orElseThrow(() -> new IllegalArgumentException("No itb handler provided"));
    }

}
