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
import net.ihe.gazelle.maestro.simulation.step.business.SimulationStepDefinition;
import net.ihe.gazelle.maestro.simulation.step.business.SimulationStepExecutor;
import net.ihe.gazelle.maestro.simulation.step.technical.dto.ReportSerializerImpl;
import net.ihe.gazelle.maestro.spi.business.runner.Handler;
import net.ihe.gazelle.maestro.spi.business.runner.StepExecutor;
import net.ihe.gazelle.maestro.spi.business.runner.StepRunnerFactory;
import net.ihe.gazelle.maestro.userinteraction.step.business.UserInteractionHandler;

import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class SimulationStepRunnerFactory implements StepRunnerFactory {

   private static final String USER_INTERACTION_SERVICE = "User Interaction Service";

   @Override
   public String getSupportedStep() {
      return SimulationStepDefinition.TYPE;
   }

   @Override
   public Map<String, Class<? extends Handler>> getRequiredServices(Step step) {
      return Map.of(
            step.getPropertyValue(SimulationStepDefinition.SIMULATION_SERVICE), SimulationHandler.class,
            USER_INTERACTION_SERVICE, UserInteractionHandler.class
      );
   }


   @Override
   public StepExecutor createStepExecutor(Step step, Map<String, Handler> handlers) {
      String simulatorName = step.getPropertyValue(SimulationStepDefinition.SIMULATION_SERVICE);
      return new SimulationStepExecutor(
            Optional.ofNullable((SimulationHandler) handlers.get(simulatorName))
                  .orElseThrow(() -> new IllegalArgumentException(
                        "No simulation handler provided for service: " + simulatorName)),
            Optional.ofNullable((UserInteractionHandler) handlers.get(USER_INTERACTION_SERVICE))
                  .orElseThrow(() -> new IllegalArgumentException("No user interaction handler provided")),
            new ReportSerializerImpl()
      );
   }
}
