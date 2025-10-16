/*
 * Copyright 2025 Kereval.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ihe.gazelle.maestro.engine.business;

import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.PropertyRef;
import net.ihe.gazelle.maestro.engine.business.context.Context;

import java.util.NoSuchElementException;

/**
 * A ResolvablePropertyRef is a PropertyRef knowing TestRun's Context,
 * so it can resolve its reference and return the actual value
 * {@link PropertyRef}
 * {@link Context}
 * @param <T>
 */
class ResolvablePropertyRef<T> extends PropertyRef<T> {

    private final Context context;

    ResolvablePropertyRef(PropertyRef<T> propertyRef, Context context) {
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
