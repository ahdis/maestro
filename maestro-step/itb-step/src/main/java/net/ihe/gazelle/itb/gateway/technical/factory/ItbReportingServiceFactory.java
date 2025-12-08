package net.ihe.gazelle.itb.gateway.technical.factory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import net.ihe.gazelle.itb.gateway.business.ItbClient;
import net.ihe.gazelle.itb.gateway.business.ItbReportingService;
import net.ihe.gazelle.itb.gateway.technical.dao.ItbSessionStoreImpl;

@ApplicationScoped
public class ItbReportingServiceFactory {

   private final ItbClient itbClient;

   @Inject
   public ItbReportingServiceFactory(ItbClient itbClient) {
      this.itbClient = itbClient;
   }

   @Default
   @Produces
   public ItbReportingService createItbReportingService() {
      return new ItbReportingService(itbClient, new ItbSessionStoreImpl());
   }

}
