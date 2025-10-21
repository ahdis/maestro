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

package net.ihe.gazelle.maestro.assertion.step.technical.factory;

import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.spi.business.runner.Handler;
import net.ihe.gazelle.maestro.spi.business.runner.StepRunner;
import net.ihe.gazelle.maestro.spi.business.runner.StepRunnerFactory;
import net.ihe.gazelle.maestro.assertion.step.business.AssertionStep;
import net.ihe.gazelle.maestro.assertion.step.business.AssertionStepRunner;
import net.ihe.gazelle.maestro.assertion.step.technical.handler.AssertionHandlerImpl;

import java.util.Map;
import java.util.Set;

/**
 * Assertion Step is not implemented. It exists just for test purpose
 */
public class AssertionStepRunnerFactory implements StepRunnerFactory {


    @Override
    public String getSupportedStep() {
        return AssertionStep.class.getCanonicalName();
    }

    @Override
    public Map<String, Class<? extends Handler>> getRequiredServices(Step step) {
        return Map.of();
    }

    @Override
    public StepRunner createStepRunner(Step step, Set<Handler> handlers) {
        return new AssertionStepRunner(new AssertionHandlerImpl());
    }

}
