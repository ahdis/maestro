package net.ihe.gazelle.maestro.api.business.testrun.runner;

import net.ihe.gazelle.servicemetadata.api.business.ConsumedInterface;
import net.ihe.gazelle.servicemetadata.api.business.ProvidedInterface;

public interface HandlerFactory {

    /**
     * @return Handler Implementation class
     */
    Class<? extends Handler> getHandlerType();

    /**
     *
     * @param providedInterface The provided interface used by the handler
     * @return A Handler that can provide interaction with service
     * @throws IllegalArgumentException if service has no binding set
     */
    Handler createHandler(ProvidedInterface providedInterface, ConfigProvider configProvider) throws IllegalArgumentException;

    /**
     * Get the interface consumed
     * @return
     */
    ConsumedInterface getConsumedInterface();

}
