package net.ihe.gazelle.handler;

import net.ihe.gazelle.runner.Handler;

public interface HandlerProvider {

    Handler getHandler(Class<? extends Handler> handlerType, String serviceName);

    class HandlerNotFoundException extends RuntimeException {
        public HandlerNotFoundException(String message) {
            super(message);
        }
    }
}
