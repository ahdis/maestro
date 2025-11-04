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

package net.ihe.gazelle.maestro.spi.technical;

import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.spi.business.Handler;
import net.ihe.gazelle.maestro.spi.business.StepExecutor;

import java.util.Map;

public interface StepExecutorFactory {

   /**
    * @return Type name of the supported Step
    */
   String getSupportedStep();

   /**
    * Get the list of required services to run the step
    *
    * @param step
    *
    * @return a map containing the service names as keys and the required Handler class as values
    */
   Map<String, Class<? extends Handler>> getRequiredServices(Step step);

   /**
    * @param step     step to run
    * @param handlers a map of services (name as key and, and handlers as value) that can be used to run the step
    *
    * @return a StepExecutor instance that can run step using a Handler from handlers
    *
    * @throws IllegalArgumentException if the provided handlers are not suitable to run the step
    */
   StepExecutor createStepExecutor(Step step, Map<String, Handler> handlers);

}
