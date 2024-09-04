package net.ihe.gazelle.technical.configuration;

import net.ihe.gazelle.servicemetadata.api.domain.structure.Service;
import net.ihe.gazelle.technical.ServiceRegistry;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class ServiceRegistryImpl implements ServiceRegistry {

    @ConfigProperty(name = "SERVICE_REGISTRY_FILE_PATH", defaultValue = "/opt/maestro/services.json")
    private String servicesFilePath;

    private List<Service> services = null;

    @Override
    public List<Service> getServices() {
        if(services == null) {
            services = loadFile(servicesFilePath);
        }
        return services;
    }

    private List<Service> loadFile(String servicesFilePath) {
        return new ArrayList<>();
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
