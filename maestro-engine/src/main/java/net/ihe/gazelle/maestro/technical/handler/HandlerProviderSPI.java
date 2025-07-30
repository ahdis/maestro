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

package net.ihe.gazelle.maestro.technical.handler;

import net.ihe.gazelle.lang.GzlCollectors;
import net.ihe.gazelle.maestro.api.business.testrun.runner.ConfigProvider;
import net.ihe.gazelle.maestro.api.business.testrun.runner.Handler;
import net.ihe.gazelle.maestro.api.business.testrun.runner.HandlerFactory;
import net.ihe.gazelle.maestro.technical.ServiceRegistry;
import net.ihe.gazelle.servicemetadata.api.business.ConsumedInterface;
import net.ihe.gazelle.servicemetadata.api.business.ProvidedInterface;
import net.ihe.gazelle.servicemetadata.api.business.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;

public class HandlerProviderSPI implements HandlerProvider {

    private final ServiceRegistry serviceRegistry;
    private final ConfigProvider configProvider;

    public HandlerProviderSPI(ServiceRegistry serviceRegistry, ConfigProvider configProvider) {
        this.serviceRegistry = serviceRegistry;
        this.configProvider = configProvider;
    }

    @Override
    public Handler getHandler(Class<? extends Handler> requestedHandlerType, String serviceName) {
        ServiceLoader<HandlerFactory> serviceLoader = ServiceLoader.load(HandlerFactory.class);
        Service service = serviceRegistry.getService(serviceName);
        for (HandlerFactory handlerFactory : serviceLoader) {
            ProvidedInterface providedInterface = getMatchingInterface(service, handlerFactory.getConsumedInterfaces());
            if (providedInterface != null && requestedHandlerType.isAssignableFrom(handlerFactory.getHandlerType())) {
                return handlerFactory.createHandler(providedInterface, configProvider);
            }
        }
        throw new HandlerNotFoundException("Unable to find a handler implementation for type: " + requestedHandlerType.getCanonicalName());
    }

    private ProvidedInterface getMatchingInterface(Service service, List<ConsumedInterface> consumedInterfaces) {
        try {
            return service.getProvidedInterfaces().stream().filter(
                    providedInterface -> consumedInterfaces.stream()
                            .anyMatch(consumedInterface -> providedInterface.getInterfaceName() != null &&
                                    providedInterface.getInterfaceName().equals(consumedInterface.getInterfaceName()))
                    ).collect(GzlCollectors.toSingleton());
        } catch (NoSuchElementException e) {
            return null;
        }
    }

}
