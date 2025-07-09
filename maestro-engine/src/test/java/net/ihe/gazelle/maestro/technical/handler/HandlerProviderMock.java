/*
 * Copyright 2025 IHE International.
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

package net.ihe.gazelle.maestro.technical.handler;

import net.ihe.gazelle.maestro.api.business.testrun.runner.Handler;
import net.ihe.gazelle.maestro.business.step.simulation.SimulationHandler;
import net.ihe.gazelle.maestro.business.step.simulation.SimulationHandlerMock;
import net.ihe.gazelle.maestro.business.step.userinteract.UserInteractionHandler;
import net.ihe.gazelle.maestro.business.step.userinteract.UserInteractionHandlerImpl;
import net.ihe.gazelle.maestro.business.step.validation.ValidationHandler;
import net.ihe.gazelle.maestro.business.step.validation.ValidationHandlerMock;

public class HandlerProviderMock implements HandlerProvider {

    @Override
    public Handler getHandler(Class<? extends Handler> handlerType, String serviceName) {
        if (handlerType.isAssignableFrom(ValidationHandler.class)) {
            return new ValidationHandlerMock();
        } else if (handlerType.isAssignableFrom(SimulationHandler.class)) {
            return new SimulationHandlerMock(null);
        } else if (handlerType.isAssignableFrom(UserInteractionHandler.class)) {
            return new UserInteractionHandlerImpl();
        }
        return null;
    }
}
