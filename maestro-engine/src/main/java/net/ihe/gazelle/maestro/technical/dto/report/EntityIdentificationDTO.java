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
