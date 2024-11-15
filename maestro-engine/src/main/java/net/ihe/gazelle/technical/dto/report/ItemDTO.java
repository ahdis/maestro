package net.ihe.gazelle.technical.dto.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import net.ihe.gazelle.report.model.Item;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"name", "content", "reference"})
public record ItemDTO(Item domain) {

    @JsonProperty(value = "name")
    public String getName() {
        return domain.getName();
    }

    @JsonProperty(value = "content")
    public byte[] getContent() {
        return domain.getContent();
    }

    @JsonProperty(value = "reference")
    public String getReference() {
        return domain.getReference();
    }

    @JsonIgnore
    public boolean isNameValid() {
        return domain.isNameValid();
    }

    @JsonIgnore
    public boolean isContentValid() {
        return domain.isContentValid();
    }

    @JsonIgnore
    public boolean isReferenceValid() {
        return domain.isReferenceValid();
    }

    @JsonIgnore
    public boolean isValid() {
        return domain.isValid();
    }
}
