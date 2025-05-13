package net.ihe.gazelle.maestro.business.step.validation;

import net.ihe.gazelle.maestro.runner.Handler;
import net.ihe.gazelle.maestro.runner.HandlerFactory;
import net.ihe.gazelle.servicemetadata.api.business.HttpRestBinding;
import net.ihe.gazelle.servicemetadata.api.business.Service;

public class ValidationHandlerFactory implements HandlerFactory {
    @Override
    public Class<? extends Handler> getHandlerType() {
        return ValidationHandler.class;
    }

    @Override
    public Handler createHandler(Service service) throws IllegalArgumentException {
        try {
            HttpRestBinding binding = (HttpRestBinding) service.getProvidedInterfaces().getFirst().getBindings().getFirst();
            return new ValidationHandler(binding.getServiceUrl());
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Service " + service.getName() + " has no bindings");
        }
    }
}

