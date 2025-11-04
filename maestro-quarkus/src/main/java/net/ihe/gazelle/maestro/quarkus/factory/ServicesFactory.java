package net.ihe.gazelle.maestro.quarkus.factory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import jakarta.ws.rs.Produces;
import net.ihe.gazelle.maestro.engine.business.StepExecutorProvider;
import net.ihe.gazelle.maestro.engine.business.context.SessionStore;
import net.ihe.gazelle.maestro.spi.technical.HandlerProvider;
import technical.dao.SessionStoreImpl;
import net.ihe.gazelle.maestro.engine.business.context.TestRunSession;
import net.ihe.gazelle.maestro.engine.business.context.TestSuiteSession;
import technical.provider.ServiceRegistry;
import technical.provider.StepExecutorSPIProvider;
import technical.provider.HandlerProviderSPI;
import net.ihe.gazelle.maestro.spi.technical.ConfigProvider;

public class ServicesFactory {

   private final HandlerProvider handlerProvider;

   @Inject
   public ServicesFactory(HandlerProvider handlerProvider) {
      this.handlerProvider = handlerProvider;
   }

   @Produces
   @Default
   public StepExecutorProvider getStepRunnerProvider() {
      return new StepExecutorSPIProvider(handlerProvider);
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
