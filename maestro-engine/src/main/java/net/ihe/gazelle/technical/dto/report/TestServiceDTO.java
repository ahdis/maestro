package net.ihe.gazelle.technical.dto.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import net.ihe.gazelle.report.model.TestService;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"serviceIdentification", "disclaimer"})
public record TestServiceDTO(TestService domain) {

    @JsonProperty(value = "serviceIdentification")
    public EntityIdentificationDTO getServiceIdentification() {
        return new EntityIdentificationDTO(domain.getServiceIdentification());
    }

    @JsonProperty(value = "disclaimer")
    public String getDisclaimer() {
        return domain.getDisclaimer();
    }

    @JsonIgnore
    public boolean isServiceIdentificationValid() {
        return domain.isServiceIdentificationValid();
    }

    @JsonIgnore
    public boolean isServiceVersionValid() {
        return domain.isServiceVersionValid();
    }

    @JsonIgnore
    public boolean isDisclaimerValid() {
        return domain.isDisclaimerValid();
    }

    @JsonIgnore
    public boolean isValid() {
        return domain.isValid();
    }
}
