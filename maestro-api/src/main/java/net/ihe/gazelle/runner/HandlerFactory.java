package net.ihe.gazelle.runner;

import net.ihe.gazelle.servicemetadata.api.domain.structure.Service;

public interface HandlerFactory {

    Class<? extends Handler> getHandlerType();

    Handler createHandler(Service service);

}
