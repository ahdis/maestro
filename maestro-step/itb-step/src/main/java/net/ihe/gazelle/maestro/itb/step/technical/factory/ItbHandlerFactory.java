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

package net.ihe.gazelle.maestro.itb.step.technical.factory;

import net.ihe.gazelle.maestro.itb.step.technical.handler.ItbHandlerImpl;
import net.ihe.gazelle.maestro.spi.business.runner.ConfigProvider;
import net.ihe.gazelle.maestro.spi.business.runner.Handler;
import net.ihe.gazelle.maestro.spi.business.runner.HandlerFactory;
import net.ihe.gazelle.servicemetadata.api.business.ConsumedInterface;
import net.ihe.gazelle.servicemetadata.api.business.ConsumedInterfaceBuilder;
import net.ihe.gazelle.servicemetadata.api.business.HttpRestBinding;
import net.ihe.gazelle.servicemetadata.api.business.ProvidedInterface;

import java.util.List;

public class ItbHandlerFactory implements HandlerFactory {

    @Override
    public Class<? extends Handler> getHandlerType() {
        return ItbHandlerImpl.class;
    }

    @Override
    public Handler createHandler(String serviceName, ProvidedInterface providedInterface, ConfigProvider configProvider) throws IllegalArgumentException {
        return new ItbHandlerImpl();
    }

    @Override
    public List<ConsumedInterface> getConsumedInterfaces() {
        return List.of(new ConsumedInterfaceBuilder()
                .setInterfaceName("ITB API")
                .setRequired(true)
                .setSupportedBindings(List.of(HttpRestBinding.TYPE))
                .setSupportedVersions(List.of("1.0.0"))
                .build());
    }
}

