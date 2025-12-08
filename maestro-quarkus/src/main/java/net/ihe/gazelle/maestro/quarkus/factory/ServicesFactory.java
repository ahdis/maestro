package net.ihe.gazelle.maestro.quarkus.factory;

import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import jakarta.ws.rs.Produces;
import net.ihe.gazelle.maestro.engine.business.StepExecutorProvider;
import net.ihe.gazelle.maestro.spi.technical.HandlerProvider;
import technical.provider.StepExecutorSPIProvider;

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
}
