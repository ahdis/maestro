package net.ihe.gazelle;

import net.ihe.gazelle.business.engine.marshaller.StepMarshallerProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StepMarshallerProviderTest {
    @Test
    void ValidationStep(){
        StepMarshallerProvider stepMarshallerProvider = new StepMarshallerProvider();
        String typeExpected = "net.ihe.gazelle.business.step.validation.ValidationStep";
        String type = stepMarshallerProvider.getMarshaller(typeExpected).getSupportedStep();
        assertEquals(typeExpected, type);
    }
    @Test
    void unsupportedStep(){
        StepMarshallerProvider stepMarshallerProvider = new StepMarshallerProvider();
        String typeExpected = "saucisse";
        RuntimeException exception = assertThrows(RuntimeException.class, () -> stepMarshallerProvider.getMarshaller(typeExpected));
        assertEquals("No marshaller found for type saucisse", exception.getMessage());
    }
}
