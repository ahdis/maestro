package net.ihe.gazelle.maestro.business.step.itb;

import net.ihe.gazelle.maestro.Property;
import net.ihe.gazelle.maestro.Step;
import net.ihe.gazelle.maestro.StepFactory;

import java.util.Map;

public class ItbStepFactory implements StepFactory {

    /**
     * @param name
     * @param properties
     * @return
     * @throws ClassCastException if given properties does not have expected types
     */
    @Override
    @SuppressWarnings("unchecked")
    public Step create(String name, Map<String, Property<?>> properties) {
        return new ItbStep(
                name,
                (Property<String>) properties.get(ItbStep.TEST_CASE),
                (Property<String>) properties.get(ItbStep.SYSTEM_ID),
                (Property<String>) properties.get(ItbStep.ACTOR_ID),
                (Property<byte[]>) properties.get(ItbStep.FIRST_REQUEST_HEADER),
                (Property<byte[]>) properties.get(ItbStep.FIRST_REQUEST_PAYLOAD),
                (Property<byte[]>) properties.get(ItbStep.FIRST_RESPONSE_HEADER),
                (Property<byte[]>) properties.get(ItbStep.FIRST_RESPONSE_PAYLOAD)
        );
    }

    @Override
    public String getSupportedStep() {
        return ItbStep.TYPE;
    }
}
