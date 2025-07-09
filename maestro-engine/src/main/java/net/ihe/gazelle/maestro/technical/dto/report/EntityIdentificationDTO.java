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

package net.ihe.gazelle.maestro.technical.dto.report;

import com.fasterxml.jackson.annotation.*;
import net.ihe.gazelle.maestro.api.business.testreport.EntityIdentification;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"version", "name"})
public class EntityIdentificationDTO {

    @JsonIgnore
    private final EntityIdentification domain;

    public EntityIdentificationDTO() {
        domain = new EntityIdentification();
    }

    public EntityIdentificationDTO(EntityIdentification domain) {
        this.domain = domain;
    }

    @JsonGetter(value = "version")
    public String getVersion() {
        return domain.getVersion();
    }

    @JsonSetter(value = "version")
    public void setVersion(String version) {
        domain.setVersion(version);
    }

    @JsonGetter(value = "name")
    public String getName() {
        return domain.getName();
    }

    @JsonSetter(value = "name")
    public void setName(String name) {
        domain.setName(name);
    }

    @JsonIgnore
    public EntityIdentification getDomain() {
        return domain;
    }
}
