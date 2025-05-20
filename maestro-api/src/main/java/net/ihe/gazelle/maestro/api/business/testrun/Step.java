package net.ihe.gazelle.maestro.api.business.testrun;

import java.util.List;

/**
 * Define an automated step to execute with all its required information.
 * A step contains has some properties representing its different required inputs.
 * {@link Property}
 */
public interface Step {

    String getName();

    void setName(String name);

    // Suppress generic wildcard warning
    @SuppressWarnings("java:S1452")
    List<Property<?>> getProperties();

    void setProperties(List<Property<?>> properties);

    String getType();

    default boolean isNameEquals(Step other) {
        return getName().equals(other.getName());
    }
}

