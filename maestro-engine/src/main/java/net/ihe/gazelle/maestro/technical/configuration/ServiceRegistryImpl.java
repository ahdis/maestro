package net.ihe.gazelle.maestro.technical.configuration;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.ihe.gazelle.maestro.business.step.userinteract.UserInteractionHandler;
import net.ihe.gazelle.maestro.technical.ServiceRegistry;
import net.ihe.gazelle.servicemetadata.api.business.HttpRestBindingBuilder;
import net.ihe.gazelle.servicemetadata.api.business.ProvidedInterfaceBuilder;
import net.ihe.gazelle.servicemetadata.api.business.Service;
import net.ihe.gazelle.servicemetadata.api.business.ServiceBuilder;

import java.util.List;
import java.util.NoSuchElementException;

@ApplicationScoped
public class ServiceRegistryImpl implements ServiceRegistry {

    private final ServiceRegistryDAO serviceRegistryDAO;

    private List<Service> services = null;

    @Inject
    public ServiceRegistryImpl(ServiceRegistryDAO serviceRegistryDAO){
        this.serviceRegistryDAO = serviceRegistryDAO;
    }

    @Override
    public List<Service> getServices() {
        if (services == null) {
            services = serviceRegistryDAO.getServices();
            addInternalServices();
        }
        return services;
    }

    @Override
    public Service getService(String serviceName) {
        if (serviceName == null) {
            throw new IllegalArgumentException("service name is required");
        }
        return getServices().stream().filter(service -> serviceName.equals(service.getName()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No service with name " + serviceName));
    }

    private void addInternalServices() {
        services.add(new ServiceBuilder()
                .setVersion("1.0.0")
                .setInstanceId("112233")
                .setReplicaId("112")
                .setName(UserInteractionHandler.NAME)
                .addProvidedInterfaceBuilder(new ProvidedInterfaceBuilder()
                        .setInterfaceVersion("1.0.0")
                        .setInterfaceName(UserInteractionHandler.INTERFACE_NAME)
                        .addBinding(
                                new HttpRestBindingBuilder()
                                        .setServiceUrl("http://localhost:8080/maestro")
                        )
                )
                .build()
        );
    }
}
