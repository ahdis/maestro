package net.ihe.gazelle.maestro.technical.step.simulation;

import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.Step;
import net.ihe.gazelle.maestro.business.step.assertion.AssertionStep;
import net.ihe.gazelle.maestro.business.step.simulation.SimulationStep;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimulationStepRunnerFactoryTest {

    private final SimulationStepRunnerFactory factory = new SimulationStepRunnerFactory();

    @Test
    void canonicalNameTest(){
        assertEquals(SimulationStep.class.getCanonicalName(), factory.getSupportedStep());
    }

    @Test
    void getRequiredServicesOfAssertionStep(){
        Step assertionStep = new AssertionStep(
                "saucisse",
                new Property<>(AssertionStep.ASSERTION, "equals(puré)".getBytes()),
                new Property<>(AssertionStep.ASSERTION_SERVICE, "cuisine")
        );
        assertEquals(1, factory.getRequiredServices(assertionStep).size());
    }
}
