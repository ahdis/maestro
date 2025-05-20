package net.ihe.gazelle.maestro.business.step.assertion;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class AssertionStepTest {

    @Test
    void setPropertiesEmptyField(){
        AssertionStep step = new AssertionStep("test", null, null);
        assertThrows(IllegalArgumentException.class, () -> step.setProperties(null));
    }

}
