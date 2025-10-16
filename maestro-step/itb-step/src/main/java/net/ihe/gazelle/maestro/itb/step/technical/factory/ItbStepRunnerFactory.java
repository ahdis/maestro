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

package net.ihe.gazelle.maestro.itb.step.technical.factory;

import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.business.testrun.runner.Handler;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRunner;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRunnerFactory;
import net.ihe.gazelle.maestro.itb.step.business.ItbStep;
import net.ihe.gazelle.maestro.itb.step.business.ItbStepRunner;
import net.ihe.gazelle.maestro.itb.step.technical.handler.ItbHandlerImpl;

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
