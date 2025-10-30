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

package net.ihe.gazelle.maestro.spi.business.runner;

import net.ihe.gazelle.maestro.api.business.test.Step;

import java.util.Map;
import java.util.Set;

public interface StepRunnerFactory {

    /**
     * @return Type name of the supported Step
     */
    String getSupportedStep();

    Map<String, Class<? extends Handler>> getRequiredServices(Step step);

    /**
     * @param step step to run
     * @param handlers a set of handlers
     * @return a StepExecutor instance that can run step using a Handler from handlers
     * @throws IllegalArgumentException if no handler from handlers can execute step
     */
    StepExecutor createStepRunner(Step step, Set<Handler> handlers);
}
