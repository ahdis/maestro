package net.ihe.gazelle.report.model;

import java.util.Objects;

/**
 * Business rules
 * all mandatory
 * serviceIdentification.version mandatory (version "unknown" is accepted)
 */
public class TestService {

    private EntityIdentification serviceIdentification;
    private String disclaimer;

    public TestService() {
    }

    public TestService(EntityIdentification serviceIdentification, String disclaimer) {
        setServiceIdentification(serviceIdentification);
        this.disclaimer = disclaimer;
    }

    public EntityIdentification getServiceIdentification() {
        return serviceIdentification;
    }

    public TestService setServiceIdentification(EntityIdentification serviceIdentification) {
        if (serviceIdentification.getVersion() == null) {
            serviceIdentification.setVersion("unknown");
        }
        this.serviceIdentification = serviceIdentification;
        return this;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public TestService setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
        return this;
    }

    public boolean isServiceIdentificationValid() {
        return serviceIdentification != null;
    }

    public boolean isServiceVersionValid() {
        return serviceIdentification.getVersion() != null && !serviceIdentification.getVersion().isBlank();
    }

    public boolean isDisclaimerValid() {
        return disclaimer != null && !disclaimer.isBlank();
    }

    public boolean isValid() {
        return isServiceIdentificationValid()
                && isServiceVersionValid()
                && isDisclaimerValid();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestService that)) return false;
        return Objects.equals(serviceIdentification, that.serviceIdentification)
                && Objects.equals(disclaimer, that.disclaimer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceIdentification, disclaimer);
    }
}
