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

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import net.ihe.gazelle.maestro.api.business.testreport.SystemUnderTest;
import net.ihe.gazelle.modelmarshaller.technical.dto.DTO;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "systemIdentification",
        "macAddresses",
        "ipAddresses",
        "hostNames"
})
public class SystemUnderTestDTO implements DTO<SystemUnderTest> {

    @JsonIgnore
    private final SystemUnderTest systemUnderTest;

    public SystemUnderTestDTO() {
        this(new SystemUnderTest());
    }

    public SystemUnderTestDTO(SystemUnderTest systemUnderTest) {
        this.systemUnderTest = systemUnderTest;
    }

    @Override
    @JsonIgnore
    public SystemUnderTest getBusinessObject() {
        return systemUnderTest;
    }

    @JsonGetter(value = "systemIdentification")
    public EntityIdentificationDTO getSystemIdentification() {
        return new EntityIdentificationDTO(getBusinessObject().getSystemIdentification());
    }

    @JsonSetter(value = "systemIdentification")
    public void setSystemIdentification(EntityIdentificationDTO entityIdentificationDTO) {
        getBusinessObject().setSystemIdentification(entityIdentificationDTO.getBusinessObject());
    }

    @JsonGetter(value = "macAddresses")
    public List<String> getMacAddresses() {
        return getBusinessObject().getMacAddresses();
    }

    @JsonSetter(value = "macAddresses")
    public void setMacAddresses(List<String> macAddresses) {
        getBusinessObject().setMacAddresses(macAddresses);
    }

    @JsonGetter(value = "ipAddresses")
    public List<String> getIpAddresses() {
        return getBusinessObject().getIpAddresses();
    }

    @JsonSetter(value = "ipAddresses")
    public void setIpAddresses(List<String> ipAddresses) {
        getBusinessObject().setIpAddresses(ipAddresses);
    }

    @JsonGetter(value = "hostNames")
    public List<String> getHostNames() {
        return getBusinessObject().getHostNames();
    }

    @JsonSetter(value = "hostNames")
    public void setHostNames(List<String> hostNames) {
        getBusinessObject().setHostNames(hostNames);
    }
}
