package net.ihe.gazelle.technical.dto.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import net.ihe.gazelle.report.model.TestCounters;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"total", "passed", "failed", "undefined", "unexpectedErrors"})
public record TestCountersDTO(TestCounters domain) {

    @JsonProperty(value = "total")
    public int getTotal() {
        return domain.getTotal();
    }

    @JsonProperty(value = "passed")
    public int getPassed() {
        return domain.getPassed();
    }

    @JsonProperty(value = "failed")
    public int getFailed() {
        return domain.getFailed();
    }

    @JsonProperty(value = "undefined")
    public int getUndefined() {
        return domain.getUndefined();
    }

    @JsonProperty(value = "unexpectedErrors")
    public int getUnexpectedErrors() {
        return domain.getUnexpectedErrors();
    }

    @JsonIgnore
    public boolean isTotalValid() {
        return domain.isTotalValid();
    }

    @JsonIgnore
    public boolean isPassedValid() {
        return domain.isPassedValid();
    }

    @JsonIgnore
    public boolean isFailedValid() {
        return domain.isFailedValid();
    }

    @JsonIgnore
    public boolean isUndefinedValid() {
        return domain.isUndefinedValid();
    }

    @JsonIgnore
    public boolean isUnexpectedErrorsValid() {
        return domain.isUnexpectedErrorsValid();
    }

    @JsonIgnore
    public boolean isValid() {
        return domain.isValid();
    }
}
