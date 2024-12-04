package net.ihe.gazelle.maestro;

import java.util.Objects;

/**
 * A PropertyRef is a Property referencing another Property contains in TestRun Context.
 * A PropertyRef's value cannot be resolved because PropertyRef doesn't have access to Context.
 * ResolvablePropertyRef are defined in maestro-engine module.
 * @param <T> Value type
 */
public class PropertyRef<T> extends Property<T> {
    public final String reference;

    public PropertyRef(String name, String reference) {
        super(name, null);
        this.reference = reference;
    }

    @Override
    public T getValue() {
        throw  new IllegalStateException("Unresolvable property, engine did not convert it as resolvable property");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PropertyRef<?> that = (PropertyRef<?>) o;
        return Objects.equals(reference, that.reference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), reference);
    }
}
