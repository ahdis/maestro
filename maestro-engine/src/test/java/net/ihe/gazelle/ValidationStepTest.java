package net.ihe.gazelle;

import net.ihe.gazelle.business.step.validation.ValidationStep;
import org.junit.jupiter.api.Test;

import java.util.*;

class ValidationStepTest {

    @Test
    void getProperties(){
        Map<String, String> properties = new HashMap<>();
        properties.put("validationService", "matchbox");
        properties.put("validationProfile", "Parameters");
        properties.put("contentToValidate", "saucisse");
        ValidationStep validationStep = new ValidationStep("dummyStep", properties);
        org.junit.jupiter.api.Assertions.assertEquals(properties, validationStep.getProperties());
    }

    @Test
    void getNullProperties(){
        ValidationStep validationStep = new ValidationStep();
        org.junit.jupiter.api.Assertions.assertEquals(new HashMap<>(), validationStep.getProperties());
    }

    @Test
    void setName(){
        ValidationStep validationStep = new ValidationStep();
        validationStep.setName("dummyTest");
        org.junit.jupiter.api.Assertions.assertEquals("dummyTest", validationStep.getName());
    }

    @Test
    void getMandatoryKeys() {
        ValidationStep validationStep = new ValidationStep();
        List<String> keys = validationStep.getMandatoryKeys();
        List<String> expected = new ArrayList<>();
        expected.add("validationService");
        expected.add("validationProfile");
        expected.add("contentToValidate");

        org.junit.jupiter.api.Assertions.assertEquals(new HashSet<>(keys), new HashSet<>(expected));

    }
}
