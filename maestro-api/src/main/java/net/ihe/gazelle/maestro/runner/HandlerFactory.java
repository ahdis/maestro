package net.ihe.gazelle.maestro.runner;

import net.ihe.gazelle.servicemetadata.api.domain.structure.Service;

public interface HandlerFactory {

    /**
     * @return Handler Implementation class
     */
    Class<? extends Handler> getHandlerType();

    /**
     *
     * @param service A service for which we are looking for Handler
     * @return A Handler that can provide interaction with service
     * @throws IllegalArgumentException if service has no binding set
     */
    Handler createHandler(Service service) throws IllegalArgumentException;

}
