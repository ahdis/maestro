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
