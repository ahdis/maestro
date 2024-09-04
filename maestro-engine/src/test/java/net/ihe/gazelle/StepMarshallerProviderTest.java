package net.ihe.gazelle;

import net.ihe.gazelle.business.engine.marshaller.StepMarshallerProvider;
import org.junit.jupiter.api.Test;

public class StepMarshallerProviderTest {
    @Test
    void ValidationStep(){
        StepMarshallerProvider stepMarshallerProvider = new StepMarshallerProvider();
        String typeExpected = "net.ihe.gazelle.business.step.validation.ValidationStep";
        String type = stepMarshallerProvider.getMarshaller(typeExpected).getSupportedStep();
        org.junit.jupiter.api.Assertions.assertEquals(typeExpected, type);
    }
}
