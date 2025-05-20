package net.ihe.gazelle.maestro.technical.dto.report;

import com.fasterxml.jackson.annotation.*;
import net.ihe.gazelle.maestro.api.business.testreport.Test;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "name", "name", "description"})
public class TestDTO {

    @JsonIgnore
    private final Test domain;

    public TestDTO() {
        domain = new Test();
    }

    public TestDTO(Test domain) {
        this.domain = domain;
    }

    @JsonGetter(value = "id")
    public String getId() {
        return domain.getId();
    }

    @JsonSetter(value = "id")
    public void setId(String id) {
        domain.setId(id);
    }

    @JsonGetter(value = "name")
    public String getName() {
        return domain.getName();
    }

    @JsonSetter(value = "name")
    public void setName(String name) {
        domain.setName(name);
    }

    @JsonGetter(value = "version")
    public String getVersion() {
        return domain.getVersion();
    }

    @JsonSetter(value = "version")
    public void setVersion(String version) {
        domain.setVersion(version);
    }

    @JsonGetter(value = "description")
    public String getDescription() {
        return domain.getDescription();
    }

    @JsonSetter(value = "description")
    public void setDescription(String description) {
        domain.setDescription(description);
    }

    @JsonIgnore
    public Test getDomain() {
        return domain;
    }
}
