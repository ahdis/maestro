package net.ihe.gazelle.business.step.validation;

import net.ihe.gazelle.runner.Handler;
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
            URI uri = new URI(serverUrl);
            this.validationServiceHttpClient = new ValidationServiceHttpClient(uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isAvailable(String validationService){
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ValidationReport validate(ValidationRequest validationRequest) {
        if(validationServiceHttpClient != null) {
            return validationServiceHttpClient.validate(validationRequest);
        } else {
            throw new RuntimeException("validationServiceRestClient not set");
        }
    }

    @Override
    public List<ValidationProfile> getValidationProfiles() {
        if(validationServiceHttpClient != null) {
            return validationServiceHttpClient.getValidationProfiles();
        } else {
            throw new RuntimeException("validationServiceRestClient not set");
        }
    }

}
