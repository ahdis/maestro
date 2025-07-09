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
import net.ihe.gazelle.maestro.api.business.testreport.SystemUnderTest;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"systemIdentification", "macAddresses", "ipAddresses", "hostNames"})
public class SystemUnderTestDTO {

    @JsonIgnore
    private final SystemUnderTest domain;

    public SystemUnderTestDTO() {
        this.domain = new SystemUnderTest();
    }

    public SystemUnderTestDTO(SystemUnderTest domain) {
        this.domain = domain;
    }

    @JsonGetter(value = "systemIdentification")
    public EntityIdentificationDTO getSystemIdentification() {
        return new EntityIdentificationDTO(domain.getSystemIdentification());
    }

    @JsonSetter(value = "systemIdentification")
    public void setSystemIdentification(EntityIdentificationDTO entityIdentificationDTO) {
        domain.setSystemIdentification(entityIdentificationDTO.getDomain());
    }

    @JsonGetter(value = "macAddresses")
    public List<String> getMacAddresses() {
        return domain.getMacAddresses();
    }

    @JsonSetter(value = "macAddresses")
    public void setMacAddresses(List<String> macAddresses) {
        domain.setMacAddresses(macAddresses);
    }

    @JsonGetter(value = "ipAddresses")
    public List<String> getIpAddresses() {
        return domain.getIpAddresses();
    }

    @JsonSetter(value = "ipAddresses")
    public void setIpAddresses(List<String> ipAddresses) {
        domain.setIpAddresses(ipAddresses);
    }

    @JsonGetter(value = "hostNames")
    public List<String> getHostNames() {
        return domain.getHostNames();
    }

    @JsonSetter(value = "hostNames")
    public void setHostNames(List<String> hostNames) {
        domain.setHostNames(hostNames);
    }

    @JsonIgnore
    public SystemUnderTest getDomain() {
        return domain;
    }
}
