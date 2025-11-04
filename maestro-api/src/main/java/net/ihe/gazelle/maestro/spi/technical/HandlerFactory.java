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

package net.ihe.gazelle.maestro.spi.technical;

import net.ihe.gazelle.maestro.spi.business.Handler;
import net.ihe.gazelle.servicemetadata.api.business.ConsumedInterface;
import net.ihe.gazelle.servicemetadata.api.business.ProvidedInterface;

import java.util.List;

public interface HandlerFactory {

    /**
     * @return Handler Implementation class
     */
    Class<? extends Handler> getHandlerType();

    /**
     *
     * @param serviceName
     * @param providedInterface The provided interface used by the handler
     *
     * @return A Handler that can provide interaction with service
     *
     * @throws IllegalArgumentException if service has no binding set
     */
    Handler createHandler(String serviceName, ProvidedInterface providedInterface, ConfigProvider configProvider) throws IllegalArgumentException;

    /**
     * Get the list of consumed interfaces by this handler factory.
     * @return a list of consumed interfaces
     */
    List<ConsumedInterface> getConsumedInterfaces();

}
