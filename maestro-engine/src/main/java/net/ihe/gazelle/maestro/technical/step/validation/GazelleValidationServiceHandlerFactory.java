package net.ihe.gazelle.maestro.technical.step.validation;

import net.ihe.gazelle.maestro.api.business.testrun.runner.ConfigProvider;
import net.ihe.gazelle.maestro.api.business.testrun.runner.Handler;
import net.ihe.gazelle.maestro.api.business.testrun.runner.HandlerFactory;
import net.ihe.gazelle.maestro.business.step.validation.ServiceClientException;
import net.ihe.gazelle.servicemetadata.api.business.ConsumedInterface;
import net.ihe.gazelle.servicemetadata.api.business.ConsumedInterfaceBuilder;
import net.ihe.gazelle.servicemetadata.api.business.HttpRestBinding;
import net.ihe.gazelle.servicemetadata.api.business.ProvidedInterface;
import net.ihe.gazelle.validation.v1.api.domain.wrapper.ValidationServiceWrapper;
import net.ihe.gazelle.validation.v2.api.technical.ws.ValidationServiceController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class GazelleValidationServiceHandlerFactory implements HandlerFactory {

    @Override
    public Class<? extends Handler> getHandlerType() {
        return GazelleValidationServiceHandler.class;
    }

    @Override
    public Handler createHandler(ProvidedInterface providedInterface, ConfigProvider configProvider) throws IllegalArgumentException {
        try {
            HttpRestBinding binding = (HttpRestBinding) providedInterface.getBindings().getFirst();
            String interfaceVersion = providedInterface.getInterfaceVersion();
            URI uri = new URI(binding.getServiceUrl());
            if (interfaceVersion != null && interfaceVersion.startsWith("1.")) {
                return new GazelleValidationServiceHandler(new ValidationServiceWrapper(new net.ihe.gazelle.validation.v1.client.ValidationServiceHttpClient(uri)));
            } else {
                return new GazelleValidationServiceHandler(new net.ihe.gazelle.validation.v2.client.ValidationServiceHttpClient(uri));
            }
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Service " + providedInterface.getInterfaceName() + " has no bindings");
        } catch (URISyntaxException e) {
            throw new ServiceClientException("Can't initiate validation handler due to exception while creating client from server url", e);
        }
    }

    @Override
    public ConsumedInterface getConsumedInterface() {
        return new ConsumedInterfaceBuilder()
                .setInterfaceName(ValidationServiceController.INTERFACE_NAME)
                .setRequired(true)
                .setSupportedBindings(List.of(HttpRestBinding.TYPE))
                .setSupportedVersions(List.of("1.0.0", "2.0.0"))
                .build();
    }
}

