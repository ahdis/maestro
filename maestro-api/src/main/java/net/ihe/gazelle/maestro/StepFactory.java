package net.ihe.gazelle.maestro;

import java.util.Map;

public interface StepFactory {

    /**
     * @param name step's name
     * @param properties a map containing properties with property's name as key
     * @return a Step with attribute name and all its properties set corresponding to properties
     * @throws ClassCastException if given properties does not have expected types
     */
    Step create(String name, Map<String, Property<?>> properties);

    /**
     * @return a string of provided step type
     */
    String getSupportedStep();
}
