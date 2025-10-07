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

package net.ihe.gazelle.maestro.api.technical.dto.testrun;

import com.fasterxml.jackson.annotation.*;
import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.PropertyRef;
import net.ihe.gazelle.maestro.api.business.testrun.UnsupportedPropertyTypeException;
import net.ihe.gazelle.modelmarshaller.technical.dto.DTO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "name",
        "value",
        "type"
})
public class PropertyDTO implements DTO<Property<?>> {

    public static final String ISO_8601_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
    private static final Pattern REFERENCE_PATTERN = Pattern.compile("^\\$\\{(\\w+)}$");
    public static final String STRING = "string";
    public static final String INTEGER = "integer";
    public static final String BOOLEAN = "boolean";
    public static final String DOUBLE = "double";
    public static final String BYTE_ARRAY = "byteArray";
    public static final String DATE = "date";

    @JsonIgnore
    private Property<?> property;
    @JsonIgnore
    private String value;
    @JsonIgnore
    private String type;

    public PropertyDTO() {
        property = new Property<>();
    }

    public PropertyDTO(Property<?> property) {
        if (property instanceof PropertyRef<?> propertyRef) {
            value = "${" + propertyRef.getReference() + "}";
            type = extractType(propertyRef);
        } else {
            if (property.getValue() instanceof String stringValue) {
                value = stringValue;
                type = STRING;
            } else if (property.getValue() instanceof Integer integer) {
                value = String.valueOf(integer);
                type = INTEGER;
            } else if (property.getValue() instanceof Boolean booleanValue) {
                value = String.valueOf(booleanValue);
                type = BOOLEAN;
            } else if (property.getValue() instanceof Double doubleValue) {
                value = String.valueOf(doubleValue);
                type = DOUBLE;
            } else if (property.getValue() instanceof byte[] binaryValue) {
                value = Base64.getEncoder().encodeToString(binaryValue);
                type = BYTE_ARRAY;
            } else if (property.getValue() instanceof Date date) {
                SimpleDateFormat formatter = new SimpleDateFormat(ISO_8601_DATETIME_PATTERN);
                value = formatter.format(date);
                type = DATE;
            } else {
                throw new UnsupportedPropertyTypeException("Unsupported property type : " + property.getValue().getClass().getName());
            }
        }
        this.property = property;
    }

    @Override
    @JsonIgnore
    public Property<?> getBusinessObject() {
        switch (type) {
            case STRING -> property = getReferenceOr(
                    String.class,
                    () -> new Property<>(this.getName(), value)
            );
            case INTEGER -> property = getReferenceOr(
                    Integer.class,
                    () -> new Property<>(this.getName(), Integer.valueOf(value))
            );
            case BOOLEAN -> property = getReferenceOr(
                    Boolean.class,
                    () -> new Property<>(this.getName(), Boolean.valueOf(value))
            );
            case DOUBLE -> property = getReferenceOr(
                    Double.class,
                    () -> new Property<>(this.getName(), Double.valueOf(value))
            );
            case BYTE_ARRAY -> property = getReferenceOr(
                    byte[].class,
                    () -> new Property<>(this.getName(), Base64.getDecoder().decode(value))
            );
            case DATE -> property = getReferenceOr(
                    Date.class,
                    () -> new Property<>(this.getName(), parseDate(value))
            );
            default -> throw new UnsupportedPropertyTypeException("Unsupported property type : " + type);
        }
        return property;
    }

    @JsonGetter("name")
    public String getName() {
        return property.getName();
    }

    @JsonSetter("name")
    public void setName(String name) {
        property.setName(name);
    }

    @JsonGetter("value")
    public String getValue() {
        return value;
    }

    @JsonSetter("value")
    public void setValue(String value) {
        this.value = value;
    }

    @JsonGetter("type")
    public String getType() {
        return type;
    }

    @JsonSetter("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonIgnore
    private <T> String extractType(PropertyRef<T> propertyRef) {
        if (propertyRef.getType().equals(String.class)) {
            return STRING;
        } else if (propertyRef.getType().equals(Integer.class)) {
            return INTEGER;
        } else if (propertyRef.getType().equals(Boolean.class)) {
            return BOOLEAN;
        } else if (propertyRef.getType().equals(Double.class)) {
            return DOUBLE;
        } else if (propertyRef.getType().equals(byte[].class)) {
            return BYTE_ARRAY;
        } else if (propertyRef.getType().equals(Date.class)) {
            return DATE;
        }
        throw new UnsupportedPropertyTypeException("Unsupported property type for reference: " + propertyRef.getType().getTypeName());
    }

    @JsonIgnore
    private Property<?> getReferenceOr(Class<?> type, Supplier<Property<?>> propertySupplier) {
        if ((this.getValue() instanceof String valueString) && isReference(this.getValue())) {
            return new PropertyRef<>(this.getName(), getReference(valueString), type);
        } else  {
            return propertySupplier.get();
        }
    }

    @JsonIgnore
    private Date parseDate(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat(ISO_8601_DATETIME_PATTERN);
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + date + " Date shall match " + ISO_8601_DATETIME_PATTERN);
        }
    }

    /**
     * @param value a String
     * @return true if
     */
    @JsonIgnore
    private boolean isReference(String value) {
        Matcher matcher = REFERENCE_PATTERN.matcher(value);
        return matcher.matches();
    }

    /**
     * @param value A String following reference syntax
     * @return The property name contained in reference
     * @throws IllegalArgumentException if it can't extract Property from value
     */
    @JsonIgnore
    private String getReference(String value) {
        Matcher matcher = REFERENCE_PATTERN.matcher(value);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("Invalid reference: " + value);
    }
}
