package net.ihe.gazelle.maestro.technical.step.validation;

import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.Step;
import net.ihe.gazelle.maestro.business.step.assertion.AssertionStep;
import net.ihe.gazelle.maestro.business.step.validation.ValidationStep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationStepRunnerFactoryTest {

    ValidationStepRunnerFactory validationStepRunnerFactory;

    @BeforeEach
    void setUp() {
        validationStepRunnerFactory = new ValidationStepRunnerFactory();
    }

    @Test
    void canonicalNameTest(){
        assertEquals(ValidationStep.class.getCanonicalName(), validationStepRunnerFactory.getSupportedStep());
    }

    @Test
    void getRequiredServicesOfAssertionStep() {
        Step assertionStep = new AssertionStep(
                "saucisse",
                new Property<>(AssertionStep.ASSERTION, "equals(puré)".getBytes()),
                new Property<>(AssertionStep.ASSERTION_SERVICE, "cuisine")
                );
        assertTrue(validationStepRunnerFactory.getRequiredServices(assertionStep).isEmpty());
    }

}
