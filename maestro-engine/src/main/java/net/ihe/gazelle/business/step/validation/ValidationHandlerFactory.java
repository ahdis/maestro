package net.ihe.gazelle.business.step.validation;

import net.ihe.gazelle.runner.Handler;
import net.ihe.gazelle.runner.HandlerFactory;
import net.ihe.gazelle.servicemetadata.api.domain.structure.RestBinding;
import net.ihe.gazelle.servicemetadata.api.domain.structure.Service;
import net.ihe.gazelle.validation.api.domain.metadata.structure.ValidationProfile;
import net.ihe.gazelle.validation.api.domain.report.structure.ValidationReport;
import net.ihe.gazelle.validation.api.domain.request.structure.ValidationRequest;

import java.util.List;

public class ValidationHandlerFactory implements HandlerFactory {
    @Override
    public Class<? extends Handler> getHandlerType() {
        return ValidationHandler.class;
    }

    @Override
    public Handler createHandler(Service service) {
        //TODO To refactor with validation service api v2
        RestBinding binding = (RestBinding)service.getProvidedInterfaces().getFirst().getBindings().getFirst();

        return new ValidationHandlerMock();
//        return new ValidationHandler(binding.getServiceUrl());
    }
    static class ValidationHandlerMock extends ValidationHandler {


        public ValidationHandlerMock() {
            /*Empty constructor used for mocking*/
        }

        @Override
        public boolean isAvailable(String service) {
            return true;
        }

        @Override
        public ValidationReport validate(ValidationRequest validationRequest) {
            return new ValidationReport();
        }

        @Override
        public List<ValidationProfile> getValidationProfiles() {
            return List.of();
        }
    }
}

