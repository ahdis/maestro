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

package net.ihe.gazelle.maestro.technical;

import net.ihe.gazelle.servicemetadata.api.business.Service;

import java.util.List;

public interface ServiceRegistry {

    /**
     * @return List of services from Database
     */
    List<Service> getServices();

    /**
     * @param serviceName Name of the service 
     * @return Service with name corresponding to serviceName
     * @throws IllegalArgumentException if there is no Service with corresponding name in DB
     */
    Service getService(String serviceName);
}
