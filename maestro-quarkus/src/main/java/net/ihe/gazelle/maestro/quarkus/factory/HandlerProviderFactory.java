package net.ihe.gazelle.maestro.quarkus.factory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import jakarta.ws.rs.Produces;
import net.ihe.gazelle.maestro.api.business.MaestroObserver;
import net.ihe.gazelle.maestro.engine.business.context.ReadSessionStore;
import net.ihe.gazelle.maestro.spi.technical.ConfigProvider;
import net.ihe.gazelle.maestro.spi.technical.HandlerProvider;
import technical.provider.HandlerProviderSPI;
import technical.provider.ServiceRegistry;

/**
 * Factory class responsible for creating {@link HandlerProvider} instances.
 */
@ApplicationScoped
public class HandlerProviderFactory {

   private final ServiceRegistry serviceRegistry;
   private final ConfigProvider configProvider;
   private final ReadSessionStore<MaestroObserver> observerStore;

   /**
    * Creates a new {@code HandlerProviderFactory} with the specified dependencies.
    *
    * @param serviceRegistry the service registry used to resolve services
    * @param configProvider the configuration provider used to inject configurations
    * @param observerStore the session store for {@link MaestroObserver} instances
    */
   @Inject
   public HandlerProviderFactory(ServiceRegistry serviceRegistry, ConfigProvider configProvider, @TestRunObserverStore ReadSessionStore<MaestroObserver> observerStore) {
      this.serviceRegistry = serviceRegistry;
      this.configProvider = configProvider;
      this.observerStore = observerStore;
   }

   /**
    * Produces a {@link HandlerProvider} instance configured with the injected dependencies.
    *
    * @return a new {@link HandlerProviderSPI} instance
    */
   @Produces
   @Default
   public HandlerProvider getHandlerProvider() {
      return new HandlerProviderSPI(serviceRegistry, configProvider, observerStore);
   }
}
