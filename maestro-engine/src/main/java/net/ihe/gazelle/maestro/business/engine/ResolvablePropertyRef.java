package net.ihe.gazelle.maestro.business.engine;

import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.PropertyRef;
import net.ihe.gazelle.maestro.api.business.testrun.context.Context;

import java.util.NoSuchElementException;

/**
 * A ResolvablePropertyRef is a PropertyRef knowing TestRun's Context,
 * so it can resolve its reference and return the actual value
 * {@link PropertyRef}
 * {@link Context}
 * @param <T>
 */
public class ResolvablePropertyRef<T> extends PropertyRef<T> {

    private final Context context;

    public ResolvablePropertyRef(PropertyRef<T> propertyRef, Context context) {
        super(propertyRef.getName(), propertyRef.getReference(), propertyRef.getType());
        this.context = context;
    }

    @Override
    public T getValue() {
        return resolveRef(reference).getValue();
    }

    /**
     * resolve property reference
     * @param ref name of the referred property
     * @return the property with name equals to ref
     * @throws NoSuchElementException if the ref does not match any properties in the context
     * @throws ClassCastException if the type of the referred property does not match the type of the expected result.
     */
    @SuppressWarnings("unchecked")
    protected Property<T> resolveRef(String ref) {
        if(context.containsKey(ref)){
            return (Property<T>) context.getProperty(ref);
        }
        throw new NoSuchElementException("Could not resolve property reference " + ref);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ResolvablePropertyRef && super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
