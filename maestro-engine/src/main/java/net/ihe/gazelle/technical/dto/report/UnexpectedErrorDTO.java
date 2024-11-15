package net.ihe.gazelle.technical.dto.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import net.ihe.gazelle.report.model.UnexpectedError;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"name", "message", "cause"})
public record UnexpectedErrorDTO(UnexpectedError domain) {

    @JsonProperty(value = "name")
    public String getName() {
        return domain.getName();
    }

    @JsonProperty(value = "message")
    public String getMessage() {
        return domain.getMessage();
    }

    @JsonProperty(value = "cause")
    public UnexpectedErrorDTO getCause() {
        if (domain.getCause() != null) {
            return new UnexpectedErrorDTO(domain.getCause());
        }
        return null;
    }

    @JsonIgnore
    public boolean isNameValid() {
        return domain.isNameValid();
    }

    @JsonIgnore
    public boolean isMessageValid() {
        return domain.isMessageValid();
    }

    @JsonIgnore
    public boolean isValid() {
        return domain.isValid();
    }
}
