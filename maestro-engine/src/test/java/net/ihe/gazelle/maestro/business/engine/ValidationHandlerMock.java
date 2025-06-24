package net.ihe.gazelle.maestro.business.engine;

import net.ihe.gazelle.maestro.technical.step.validation.GazelleValidationServiceHandler;
import net.ihe.gazelle.validation.v2.api.business.profile.ValidationProfile;
import net.ihe.gazelle.validation.v2.api.business.report.ValidationReport;
import net.ihe.gazelle.validation.v2.api.business.request.ValidationRequest;

import java.util.List;

public class ValidationHandlerMock extends GazelleValidationServiceHandler {


    public ValidationHandlerMock() {
        /*Empty constructor used for mocking*/
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public ValidationReport validate(ValidationRequest validationRequest) {
        return new ValidationReport();
    }

    @Override
    public List<ValidationProfile> getValidationProfiles() {
        return List.of();
    }
}
