/*
 * Copyright 2025-2026 IHE International.
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

package technical.provider;

import net.ihe.gazelle.lang.GzlCollectors;
import net.ihe.gazelle.maestro.api.business.MaestroObserver;
import net.ihe.gazelle.maestro.api.business.userinteract.GradingHandler;
import net.ihe.gazelle.maestro.api.business.userinteract.UserInteractionHandler;
import net.ihe.gazelle.maestro.engine.business.context.ReadSessionStore;
import net.ihe.gazelle.maestro.spi.business.Handler;
import net.ihe.gazelle.maestro.spi.technical.*;
import net.ihe.gazelle.servicemetadata.api.business.ConsumedInterface;
import net.ihe.gazelle.servicemetadata.api.business.ProvidedInterface;
import net.ihe.gazelle.servicemetadata.api.business.Service;
import technical.userinteract.GradingHandlerImpl;
import technical.userinteract.UserInteractionHandlerImpl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;

/**
 * Service Provider Interface (SPI) implementation of {@link HandlerProvider}.
 * Responsible for providing {@link Handler} instances for a given {@link HandlerContext} and handler type.
 * It can create handlers via service discovery or return a {@link UserInteractionHandler} when requested.
 */
public class HandlerProviderSPI implements HandlerProvider {

    private final ServiceRegistry serviceRegistry;
    private final ConfigProvider configProvider;
    private final ReadSessionStore<MaestroObserver> testSuiteRunObserverStore;
    private final ReadSessionStore<MaestroObserver> testRunObserverStore;

    /**
     * Creates a new {@code HandlerProviderSPI} with the specified service registry, configuration provider,
     * and observer store.
     *
     * @param serviceRegistry the registry used to look up services and their interfaces
     * @param configProvider the configuration provider used to configure handlers
     * @param testSuiteRunObserverStore the session store for {@link MaestroObserver} instances
     * @param testRunObserverStore the store containing {@link MaestroObserver} instances, used for user interaction handlers
     */
    public HandlerProviderSPI(ServiceRegistry serviceRegistry,
                              ConfigProvider configProvider,
                              ReadSessionStore<MaestroObserver> testSuiteRunObserverStore,
                              ReadSessionStore<MaestroObserver> testRunObserverStore) {
        this.serviceRegistry = serviceRegistry;
        this.configProvider = configProvider;
        this.testSuiteRunObserverStore = testSuiteRunObserverStore;
        this.testRunObserverStore = testRunObserverStore;
    }

    @Override
    public Handler getHandler(HandlerContext handlerContext, Class<? extends Handler> requestedHandlerType) {
        MaestroObserver observer = getObserver(handlerContext.sessionId());
        if (requestedHandlerType.equals(UserInteractionHandler.class)) {
            return new UserInteractionHandlerImpl(observer);
        }
        if (requestedHandlerType.equals(GradingHandler.class)) {
            return new GradingHandlerImpl(observer, configProvider);
        }
        ServiceLoader<HandlerFactory> serviceLoader = ServiceLoader.load(HandlerFactory.class);
        Service service = serviceRegistry.getService(handlerContext.serviceName());
        for (HandlerFactory handlerFactory : serviceLoader) {
            ProvidedInterface providedInterface = getMatchingInterface(service, handlerFactory.getConsumedInterfaces());
            if (providedInterface != null && requestedHandlerType.isAssignableFrom(handlerFactory.getHandlerType())) {
                return handlerFactory.createHandler(handlerContext, providedInterface, configProvider);
            }
        }
        throw new HandlerNotFoundException("Unable to find a handler implementation for type: " + requestedHandlerType.getCanonicalName());
    }

    private MaestroObserver getObserver(String sessionId) {
        MaestroObserver observer = testSuiteRunObserverStore.getSession(sessionId);
        if (observer == null) {
            observer = testRunObserverStore.getSession(sessionId);
        }
        return observer;
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
