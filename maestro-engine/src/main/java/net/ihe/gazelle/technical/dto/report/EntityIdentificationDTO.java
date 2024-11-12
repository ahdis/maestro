package net.ihe.gazelle.technical.dto.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import net.ihe.gazelle.report.model.EntityIdentification;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"version", "name"})
public record EntityIdentificationDTO(EntityIdentification domain) {

    @JsonProperty(value = "version")
    public String getVersion() {
        return domain.getVersion();
    }

    @JsonProperty(value = "name")
    public String getName() {
        return domain.getName();
    }

    @JsonIgnore
    public boolean isVersionValid() {
        return domain.isVersionValid();
    }

    @JsonIgnore
    public boolean isNameValid() {
        return domain.isNameValid();
    }

    @JsonIgnore
    public boolean isValid() {
        return domain.isValid();
    }
}
