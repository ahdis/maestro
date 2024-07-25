package net.ihe.gazelle;

import net.ihe.gazelle.business.step.validation.ValidationStep;
import net.ihe.gazelle.business.test.TestImpl;
import org.junit.jupiter.api.Test;

class TestImplTest {
    @Test
    void addStep(){
        TestImpl test = new TestImpl();
        ValidationStep step = new ValidationStep("a","b","c","d");
        test.addStep(step);
        org.junit.jupiter.api.Assertions.assertEquals(step, test.getSteps().getFirst());
    }
}
