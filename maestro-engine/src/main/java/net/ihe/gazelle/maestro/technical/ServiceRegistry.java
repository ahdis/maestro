package net.ihe.gazelle.maestro.technical;

import net.ihe.gazelle.servicemetadata.api.business.Service;

import java.util.List;

public interface ServiceRegistry {

    /**
     * @return List of services from Database
     */
    List<Service> getServices();

    /**
     * @param serviceName Name of the service 
     * @return Service with name corresponding to serviceName
     * @throws IllegalArgumentException if there is no Service with corresponding name in DB
     */
    Service getService(String serviceName);
}
