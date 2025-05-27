package net.ihe.gazelle.maestro.business.step.itb;


import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.PropertyRef;
import net.ihe.gazelle.maestro.api.business.testrun.Step;
import net.ihe.gazelle.maestro.api.business.testrun.StepFactory;

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
        return new ItbStep(name,
                (Property<String>) properties.get(ItbStep.TEST_CASE),
                (Property<String>) properties.get(ItbStep.SYSTEM_ID),
                (Property<String>) properties.get(ItbStep.ACTOR_ID),
                properties.values().stream()
                        .filter(value -> value instanceof PropertyRef<?>)
                        .map(value -> (PropertyRef<byte[]>) value)
                        .toArray(PropertyRef[]::new)
        );
    }

    @Override
    public String getSupportedStep() {
        return ItbStep.TYPE;
    }
}
