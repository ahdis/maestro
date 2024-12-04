package net.ihe.gazelle.maestro.business.step.assertion;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AssertionHandlerTest {

    @Test
    void isAvaliableTest() {
        AssertionHandler handler = new AssertionHandler();
        assertTrue(handler.isAvailable());
    }

}
