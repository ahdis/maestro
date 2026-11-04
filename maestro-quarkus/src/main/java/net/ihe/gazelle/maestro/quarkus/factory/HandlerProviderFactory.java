/*
 * Copyright 2026 IHE International.
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

package net.ihe.gazelle.maestro.quarkus.factory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import jakarta.ws.rs.Produces;
import net.ihe.gazelle.maestro.api.business.MaestroObserver;
import net.ihe.gazelle.maestro.engine.business.context.ReadSessionStore;
import net.ihe.gazelle.maestro.engine.technical.provider.HandlerProviderSPI;
import net.ihe.gazelle.maestro.engine.technical.provider.ServiceRegistry;
import net.ihe.gazelle.maestro.spi.technical.ConfigProvider;
import net.ihe.gazelle.maestro.spi.technical.HandlerProvider;

/**
 * Factory class responsible for creating {@link HandlerProvider} instances.
 */
@ApplicationScoped
public class HandlerProviderFactory {

   private final ServiceRegistry serviceRegistry;
   private final ConfigProvider configProvider;
   private final ReadSessionStore<MaestroObserver> testSuiteRunObserverStore;
   private final ReadSessionStore<MaestroObserver> testRunObserverStore;

   /**
    * Creates a new {@code HandlerProviderFactory} with the specified dependencies.
    *
    * @param serviceRegistry the service registry used to resolve services
    * @param configProvider the configuration provider used to inject configurations
    * @param testSuiteRunObserverStore the session store for {@link MaestroObserver} instances
    * @param testRunObserverStore the session store for {@link MaestroObserver} instances
    */
   @Inject
   public HandlerProviderFactory(ServiceRegistry serviceRegistry,
                                 ConfigProvider configProvider,
                                 @TestSuiteRunObserverStore ReadSessionStore<MaestroObserver> testSuiteRunObserverStore,
                                 @TestRunObserverStore ReadSessionStore<MaestroObserver> testRunObserverStore
   ) {
      this.serviceRegistry = serviceRegistry;
      this.configProvider = configProvider;
      this.testSuiteRunObserverStore = testSuiteRunObserverStore;
      this.testRunObserverStore = testRunObserverStore;
   }

   /**
    * Produces a {@link HandlerProvider} instance configured with the injected dependencies.
    *
    * @return a new {@link HandlerProviderSPI} instance
    */
   @Produces
   @Default
   public HandlerProvider getHandlerProvider() {
      return new HandlerProviderSPI(serviceRegistry, configProvider, testSuiteRunObserverStore, testRunObserverStore);
   }
}
