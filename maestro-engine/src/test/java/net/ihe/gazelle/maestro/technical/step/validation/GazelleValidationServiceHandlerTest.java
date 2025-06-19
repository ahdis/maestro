package net.ihe.gazelle.maestro.technical.step.validation;

import net.ihe.gazelle.maestro.business.step.validation.ServiceClientException;
import net.ihe.gazelle.maestro.business.step.validation.ValidationHandler;
import net.ihe.gazelle.validation.v2.api.business.request.ValidationRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class GazelleValidationServiceHandlerTest {

    @Test
    void validateWithClientNotSet(){
        ValidationHandler validationHandler = new GazelleValidationServiceHandler();
        assertThrows(ServiceClientException.class,
                () -> validationHandler.validate(new ValidationRequest()));
    }
}
