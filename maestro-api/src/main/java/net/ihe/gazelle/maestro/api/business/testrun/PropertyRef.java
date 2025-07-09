/*
 * Copyright 2025 IHE International.
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

import java.util.Objects;

/**
 * A PropertyRef is a Property referencing another Property contains in TestRun Context.
 * A PropertyRef's value cannot be resolved because PropertyRef doesn't have access to Context.
 * ResolvablePropertyRef are defined in maestro-engine module.
 * @param <T> Value type
 */
public class PropertyRef<T> extends Property<T> {

    protected final String reference;
    protected final Class<T> type;

    public PropertyRef(String name, String reference, Class<T> type) {
        super(name, null);
        this.reference = reference;
        this.type = type;
    }

    public String getReference() {
        return reference;
    }

    public Class<T> getType() {
        return type;
    }

    @Override
    public T getValue() {
        throw  new IllegalStateException("Unresolvable property, engine did not convert it as resolvable property");
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PropertyRef<?> that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(reference, that.reference)
                && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), reference, type);
    }
}
