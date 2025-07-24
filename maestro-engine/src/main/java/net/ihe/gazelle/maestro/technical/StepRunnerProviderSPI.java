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

package net.ihe.gazelle.maestro.technical;

import net.ihe.gazelle.maestro.api.business.testrun.Step;
import net.ihe.gazelle.maestro.api.business.testrun.runner.Handler;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRunner;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRunnerFactory;
import net.ihe.gazelle.maestro.technical.handler.HandlerProvider;
import net.ihe.gazelle.maestro.technical.jobs.StepRunnerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

public class StepRunnerProviderSPI implements StepRunnerProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(StepRunnerProviderSPI.class);

    private final HandlerProvider handlerProvider;

    public StepRunnerProviderSPI(HandlerProvider handlerProvider) {
        this.handlerProvider = handlerProvider;
    }

    /**
     * Will throw an IllegalArgumentException if it can't provide corresponding stepRunner
     * @param step The testreport we are want to run
     * @return a stepRunner corresponding to testreport
     */
    @Override
    public StepRunner create(Step step) {
        ServiceLoader<StepRunnerFactory> serviceLoader = ServiceLoader.load(StepRunnerFactory.class);
        for (StepRunnerFactory runnerFactory : serviceLoader) {
            if (runnerFactory.getSupportedStep().equals(step.getClass().getCanonicalName())) {
                return buildStepRunner(step, runnerFactory);
            }
        }
        IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Step class " + step.getClass() + " is not supported");
        LOGGER.error(illegalArgumentException.getMessage(), illegalArgumentException);
        throw illegalArgumentException;
    }

    private StepRunner buildStepRunner(Step step, StepRunnerFactory runnerFactory) {
        Set<Handler> handlers = new HashSet<>();
        runnerFactory.getRequiredServices(step).forEach(
                (serviceName, handlerType) -> addHandlerIfAvailable(serviceName, handlerType, handlers)
        );
        return runnerFactory.createStepRunner(step, handlers);
    }

    private void addHandlerIfAvailable(String serviceName, Class<? extends Handler> handlerType, Set<Handler> handlers) {
        Handler handler = handlerProvider.getHandler(handlerType, serviceName);
        if (handler.isAvailable()) {
            handlers.add(handler);
        }
    }
}
