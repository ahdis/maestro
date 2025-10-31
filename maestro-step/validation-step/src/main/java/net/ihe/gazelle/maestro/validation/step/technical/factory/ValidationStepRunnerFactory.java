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

package net.ihe.gazelle.maestro.validation.step.technical.factory;

import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.spi.business.runner.Handler;
import net.ihe.gazelle.maestro.spi.business.runner.StepExecutor;
import net.ihe.gazelle.maestro.spi.business.runner.StepRunnerFactory;
import net.ihe.gazelle.maestro.validation.step.business.ValidationHandler;
import net.ihe.gazelle.maestro.validation.step.business.ValidationStep;
import net.ihe.gazelle.maestro.validation.step.business.ValidationStepExecutor;
import net.ihe.gazelle.maestro.validation.step.technical.dto.ReportSerializerImpl;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ValidationStepRunnerFactory implements StepRunnerFactory {

   @Override
   public String getSupportedStep() {
      return ValidationStep.TYPE;
   }

   @Override
   public Map<String, Class<? extends Handler>> getRequiredServices(Step step) {
      return Map.of(
            step.getPropertyValue(ValidationStep.VALIDATION_SERVICE), ValidationHandler.class
      );
   }

   @Override
   public StepExecutor createStepExecutor(Step step, Map<String, Handler> handlers) {
      String serviceName = step.getPropertyValue(ValidationStep.VALIDATION_SERVICE);
      return new ValidationStepExecutor(
            Optional.ofNullable((ValidationHandler) handlers.get(serviceName))
                  .orElseThrow(() -> new IllegalArgumentException("No validation handler provided for " + serviceName)),
            new ReportSerializerImpl()
      );
   }

}
