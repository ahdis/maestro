/*
 * Copyright 2025 Kereval.
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

package net.ihe.gazelle.maestro.simulation.step.technical.factory;

import jakarta.enterprise.context.ApplicationScoped;
import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.simulation.step.business.SimulationHandler;
import net.ihe.gazelle.maestro.simulation.step.business.SimulationStep;
import net.ihe.gazelle.maestro.simulation.step.business.SimulationStepExecutor;
import net.ihe.gazelle.maestro.simulation.step.technical.dto.ReportSerializationImpl;
import net.ihe.gazelle.maestro.spi.business.runner.Handler;
import net.ihe.gazelle.maestro.spi.business.runner.StepExecutor;
import net.ihe.gazelle.maestro.spi.business.runner.StepRunnerFactory;
import net.ihe.gazelle.maestro.userinteraction.step.business.UserInteractionHandler;
import net.ihe.gazelle.maestro.userinteraction.step.business.UserInteractionHandlerImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static net.ihe.gazelle.maestro.userinteraction.step.business.UserInteractionHandler.NAME;

@ApplicationScoped
public class SimulationStepRunnerFactory implements StepRunnerFactory {

    @Override
    public String getSupportedStep() {
        return SimulationStep.TYPE;
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
    public StepExecutor createStepRunner(Step step, Set<Handler> handlers) {
        return handlers.stream().filter(SimulationHandler.class::isInstance).findFirst()
                .map(handler -> new SimulationStepExecutor(new ReportSerializationImpl(), (SimulationHandler) handler, new UserInteractionHandlerImpl()))
                .orElseThrow(() -> new IllegalArgumentException("No simulation handler provided"));
    }
}
