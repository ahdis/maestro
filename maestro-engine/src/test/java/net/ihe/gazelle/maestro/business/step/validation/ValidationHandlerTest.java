package net.ihe.gazelle.maestro.business.step.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidationHandlerTest {

    @Test
    void initWithNullParams() {

        assertThrows(IllegalArgumentException.class,() -> new ValidationHandler(null));

    }

    @Test
    void initWithEmptyParams() {

        assertThrows(IllegalArgumentException.class,() -> new ValidationHandler(""));

    }

    @Test
    void initWithBlankParams() {
        assertThrows(IllegalArgumentException.class,() -> new ValidationHandler(" "));
    }

    @Test
    void initWithInvalidParams() {
        assertThrows(ServiceClientException.class,() -> new ValidationHandler("h t t p : //"));
    }

    @Test
    void validateWithClientNotSet(){
        ValidationHandler validationHandler = new ValidationHandler();
        assertThrows(ServiceClientException.class,
                () -> validationHandler.validate(null));
    }
}
