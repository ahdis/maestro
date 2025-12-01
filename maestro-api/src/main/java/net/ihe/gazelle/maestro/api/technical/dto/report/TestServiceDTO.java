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

package net.ihe.gazelle.maestro.api.technical.dto.report;

import com.fasterxml.jackson.annotation.*;
import net.ihe.gazelle.maestro.api.business.testreport.TestService;
import net.ihe.gazelle.modelmarshaller.technical.dto.DTO;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "serviceIdentification",
        "disclaimer"
})
public class TestServiceDTO implements DTO<TestService> {

    @JsonIgnore
    private final TestService testService;

    public TestServiceDTO() {
        this(new TestService());
    }

    public TestServiceDTO(TestService testService) {
        this.testService = testService;
    }

    @Override
    @JsonIgnore
    public TestService getBusinessObject() {
        return testService;
    }

    @JsonGetter(value = "serviceIdentification")
    public EntityIdentificationDTO getServiceIdentification() {
        return new EntityIdentificationDTO(getBusinessObject().getServiceIdentification());
    }

    @JsonSetter(value = "serviceIdentification")
    public void setServiceIdentification(EntityIdentificationDTO entityIdentificationDTO) {
        getBusinessObject().setServiceIdentification(entityIdentificationDTO.getBusinessObject());
    }

    @JsonGetter(value = "disclaimer")
    public String getDisclaimer() {
        return getBusinessObject().getDisclaimer();
    }

    @JsonGetter(value = "disclaimer")
    public void setDisclaimer(String disclaimer) {
        getBusinessObject().setDisclaimer(disclaimer);
    }
}
