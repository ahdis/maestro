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

@ApplicationScoped
public class HandlerProviderFactory {

   private final ServiceRegistry serviceRegistry;
   private final ConfigProvider configProvider;
   private final ReadSessionStore<MaestroObserver> observerStore;

   @Inject
   public HandlerProviderFactory(ServiceRegistry serviceRegistry, ConfigProvider configProvider, @ObserverStore ReadSessionStore<MaestroObserver> observerStore) {
      this.serviceRegistry = serviceRegistry;
      this.configProvider = configProvider;
      this.observerStore = observerStore;
   }

   @Produces
   @Default
   public HandlerProvider getHandlerProvider() {
      return new HandlerProviderSPI(serviceRegistry, configProvider, observerStore);
   }

}
