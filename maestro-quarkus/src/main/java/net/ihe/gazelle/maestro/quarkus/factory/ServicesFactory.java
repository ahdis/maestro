package net.ihe.gazelle.maestro.quarkus.factory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import jakarta.ws.rs.Produces;
import net.ihe.gazelle.maestro.engine.business.context.TestRunSession;
import net.ihe.gazelle.maestro.engine.business.context.TestSuiteSession;
import net.ihe.gazelle.maestro.spi.business.runner.ConfigProvider;
import net.ihe.gazelle.maestro.engine.business.context.SessionStore;
import net.ihe.gazelle.maestro.engine.business.context.SessionStoreImpl;
import net.ihe.gazelle.maestro.engine.business.StepRunnerProvider;
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
   @TestSuiteStore
   public SessionStore<TestSuiteSession> getTestSuiteSessionStore() {
      return new SessionStoreImpl<TestSuiteSession>();
   }

   @ApplicationScoped
   @Produces
   @TestRunStore
   public SessionStore<TestRunSession> getTestRunSessionStore() {
      return new SessionStoreImpl<TestRunSession>();
   }


}
