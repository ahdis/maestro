package net.ihe.gazelle.handler;

import net.ihe.gazelle.runner.Handler;
import net.ihe.gazelle.runner.HandlerFactory;
import net.ihe.gazelle.technical.ServiceRegistry;

import java.util.Iterator;
import java.util.ServiceLoader;

public class HandlerProviderSPI implements HandlerProvider {

    private final ServiceRegistry serviceRegistry;

    public HandlerProviderSPI(ServiceRegistry serviceRegistry){
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public Handler getHandler(Class<? extends Handler> handlerType, String serviceName) {
        ServiceLoader<HandlerFactory> serviceLoader = ServiceLoader.load(HandlerFactory.class);
        Iterator<HandlerFactory> it = serviceLoader.iterator();
        while (it.hasNext()) {
            HandlerFactory handlerFactory = it.next();
            if (handlerType.isAssignableFrom(handlerFactory.getHandlerType())) {
                return handlerFactory.createHandler(serviceRegistry.getService(serviceName));
            }
        }
        throw new HandlerNotFoundException("Unable to find a handler implementation for type: "+ handlerType.getCanonicalName());
    }

}
