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

package technical.provider;

import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.engine.business.StepExecutorProvider;
import net.ihe.gazelle.maestro.engine.business.UnknownStepException;
import net.ihe.gazelle.maestro.spi.business.Handler;
import net.ihe.gazelle.maestro.spi.technical.HandlerProvider;
import net.ihe.gazelle.maestro.spi.business.StepExecutor;
import net.ihe.gazelle.maestro.spi.technical.StepExecutorFactory;

import java.util.AbstractMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class StepExecutorSPIProvider implements StepExecutorProvider {

   private final HandlerProvider handlerProvider;

   public StepExecutorSPIProvider(HandlerProvider handlerProvider) {
      this.handlerProvider = handlerProvider;
   }

   @Override
   public StepExecutor getExecutor(Step step) {
      ServiceLoader<StepExecutorFactory> serviceLoader = ServiceLoader.load(StepExecutorFactory.class);
      for (StepExecutorFactory runnerFactory : serviceLoader) {
         if (runnerFactory.getSupportedStep().equals(step.getType())) {
            return runnerFactory.createStepExecutor(
                  step,
                  getHandlers(runnerFactory.getRequiredServices(step))
            );
         }
      }
      throw new UnknownStepException("Step " + step.getType() + " is not supported");
   }

   private Map<String, Handler> getHandlers(final Map<String, Class<? extends Handler>> requiredServices) {
      return requiredServices.entrySet().stream()
            .map(this::injectHandler)
            .filter(this::isHandlerAvailable)
            .collect(Collectors.toMap(
                  Map.Entry::getKey,
                  Map.Entry::getValue
            ));
   }

   private Map.Entry<String, Handler> injectHandler(Map.Entry<String, Class<? extends Handler>> requiredHandler) {
      return new AbstractMap.SimpleEntry<>(
            requiredHandler.getKey(),
            handlerProvider.getHandler(requiredHandler.getKey(), requiredHandler.getValue())
      );
   }

   private boolean isHandlerAvailable(Map.Entry<String, Handler> serviceHandler) {
      return serviceHandler.getValue().isAvailable();
   }

}
