/*
 * Copyright 2026 IHE International.
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

package net.ihe.gazelle.maestro.api.business.property;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * Abstract class representing a property with a name and a value holder.
 */
public abstract class Property implements Serializable {

    @Serial
    private static final long serialVersionUID = -5410177558892768955L;

    /**
     * The name of the property.
     */
    private String name;

    /**
     * The value holder of the property.
     */
    private ValueHolder valueHolder;

    /**
     * The user who created the property.
     */
    private String userId;

    /**
     * The date of the property creation.
     */
    private Instant dateTime;

    /**
     * Default constructor.
     */
    Property() {
        valueHolder = new DirectValue(null);
    }

    /**
     * Constructs a new {@code Property} with the specified name and value.
     *
     * @param name  the name of the property, which identifies it uniquely
     * @param value the value to be assigned to the property; must be a Serializable object
     */
    Property(String name, Serializable value) {
        this(name, value, null, null);
    }

    /**
     * Constructs a new {@code Property} with the specified name and value.
     *
     * @param name  the name of the property, which identifies it uniquely
     * @param value the value to be assigned to the property; must be a Serializable object
     * @param userId the user id of the property
     * @param dateTime the date and time of the property
     */
    Property(String name, Serializable value, String userId, Instant dateTime) {
        this.name = name;
        setValue(value);
        this.userId = userId;
        this.dateTime = dateTime;
    }

    /**
     * Retrieves the name of the property.
     *
     * @return the name of the property, or {@code null} if the name has not been defined
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the property and returns the updated Property object.
     *
     * @param name the name to be assigned to the property
     * @return the updated Property object with the new name
     */
    public Property setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Retrieves the value of the property in a type-safe manner.
     * The returned value is cast to the requested type, which is expected
     * to extend {@code Serializable}.
     *
     * @param <T> the type of the value, which must extend {@code Serializable}
     * @return the value of the property as the specified type, or {@code null}
     * if no value is set or if the stored value is incompatible with the expected type
     * @throws ClassCastException if the stored value is incompatible with the expected type
     */
    @SuppressWarnings("unchecked")
    public <T extends Serializable> T getValue() {
        return (T) valueHolder.getValue();
    }

    /**
     * Sets the value of the property. If the provided value is a string that matches
     * the reference pattern, it is stored as a {@code ReferenceValue}. Otherwise,
     * it is stored as a {@code DirectValue}.
     *
     * @param value the value to be assigned to the property; must be a {@code Serializable} object
     * @return the updated {@code Property} object with the new value
     */
    public Property setValue(Serializable value) {
        if (value instanceof String strValue && strValue.matches(ReferenceValue.REFERENCE_REGEX)) {
            this.valueHolder = new ReferenceValue(strValue);
        } else {
            this.valueHolder = new DirectValue(value);
        }
        return this;
    }

    /**
     * Retrieves the user ID associated with this property.
     *
     * @return the user ID as a string, or null if the user ID is not set
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user ID associated with this property and returns the updated Property object.
     *
     * @param userId the user ID to be assigned; should be a non-null string
     * @return the updated Property object with the user ID set
     */
    public Property setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    /**
     * Retrieves the date and time associated with this property.
     *
     * @return an {@code Instant} representing the date and time, or {@code null} if no date and time are set
     */
    public Instant getDateTime() {
        return dateTime;
    }

    /**
     * Sets the date and time associated with this property and returns the updated Property object.
     *
     * @param dateTime the date and time to be assigned to the property; must be an {@code Instant} object
     * @return the updated {@code Property} object with the date and time set
     */
    public Property setDateTime(Instant dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    /**
     * Creates and returns a copy of this {@code Property} object.
     * The specific subtype of {@code Property} is preserved in the copy.
     *
     * @return a new {@code Property} instance that is a copy of this object
     */
    public abstract Property copy();

    /**
     * Retrieves the internal {@code ValueHolder} instance associated with this property.
     *
     * @return the {@code ValueHolder} instance, or {@code null} if no {@code ValueHolder} has been set
     */
    ValueHolder getValueHolder() {
        return valueHolder;
    }

    /**
     * Determines whether the current property value is a reference.
     *
     * @return {@code true} if the value holder is an instance of {@code ReferenceValue},
     * otherwise {@code false}.
     */
    public boolean isReference() {
        return valueHolder instanceof ReferenceValue;
    }

    /**
     * Returns the referenced property name when this property is a reference.
     *
     * @return referenced property name, or {@code null} when this property is not a reference
     */
    public String getReferenceName() {
        if (!isReference()) {
            return null;
        }
        return ((ReferenceValue) valueHolder).getReference();
    }

    /**
     * Returns a value suitable for property copy operations.
     * <p>
     * For reference properties, this returns the original reference expression (e.g. {@code ${inputName}})
     * so the copied property preserves provenance metadata.
     * For direct values, this returns the resolved value as-is.
     *
     * @return copy-safe value preserving references when applicable
     */
    protected Serializable getCopyValue() {
        if (valueHolder instanceof ReferenceValue referenceValue && !referenceValue.isSupplied()) {
            return ReferenceValue.format(getReferenceName());
        }
        return getValue();
    }

    /**
     * Determines whether the name of the property has been defined.
     *
     * @return true if the name is not null and not blank, otherwise false
     */
    public boolean isNameDefined() {
        return name != null && !name.isBlank();
    }

}
