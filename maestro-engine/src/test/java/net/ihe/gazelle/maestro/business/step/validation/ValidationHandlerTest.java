package net.ihe.gazelle.maestro.business.step.validation;

import net.ihe.gazelle.validation.v2.api.business.request.ValidationRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidationHandlerTest {

    @Test
    void initWithNullParams() {
        assertThrows(IllegalArgumentException.class,() -> new ValidationHandler(null,"2.0.0"));
    }

    @Test
    void initWithEmptyParams() {
        assertThrows(IllegalArgumentException.class,() -> new ValidationHandler("","2.0.0"));
    }

    @Test
    void initWithBlankParams() {
        assertThrows(IllegalArgumentException.class,() -> new ValidationHandler(" ","2.0.0"));
    }

    @Test
    void initWithInvalidParams() {
        assertThrows(ServiceClientException.class,() -> new ValidationHandler("h t t p : //","2.0.0"));
    }

    @Test
    void validateWithClientNotSet(){
        ValidationHandler validationHandler = new ValidationHandler();
        assertThrows(ServiceClientException.class,
                () -> validationHandler.validate(new ValidationRequest()));
    }
}
