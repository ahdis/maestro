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
import net.ihe.gazelle.maestro.api.business.testreport.TestService;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"serviceIdentification", "disclaimer"})
public class TestServiceDTO {

    @JsonIgnore
    private final TestService domain;

    public TestServiceDTO() {
        domain = new TestService();
    }

    public TestServiceDTO(TestService domain) {
        this.domain = domain;
    }

    @JsonGetter(value = "serviceIdentification")
    public EntityIdentificationDTO getServiceIdentification() {
        return new EntityIdentificationDTO(domain.getServiceIdentification());
    }

    @JsonSetter(value = "serviceIdentification")
    public void setServiceIdentification(EntityIdentificationDTO entityIdentificationDTO) {
        domain.setServiceIdentification(entityIdentificationDTO.getDomain());
    }

    @JsonGetter(value = "disclaimer")
    public String getDisclaimer() {
        return domain.getDisclaimer();
    }

    @JsonGetter(value = "disclaimer")
    public void setDisclaimer(String disclaimer) {
        domain.setDisclaimer(disclaimer);
    }

    @JsonIgnore
    public TestService getDomain() {
        return domain;
    }
}
