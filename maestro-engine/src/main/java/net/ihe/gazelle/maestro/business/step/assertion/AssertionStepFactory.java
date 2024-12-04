package net.ihe.gazelle.maestro.business.step.assertion;

import net.ihe.gazelle.maestro.Property;
import net.ihe.gazelle.maestro.Step;
import net.ihe.gazelle.maestro.StepFactory;

import java.util.Map;

/**
 * Assertion Step is not implemented. It exists just for test purpose
 */
public class AssertionStepFactory implements StepFactory {

    /**
     *
     * @param name
     * @param properties
     * @return
     * @throws ClassCastException if given properties does not have expected types
     */
    @Override
    @SuppressWarnings("unchecked")
    public Step create(String name, Map<String, Property<?>> properties) {
        return new AssertionStep(
                name,
                (Property<byte[]>) properties.get(AssertionStep.ASSERTION),
                (Property<String>) properties.get(AssertionStep.ASSERTION_SERVICE)
        );
    }

    @Override
    public String getSupportedStep() {
        return AssertionStep.TYPE;
    }
}
