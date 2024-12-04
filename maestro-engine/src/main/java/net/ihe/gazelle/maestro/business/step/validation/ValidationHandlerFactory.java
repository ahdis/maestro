package net.ihe.gazelle.maestro.business.step.validation;

import net.ihe.gazelle.maestro.runner.Handler;
import net.ihe.gazelle.maestro.runner.HandlerFactory;
import net.ihe.gazelle.servicemetadata.api.domain.structure.RestBinding;
import net.ihe.gazelle.servicemetadata.api.domain.structure.Service;

public class ValidationHandlerFactory implements HandlerFactory {
    @Override
    public Class<? extends Handler> getHandlerType() {
        return ValidationHandler.class;
    }

    @Override
    public Handler createHandler(Service service) throws IllegalArgumentException {
        //TODO To refactor with validation service api v2
        try {
            RestBinding binding = (RestBinding) service.getProvidedInterfaces().getFirst().getBindings().getFirst();
            return new ValidationHandler(binding.getServiceUrl());
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Service " + service.getName() + " has no bindings");
        }
    }
}

