/*
 * Copyright 2025 IHE International.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ihe.gazelle.maestro.technical.step.simulation;

import jakarta.enterprise.context.ApplicationScoped;
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

@ApplicationScoped
public class SimulationStepRunnerFactory implements StepRunnerFactory {

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
