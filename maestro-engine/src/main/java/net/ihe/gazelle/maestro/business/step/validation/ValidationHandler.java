package net.ihe.gazelle.maestro.business.step.validation;

import net.ihe.gazelle.maestro.runner.Handler;
import net.ihe.gazelle.validation.api.application.ValidationService;
import net.ihe.gazelle.validation.api.domain.metadata.structure.ValidationProfile;
import net.ihe.gazelle.validation.api.domain.report.structure.ValidationReport;
import net.ihe.gazelle.validation.api.domain.request.structure.ValidationRequest;
import net.ihe.gazelle.validation.client.ValidationServiceHttpClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class ValidationHandler implements Handler, ValidationService {

    private ValidationServiceHttpClient validationServiceHttpClient;

    protected ValidationHandler(){
        /*Empty constructor used for mocking*/
    }

    public ValidationHandler(String serverUrl){
        try {
            if(serverUrl == null || serverUrl.isBlank() || serverUrl.isEmpty()) {
                throw new IllegalArgumentException("serverUrl cannot be null, blank or empty");
            }
            URI uri = new URI(serverUrl);
            this.validationServiceHttpClient = new ValidationServiceHttpClient(uri);
        } catch (URISyntaxException e) {
            throw new ServiceClientException("Can't initiate validation handler due to exception while creating client from server url " + serverUrl, e);
        }
    }

    /**
     * Override from Handler Interface
     * @see Handler
     */
    @Override
    public boolean isAvailable() {
        return validationServiceHttpClient.getValidationProfiles() != null;
    }

    /**
     * Override from ValidationService
     * @see ValidationService
     */
    @Override
    public ValidationReport validate(ValidationRequest validationRequest) {
        if(validationServiceHttpClient != null) {
            return validationServiceHttpClient.validate(validationRequest);
        } else {
            throw new ServiceClientException("validationServiceHttpClient not set");
        }
    }

    /**
     * Override from ValidationService
     * @see ValidationService
     */
    @Override
    public List<ValidationProfile> getValidationProfiles() {
        if(validationServiceHttpClient != null) {
            return validationServiceHttpClient.getValidationProfiles();
        } else {
            throw new ServiceClientException("validationServiceHttpClient not set");
        }
    }

}
