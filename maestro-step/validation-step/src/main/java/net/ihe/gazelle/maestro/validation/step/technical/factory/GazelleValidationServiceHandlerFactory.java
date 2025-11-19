/*
 * Copyright 2025 Kereval.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ihe.gazelle.maestro.validation.step.technical.factory;

import net.ihe.gazelle.maestro.spi.technical.ConfigProvider;
import net.ihe.gazelle.maestro.spi.business.Handler;
import net.ihe.gazelle.maestro.spi.technical.HandlerContext;
import net.ihe.gazelle.maestro.spi.technical.HandlerFactory;
import net.ihe.gazelle.maestro.validation.step.technical.handler.GazelleValidationServiceHandler;
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
   public Handler createHandler(HandlerContext handlerContext, ProvidedInterface providedInterface, ConfigProvider configProvider) throws IllegalArgumentException {
        if (providedInterface == null || providedInterface.getBindings() == null || providedInterface.getBindings().isEmpty()) {
            throw new IllegalArgumentException("Service " + (providedInterface != null ? providedInterface.getInterfaceName() : "Validation Service") + " has no bindings");
        }
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
            throw new IllegalArgumentException("Can't initiate validation handler due to exception while creating client from server url", e);
        }
    }

    @Override
    public List<ConsumedInterface> getConsumedInterfaces() {
        return List.of(
        new ConsumedInterfaceBuilder()
                .setInterfaceName("Validation Service")
                .setRequired(false)
                .setSupportedBindings(List.of(HttpRestBinding.TYPE))
                .setSupportedVersions(List.of("1.0.0"))
                .build(),
        new ConsumedInterfaceBuilder()
                .setInterfaceName(ValidationServiceController.INTERFACE_NAME)
                .setRequired(false)
                .setSupportedBindings(List.of(HttpRestBinding.TYPE))
                .setSupportedVersions(List.of("2.0.0"))
                .build());
    }
}
