package net.ihe.gazelle.maestro.business.step.validation;

import net.ihe.gazelle.maestro.runner.Handler;
import net.ihe.gazelle.validation.v2.api.business.ValidationService;
import net.ihe.gazelle.validation.v2.api.business.profile.ValidationProfile;
import net.ihe.gazelle.validation.v2.api.business.report.ValidationReport;
import net.ihe.gazelle.validation.v2.api.business.request.ValidationRequest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

public class ValidationHandler implements Handler, ValidationService {

    private ValidationService validationServiceHttpClient;

    protected ValidationHandler() {
        /*Empty constructor used for mocking*/
    }

    public ValidationHandler(String serverUrl, String interfaceVersion) {
        try {
            if (serverUrl == null || serverUrl.isBlank() || serverUrl.isEmpty()) {
                throw new IllegalArgumentException("serverUrl cannot be null, blank or empty");
            }
            URI uri = new URI(serverUrl);
            if (interfaceVersion != null && interfaceVersion.startsWith("1."))
                this.validationServiceHttpClient = new net.ihe.gazelle.validation.v1.client.ValidationServiceHttpClient(uri);
            else
                this.validationServiceHttpClient = new net.ihe.gazelle.validation.v2.client.ValidationServiceHttpClient(uri);
        } catch (URISyntaxException e) {
            throw new ServiceClientException("Can't initiate validation handler due to exception while creating client from server url " + serverUrl, e);
        }
    }

    /**
     * Override from {@link Handler} Interface
     */
    @Override
    public boolean isAvailable() {
        return validationServiceHttpClient.getValidationProfiles() != null;
    }

    /**
     * Override from {@link ValidationService}
     */
    @Override
    public ValidationReport validate(ValidationRequest validationRequest) {
        if (validationServiceHttpClient != null) {
            return validationServiceHttpClient.validate(validationRequest);
        } else {
            throw new ServiceClientException("validationServiceHttpClient not set");
        }
    }

    /**
     * Override from {@link ValidationService}
     */
    @Override
    public List<ValidationProfile> getValidationProfiles() {
        if (validationServiceHttpClient != null) {
            return validationServiceHttpClient.getValidationProfiles();
        } else {
            throw new ServiceClientException("validationServiceHttpClient not set");
        }
    }

}
