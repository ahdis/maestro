package net.ihe.gazelle;

import net.ihe.gazelle.business.step.validation.ValidationStep;
import net.ihe.gazelle.business.test.TestImpl;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class TestImplTest {
    @Test
    void addStep(){
        TestImpl test = new TestImpl();
        Map<String, String> d = new HashMap<>();
        d.put("b","c");
        ValidationStep step = new ValidationStep("a",d);
        test.addStep(step);
        org.junit.jupiter.api.Assertions.assertEquals(step, test.getSteps().getFirst());
    }
}
