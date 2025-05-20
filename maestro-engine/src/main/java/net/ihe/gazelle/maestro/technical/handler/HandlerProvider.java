package net.ihe.gazelle.maestro.technical.handler;

import net.ihe.gazelle.maestro.api.business.testrun.runner.Handler;

public interface HandlerProvider {

    /**
     * @param handlerType Class of Handler we are looking for
     * @param serviceName The name of service we want Handler to connect with
     * @return a Handler of type handlerType which connect with a service with name serviceName
     * @throws HandlerNotFoundException if it couldn't find a Handler of type handlerTyper
     */
    Handler getHandler(Class<? extends Handler> handlerType, String serviceName);


}
