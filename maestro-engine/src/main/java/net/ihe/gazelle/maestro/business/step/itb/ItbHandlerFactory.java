package net.ihe.gazelle.maestro.business.step.itb;

import net.ihe.gazelle.maestro.runner.Handler;
import net.ihe.gazelle.maestro.runner.HandlerFactory;
import net.ihe.gazelle.servicemetadata.api.business.Service;

public class ItbHandlerFactory implements HandlerFactory {
    @Override
    public Class<? extends Handler> getHandlerType() {
        return ItbHandler.class;
    }

    @Override
    public Handler createHandler(Service service) throws IllegalArgumentException {
        return new ItbHandler();
    }
}

