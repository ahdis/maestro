package net.ihe.gazelle.maestro.technical.dto.report;

import com.fasterxml.jackson.annotation.*;
import net.ihe.gazelle.maestro.api.business.testreport.TestService;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"serviceIdentification", "disclaimer"})
public class TestServiceDTO {

    @JsonIgnore
    private final TestService domain;

    public TestServiceDTO() {
        domain = new TestService();
    }

    public TestServiceDTO(TestService domain) {
        this.domain = domain;
    }

    @JsonGetter(value = "serviceIdentification")
    public EntityIdentificationDTO getServiceIdentification() {
        return new EntityIdentificationDTO(domain.getServiceIdentification());
    }

    @JsonSetter(value = "serviceIdentification")
    public void setServiceIdentification(EntityIdentificationDTO entityIdentificationDTO) {
        domain.setServiceIdentification(entityIdentificationDTO.getDomain());
    }

    @JsonGetter(value = "disclaimer")
    public String getDisclaimer() {
        return domain.getDisclaimer();
    }

    @JsonGetter(value = "disclaimer")
    public void setDisclaimer(String disclaimer) {
        domain.setDisclaimer(disclaimer);
    }

    @JsonIgnore
    public TestService getDomain() {
        return domain;
    }
}
