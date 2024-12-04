package net.ihe.gazelle.maestro.technical.configuration;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Produces;

@ApplicationScoped
public class ServiceRegistryDAOFactory {

    @Produces
    ServiceRegistryDAO getServiceRegistry(){
        return new ServiceRegistryDAOImpl();
    }

}
