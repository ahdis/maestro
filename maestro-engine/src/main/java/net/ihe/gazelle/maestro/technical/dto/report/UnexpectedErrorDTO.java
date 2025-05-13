package net.ihe.gazelle.maestro.technical.dto.report;

import com.fasterxml.jackson.annotation.*;
import net.ihe.gazelle.lang.UnexpectedError;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"name", "message", "cause"})
public class UnexpectedErrorDTO {

    @JsonIgnore
    private final UnexpectedError domain;

    public UnexpectedErrorDTO() {
        domain = new UnexpectedError();
    }

    public UnexpectedErrorDTO(UnexpectedError domain) {
        this.domain = domain;
    }

    @JsonGetter(value = "name")
    public String getName() {
        return domain.getName();
    }

    @JsonSetter(value = "name")
    public void setName(String name) {
        domain.setName(name);
    }

    @JsonGetter(value = "message")
    public String getMessage() {
        return domain.getMessage();
    }

    @JsonSetter(value = "message")
    public void setMessage(String message) {
        domain.setMessage(message);
    }

    @JsonGetter(value = "cause")
    public UnexpectedErrorDTO getCause() {
        if (domain.getCause() != null) {
            return new UnexpectedErrorDTO(domain.getCause());
        }
        return null;
    }

    @JsonSetter(value = "cause")
    public void setCause(UnexpectedErrorDTO cause) {
        if (cause != null) {
            domain.setCause(cause.getDomain());
        }
    }

    @JsonIgnore
    public UnexpectedError getDomain() {
        return domain;
    }
}
