package net.ihe.gazelle.maestro.technical.step.simulation;

import net.ihe.gazelle.maestro.api.business.testrun.Step;
import net.ihe.gazelle.maestro.api.business.testrun.runner.Handler;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRunner;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRunnerFactory;
import net.ihe.gazelle.maestro.business.step.simulation.SimulationHandler;
import net.ihe.gazelle.maestro.business.step.simulation.SimulationStep;
import net.ihe.gazelle.maestro.business.step.simulation.SimulationStepRunner;
import net.ihe.gazelle.maestro.business.step.userinteract.UserInteractionHandler;
import net.ihe.gazelle.maestro.business.step.userinteract.UserInteractionHandlerImpl;
import net.ihe.gazelle.maestro.technical.serialization.ReportSerializationImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static net.ihe.gazelle.maestro.business.step.userinteract.UserInteractionHandler.NAME;

public class SimulationStepRunnerFactory  implements StepRunnerFactory {

    @Override
    public String getSupportedStep() {
        return SimulationStep.class.getCanonicalName();
    }

    @Override
    public Map<String, Class<? extends Handler>> getRequiredServices(Step step) {
        Map<String, Class<? extends Handler>> requiredHandlers = new HashMap<>();
        if (step instanceof SimulationStep simulationStep) {
            requiredHandlers.put(simulationStep.getSimulationService().getValue(), SimulationHandler.class);
        }
        requiredHandlers.put(NAME, UserInteractionHandler.class);
        return requiredHandlers;
    }

    @Override
    public StepRunner createStepRunner(Step step, Set<Handler> handlers) {
        return handlers.stream().filter(SimulationHandler.class::isInstance).findFirst()
                .map(handler -> new SimulationStepRunner(new ReportSerializationImpl(), (SimulationHandler) handler, new UserInteractionHandlerImpl()))
                .orElseThrow(() -> new IllegalArgumentException("No simulation handler provided"));
    }
}
