package net.ihe.gazelle.technical.dto.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import net.ihe.gazelle.report.model.SystemUnderTest;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"systemIdentification", "macAddresses", "ipAddresses", "hostNames"})
public record SystemUnderTestDTO(SystemUnderTest domain) {

    @JsonProperty(value = "systemIdentification")
    public EntityIdentificationDTO getSystemIdentification() {
        return new EntityIdentificationDTO(domain.getSystemIdentification());
    }

    @JsonProperty(value = "macAddresses")
    public List<String> getMacAddresses() {
        return domain.getMacAddresses();
    }

    @JsonProperty(value = "ipAddresses")
    public List<String> getIpAddresses() {
        return domain.getIpAddresses();
    }

    @JsonProperty(value = "hostNames")
    public List<String> getHostNames() {
        return domain.getHostNames();
    }

    @JsonIgnore
    public boolean isSystemIdentificationValid() {
        return domain.isSystemIdentificationValid();
    }

    @JsonIgnore
    public boolean isValid() {
        return domain.isValid();
    }
}
