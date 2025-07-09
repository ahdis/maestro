/*
 * Copyright 2025 IHE International.
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

package net.ihe.gazelle.maestro.technical.handler;

import net.ihe.gazelle.maestro.api.business.testrun.runner.Handler;

public interface HandlerProvider {

    /**
     * @param handlerType Class of Handler we are looking for
     * @param serviceName The name of service we want Handler to connect with
     * @return a Handler of type handlerType which connect with a service with name serviceName
     * @throws HandlerNotFoundException if it couldn't find a Handler of type handlerTyper
     */
    Handler getHandler(Class<? extends Handler> handlerType, String serviceName);


}
