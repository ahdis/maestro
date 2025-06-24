package net.ihe.gazelle.maestro.technical.step.validation;

import net.ihe.gazelle.maestro.api.business.testrun.runner.Handler;
import net.ihe.gazelle.maestro.business.step.validation.ServiceClientException;
import net.ihe.gazelle.maestro.business.step.validation.ValidationHandler;
import net.ihe.gazelle.validation.v2.api.business.ValidationService;
import net.ihe.gazelle.validation.v2.api.business.profile.ValidationProfile;
import net.ihe.gazelle.validation.v2.api.business.report.ValidationReport;
import net.ihe.gazelle.validation.v2.api.business.request.ValidationRequest;

import java.util.List;

public class GazelleValidationServiceHandler implements ValidationHandler {

    private ValidationService validationService;

    protected GazelleValidationServiceHandler() {
        /*Empty constructor used for mocking*/
    }

    public GazelleValidationServiceHandler(ValidationService validationService) {
        this.validationService = validationService;
    }

    /**
     * Override from {@link Handler} Interface
     */
    @Override
    public boolean isAvailable() {
        return validationService.getValidationProfiles() != null;
    }

    @Override
    public ValidationReport validate(ValidationRequest validationRequest) {
        if (validationService != null) {
            return validationService.validate(validationRequest);
        } else {
            throw new ServiceClientException("validationService not set");
        }
    }

    @Override
    public List<ValidationProfile> getValidationProfiles() {
        if (validationService != null) {
            return validationService.getValidationProfiles();
        } else {
            throw new ServiceClientException("validationService not set");
        }
    }
}
