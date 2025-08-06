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

import net.ihe.gazelle.evsapi.client.technical.ValidationClientHttpImpl;
import net.ihe.gazelle.maestro.api.business.testrun.runner.ConfigProvider;
import net.ihe.gazelle.maestro.api.business.testrun.runner.Handler;
import net.ihe.gazelle.maestro.api.business.testrun.runner.HandlerFactory;
import net.ihe.gazelle.maestro.validation.step.technical.handler.EvsValidationHandler;
import net.ihe.gazelle.servicemetadata.api.business.ConsumedInterface;
import net.ihe.gazelle.servicemetadata.api.business.ConsumedInterfaceBuilder;
import net.ihe.gazelle.servicemetadata.api.business.HttpRestBinding;
import net.ihe.gazelle.servicemetadata.api.business.ProvidedInterface;

import java.util.List;
import java.util.Objects;

/**
 * Factory for creating EVS ValidationHandler instances
 * Follows the same pattern as GazelleValidationServiceHandlerFactory
 */
public class EvsValidationServiceHandlerFactory implements HandlerFactory {

    @Override
    public Class<? extends Handler> getHandlerType() {
        return EvsValidationHandler.class;
    }

    @Override
    public Handler createHandler(ProvidedInterface providedInterface, ConfigProvider configProvider) throws IllegalArgumentException {
        try {
            String evsRestUrl = Objects.requireNonNull(org.eclipse.microprofile.config.ConfigProvider.getConfig().getValue("evs.service.rest.url", String.class));
            return new EvsValidationHandler(new ValidationClientHttpImpl(evsRestUrl));

        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to create EVS Validation Handler", e);
        }
    }

    @Override
    public List<ConsumedInterface> getConsumedInterfaces() {
        return List.of(
            new ConsumedInterfaceBuilder()
                    .setInterfaceName("EVS Client API")
                    .setRequired(false)
                    .setSupportedBindings(List.of(HttpRestBinding.TYPE))
                    .setSupportedVersions(List.of("1.0.0"))
                    .build()
        );
    }
}
