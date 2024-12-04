package net.ihe.gazelle.maestro;

import java.util.List;

/**
 * Define an automated step to execute with all its required information.
 * A step contains has some properties representing its different required inputs.
 * @see Property
 */
public interface Step {

    String getName();

    // Suppress generic wildcard warning
    @SuppressWarnings("java:S1452")
    List<Property<?>> getProperties();

    void setProperty(List<Property<?>> properties);

    String getType();

    default boolean isNameEquals(Step other) {
        return getName().equals(other.getName());
    }
}

