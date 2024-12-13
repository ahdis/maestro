package net.ihe.gazelle;

import net.ihe.gazelle.maestro.business.step.validation.ValidationStep;
import net.ihe.gazelle.maestro.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestImplTest {
    @org.junit.jupiter.api.Test
    void addStep(){
        Test test = new Test();
        ValidationStep step = new ValidationStep("a",null, null, null);
        test.addStep(step);

        assertEquals(step, test.getSteps().getFirst());
    }
}
