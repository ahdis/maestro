package net.ihe.gazelle.technical;

import net.ihe.gazelle.servicemetadata.api.domain.structure.Service;

import java.util.List;

public interface ServiceRegistry {

    List<Service> getServices();

    Service getService(String serviceName);
}
