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

package net.ihe.gazelle.maestro.quarkus.handler;

import net.ihe.gazelle.maestro.simulation.step.business.SimulationHandler;
import net.ihe.gazelle.maestro.spi.business.Handler;
import net.ihe.gazelle.maestro.userinteraction.step.business.UserInteractionHandler;
import net.ihe.gazelle.maestro.userinteraction.step.business.UserInteractionHandlerImpl;
import net.ihe.gazelle.maestro.validation.step.business.ValidationHandler;
import net.ihe.gazelle.maestro.spi.technical.HandlerProvider;

public class HandlerProviderMock implements HandlerProvider {

    @Override
    public Handler getHandler(String serviceName, Class<? extends Handler> handlerType) {
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
