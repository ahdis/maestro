package net.ihe.gazelle.technical.configuration;

import jakarta.enterprise.context.ApplicationScoped;
import net.ihe.gazelle.servicemetadata.api.domain.structure.Service;
import net.ihe.gazelle.technical.ServiceRegistry;

import java.util.List;
import java.util.NoSuchElementException;

@ApplicationScoped
public class ServiceRegistryImpl implements ServiceRegistry {

    private final ServiceRegistryDAO serviceRegistryDAO;

    private List<Service> services = null;

    public ServiceRegistryImpl(ServiceRegistryDAO serviceRegistryDAO){
        this.serviceRegistryDAO = serviceRegistryDAO;
    }

    @Override
    public List<Service> getServices() {
        if(services == null) {
            services = serviceRegistryDAO.loadFile();
        }
        return services;
    }

    @Override
    public Service getService(String serviceName) {
        if(serviceName == null) {
            throw new IllegalArgumentException("service name is required");
        }
        return getServices().stream().filter(service -> serviceName.equals(service.getName()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No service with name " + serviceName));
    }
}
