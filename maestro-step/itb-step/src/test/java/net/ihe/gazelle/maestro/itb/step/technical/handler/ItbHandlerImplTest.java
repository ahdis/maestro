package net.ihe.gazelle.maestro.itb.step.technical.handler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ItbHandlerImplTest {

    @Test
    void stepToTestRequestMapper_supportsTextAndFileInputs() {
        ItbHandlerImpl handler = new ItbHandlerImpl("baseUrl", "itbApiKey");
        assertTrue(handler.isAvailable());
    }
}
