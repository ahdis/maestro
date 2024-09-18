package net.ihe.gazelle.business.step.validation;

import net.ihe.gazelle.runner.Handler;
import net.ihe.gazelle.runner.HandlerFactory;
import net.ihe.gazelle.servicemetadata.api.domain.structure.RestBinding;
import net.ihe.gazelle.servicemetadata.api.domain.structure.Service;

public class ValidationHandlerFactory implements HandlerFactory {
    @Override
    public Class<? extends Handler> getHandlerType() {
        return ValidationHandler.class;
    }

    @Override
    public Handler createHandler(Service service) {
        //TODO To refactor with validation service api v2
        RestBinding binding = (RestBinding)service.getProvidedInterfaces().getFirst().getBindings().getFirst();

        return new ValidationHandler(binding.getServiceUrl());
    }
}
