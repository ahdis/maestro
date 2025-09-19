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

package net.ihe.gazelle.maestro.api.business.testrun;

import net.ihe.gazelle.maestro.api.business.testrun.context.Context;

import java.util.Objects;

/**
 * A structure containing a String name and a value of type T
 * Property are used to defined Step inputs.
 * {@link Step}
 * <br>
 * Context can also contain a list of Property.
 * {@link Context}
 * <br>
 * Property can also be a PropertyRef
 * {@link PropertyRef}
 * <br>
 * @param <T> Value type, can be any object
 */
public class Property<T> {

    private String name;
    private T value;

    public Property() {
        // Empty for serialization
    }

    public Property(String name, T value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Property<T> setName(String name) {
        this.name = name;
        return this;
    }

    public T getValue() {
        return value;
    }

    public Property<T> setValue(T value) {
        this.value = value;
        return this;
    }

    /**
     * Best-effort runtime type of the value.
     * For regular properties, returns {@code value.getClass()} when non-null, otherwise {@code null}.
     * Subclasses like {@link PropertyRef} may override to provide a non-null declared type.
     */
    public Class<?> getType() {
        return value != null ? value.getClass() : null;
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
