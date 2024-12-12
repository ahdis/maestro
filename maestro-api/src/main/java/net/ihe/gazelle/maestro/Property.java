package net.ihe.gazelle.maestro;

import java.util.Objects;

/**
 * A structure containing a String name and a value of type T
 * Property are used to defined Step inputs.
 * {@link Step}
 * <br>
 * Context can also contain a list of Property.
 * {@link net.ihe.gazelle.maestro.context.Context}
 * <br>
 * Property can also be a PropertyRef
 * {@link PropertyRef}
 * <br>
 * @param <T> Value type, can be any object
 */
public class Property<T> {

    private final String name;
    protected final T value;

    public Property(String name, T value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Property<?> property = (Property<?>) o;
        return Objects.equals(name, property.name) && Objects.equals(value, property.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }
}
