package net.ihe.gazelle.maestro.technical.handler;

import net.ihe.gazelle.maestro.api.business.testrun.runner.Handler;
import net.ihe.gazelle.maestro.api.business.testrun.runner.HandlerFactory;
import net.ihe.gazelle.maestro.technical.ServiceRegistry;

import java.util.ServiceLoader;

public class HandlerProviderSPI implements HandlerProvider {

    private final ServiceRegistry serviceRegistry;

    public HandlerProviderSPI(ServiceRegistry serviceRegistry){
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public Handler getHandler(Class<? extends Handler> handlerType, String serviceName) {
        ServiceLoader<HandlerFactory> serviceLoader = ServiceLoader.load(HandlerFactory.class);
        for (HandlerFactory handlerFactory : serviceLoader) {
            if (handlerType.isAssignableFrom(handlerFactory.getHandlerType())) {
                return handlerFactory.createHandler(serviceRegistry.getService(serviceName));
            }
        }
        throw new HandlerNotFoundException("Unable to find a handler implementation for type: "+ handlerType.getCanonicalName());
    }

}
