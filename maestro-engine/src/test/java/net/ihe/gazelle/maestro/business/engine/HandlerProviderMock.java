package net.ihe.gazelle.maestro.business.engine;

import net.ihe.gazelle.maestro.runner.Handler;
import net.ihe.gazelle.maestro.technical.handler.HandlerProvider;

public class HandlerProviderMock implements HandlerProvider {

    @Override
    public Handler getHandler(Class<? extends Handler> handlerType, String serviceName) {
        return new ValidationHandlerMock();
    }
}
