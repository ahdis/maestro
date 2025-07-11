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

package net.ihe.gazelle.maestro.technical.dto.testrun;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.PropertyRef;
import net.ihe.gazelle.maestro.api.business.testrun.UnsupportedPropertyTypeException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@JsonPropertyOrder({"name", "value", "type"})
public class PropertyDTO {

    public static final String ISO_8601_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
    private static final Pattern REFERENCE_PATTERN = Pattern.compile("^\\$\\{(\\w+)}$");
    public static final String STRING = "string";
    public static final String INTEGER = "integer";
    public static final String BOOLEAN = "boolean";
    public static final String DOUBLE = "double";
    public static final String BYTE_ARRAY = "byteArray";
    public static final String DATE = "date";

    @JsonIgnore
    private Property<?> domain;
    @JsonIgnore
    private String value;
    @JsonIgnore
    private String type;

    public PropertyDTO() {
        domain = new Property<>();
    }

    public PropertyDTO(Property<?> domain) {
        if (domain instanceof PropertyRef<?> propertyRef) {
            value = "${" + propertyRef.getReference() + "}";
            type = extractType(propertyRef);
        } else {
            if (domain.getValue() instanceof String stringValue) {
                value = stringValue;
                type = STRING;
            } else if (domain.getValue() instanceof Integer integer) {
                value = String.valueOf(integer);
                type = INTEGER;
            } else if (domain.getValue() instanceof Boolean booleanValue) {
                value = String.valueOf(booleanValue);
                type = BOOLEAN;
            } else if (domain.getValue() instanceof Double doubleValue) {
                value = String.valueOf(doubleValue);
                type = DOUBLE;
            } else if (domain.getValue() instanceof byte[] binaryValue) {
                value = Base64.getEncoder().encodeToString(binaryValue);
                type = BYTE_ARRAY;
            } else if (domain.getValue() instanceof Date date) {
                SimpleDateFormat formatter = new SimpleDateFormat(ISO_8601_DATETIME_PATTERN);
                value = formatter.format(date);
                type = DATE;
            } else {
                throw new UnsupportedPropertyTypeException("Unsupported property type : " + domain.getValue().getClass().getName());
            }
        }
        this.domain = domain;
    }

    @JsonGetter("name")
    public String getName() {
        return domain.getName();
    }

    @JsonSetter("name")
    public void setName(String name) {
        domain.setName(name);
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
    @SuppressWarnings("java:S1452")
    public Property<?> getDomain() {
        switch (type) {
            case STRING -> domain = getReferenceOr(
                    String.class,
                    () -> new Property<>(this.getName(), value)
            );
            case INTEGER -> domain = getReferenceOr(
                    Integer.class,
                    () -> new Property<>(this.getName(), Integer.valueOf(value))
            );
            case BOOLEAN -> domain = getReferenceOr(
                    Boolean.class,
                    () -> new Property<>(this.getName(), Boolean.valueOf(value))
            );
            case DOUBLE -> domain = getReferenceOr(
                    Double.class,
                    () -> new Property<>(this.getName(), Double.valueOf(value))
            );
            case BYTE_ARRAY -> domain = getReferenceOr(
                    byte[].class,
                    () -> new Property<>(this.getName(), Base64.getDecoder().decode(value))
            );
            case DATE -> domain = getReferenceOr(
                    Date.class,
                    () -> new Property<>(this.getName(), parseDate(value))
            );
            default -> throw new UnsupportedPropertyTypeException("Unsupported property type : " + type);
        }
        return domain;
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
