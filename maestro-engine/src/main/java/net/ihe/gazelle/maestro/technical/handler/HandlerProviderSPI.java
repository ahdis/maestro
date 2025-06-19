package net.ihe.gazelle.maestro.technical.handler;

import net.ihe.gazelle.lang.GzlCollectors;
import net.ihe.gazelle.maestro.api.business.testrun.runner.ConfigProvider;
import net.ihe.gazelle.maestro.api.business.testrun.runner.Handler;
import net.ihe.gazelle.maestro.api.business.testrun.runner.HandlerFactory;
import net.ihe.gazelle.maestro.technical.ServiceRegistry;
import net.ihe.gazelle.servicemetadata.api.business.ConsumedInterface;
import net.ihe.gazelle.servicemetadata.api.business.ProvidedInterface;
import net.ihe.gazelle.servicemetadata.api.business.Service;

import java.util.NoSuchElementException;
import java.util.ServiceLoader;

public class HandlerProviderSPI implements HandlerProvider {

    private final ServiceRegistry serviceRegistry;
    private final ConfigProvider configProvider;

    public HandlerProviderSPI(ServiceRegistry serviceRegistry, ConfigProvider configProvider) {
        this.serviceRegistry = serviceRegistry;
        this.configProvider = configProvider;
    }

    @Override
    public Handler getHandler(Class<? extends Handler> requestedHandlerType, String serviceName) {
        ServiceLoader<HandlerFactory> serviceLoader = ServiceLoader.load(HandlerFactory.class);
        Service service = serviceRegistry.getService(serviceName);
        for (HandlerFactory handlerFactory : serviceLoader) {
            ProvidedInterface providedInterface = getMatchingInterface(service, handlerFactory.getConsumedInterface());
            if (providedInterface != null && requestedHandlerType.isAssignableFrom(handlerFactory.getHandlerType())) {
                return handlerFactory.createHandler(providedInterface, configProvider);
            }
        }
        throw new HandlerNotFoundException("Unable to find a handler implementation for type: "+ requestedHandlerType.getCanonicalName());
    }

    private ProvidedInterface getMatchingInterface(Service service, ConsumedInterface consumedInterface) {
        try {
            return service.getProvidedInterfaces().stream().filter(
                    providedInterface -> providedInterface.getInterfaceName().equals(consumedInterface.getInterfaceName())
            ).collect(GzlCollectors.toSingleton());
        } catch (NoSuchElementException e) {
            return null;
        }
    }

}
