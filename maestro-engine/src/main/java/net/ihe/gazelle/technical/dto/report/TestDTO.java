package net.ihe.gazelle.technical.dto.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import net.ihe.gazelle.report.model.Test;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "name", "name", "description"})
public record TestDTO(Test domain) {

    @JsonProperty(value = "id")
    public String getId() {
        return domain.getId();
    }

    @JsonProperty(value = "name")
    public String getName() {
        return domain.getName();
    }

    @JsonProperty(value = "version")
    public String getVersion() {
        return domain.getVersion();
    }

    @JsonProperty(value = "description")
    public String getDescription() {
        return domain.getDescription();
    }

    @JsonIgnore
    public boolean isIdValid() {
        return domain.isIdValid();
    }

    @JsonIgnore
    public boolean isNameValid() {
        return domain.isNameValid();
    }

    @JsonIgnore
    public boolean isVersionValid() {
        return domain.isVersionValid();
    }

    @JsonIgnore
    public boolean isDescriptionValid() {
        return domain.isDescriptionValid();
    }

    @JsonIgnore
    public boolean isValid() {
        return domain.isValid();
    }
}
