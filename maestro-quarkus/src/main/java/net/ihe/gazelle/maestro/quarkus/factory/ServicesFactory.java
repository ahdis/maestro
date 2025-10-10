package net.ihe.gazelle.maestro.quarkus.factory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import jakarta.ws.rs.Produces;
import net.ihe.gazelle.maestro.api.business.testrun.runner.ConfigProvider;
import net.ihe.gazelle.maestro.business.context.SessionStore;
import net.ihe.gazelle.maestro.business.context.SessionStoreImpl;
import net.ihe.gazelle.maestro.business.StepRunnerProvider;
import net.ihe.gazelle.maestro.quarkus.ServiceRegistry;
import net.ihe.gazelle.maestro.quarkus.StepRunnerProviderSPI;
import net.ihe.gazelle.maestro.quarkus.handler.HandlerProviderSPI;

public class ServicesFactory {

   private final ServiceRegistry serviceRegistry;
   private final ConfigProvider configProvider;

   @Inject
   public ServicesFactory(ServiceRegistry serviceRegistry, ConfigProvider configProvider) {
      this.serviceRegistry = serviceRegistry;
      this.configProvider = configProvider;
   }

   @Produces
   @Default
   public StepRunnerProvider getStepRunnerProvider() {
      return new StepRunnerProviderSPI(new HandlerProviderSPI(serviceRegistry, configProvider));
   }

   @ApplicationScoped
   @Produces
   @Default
   public SessionStore getSessionStore() {
      return new SessionStoreImpl();
   }

}
