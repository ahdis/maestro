package net.ihe.gazelle.maestro.technical;

import net.ihe.gazelle.maestro.api.business.testrun.DuplicatedStepsException;
import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.Step;
import net.ihe.gazelle.maestro.api.business.testrun.StepFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class StepFactoryProvider {

    private static final Map<String, StepFactory> FACTORIES = new HashMap<>();

    public Step create(String type, String stepName, Map<String, Property<?>> properties) {
        StepFactory factory = getFactories().get(type);
        if (factory != null) {
            return factory.create(stepName, properties);
        } else {
            throw new NotProvidedStep("Step type " + type + " not known");
        }
    }

    /**
     *
     * @return map of provided StepFactories with supported step type as key
     * @throws DuplicatedStepsException if two factories provide the same step type
     */
    private Map<String, StepFactory> getFactories() {
        if (FACTORIES.isEmpty()) {
            ServiceLoader<StepFactory> stepFactories = ServiceLoader.load(StepFactory.class);
            for (StepFactory factory : stepFactories) {
                StepFactory previousFactory = FACTORIES.put(factory.getSupportedStep(), factory);
                if (previousFactory != null) {
                    throw new DuplicatedStepsException("Two factories provided for the same test type " + previousFactory.getSupportedStep());
                }
            }
        }
        return FACTORIES;
    }
}
