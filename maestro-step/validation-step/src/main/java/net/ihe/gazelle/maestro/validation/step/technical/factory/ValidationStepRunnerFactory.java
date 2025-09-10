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

import net.ihe.gazelle.maestro.api.business.testrun.Step;
import net.ihe.gazelle.maestro.api.business.testrun.runner.Handler;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRunner;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRunnerFactory;
import net.ihe.gazelle.maestro.validation.step.business.ValidationHandler;
import net.ihe.gazelle.maestro.validation.step.business.ValidationStep;
import net.ihe.gazelle.maestro.validation.step.business.ValidationStepRunner;
import net.ihe.gazelle.maestro.validation.step.technical.dto.ReportSerializationImpl;

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
        if (step instanceof ValidationStep validationStep) {
            requiredHandlers.put(validationStep.getValidationService().getValue(), ValidationHandler.class);
        }
        return requiredHandlers;
    }

    @Override
    public StepRunner createStepRunner(Step step, Set<Handler> handlers) {
        return handlers.stream().filter(ValidationHandler.class::isInstance).findFirst()
                .map(handler -> new ValidationStepRunner(new ReportSerializationImpl(), (ValidationHandler) handler))
                .orElseThrow(() -> new IllegalArgumentException("No validation handler provided"));
    }
}
