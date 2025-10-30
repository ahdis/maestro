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

package net.ihe.gazelle.maestro.quarkus.ws.async.tmintegration;

import net.ihe.gazelle.maestro.api.business.UnsupportedPropertyTypeException;
import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.PropertyRef;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DTO for Property objects. It has 3 attributes:
 *
 * <ul>
 *   <li><b>name</b> – Property name</li>
 *   <li><b>value</b> – Property value represented as a {@code String}, but the final
 *       Property value can be of various types described in the {@code type} field</li>
 *   <li><b>type</b> – Property type. Possible values include:
 *     <ul>
 *       <li><b>string</b> – corresponds to {@link String}</li>
 *       <li><b>integer</b> – corresponds to {@link Integer}</li>
 *       <li><b>boolean</b> – corresponds to {@link Boolean}</li>
 *       <li><b>double</b> – corresponds to {@link Double}</li>
 *       <li><b>byteArray</b> – corresponds to {@code byte[]}</li>
 *       <li><b>date</b> – corresponds to {@link java.util.Date}</li>
 *     </ul>
 *   </li>
 * </ul>
 */
public class PropertyDTO {

    public static final String ISO_8601_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSX";

    private static final Pattern DATA_URI_BASE64_PREFIX = Pattern.compile("^data:[^,]*;base64,", Pattern.CASE_INSENSITIVE);

    private String name;
    protected String value;
    private String type;

    public PropertyDTO() {
        // Empty for serialization
    }

    public PropertyDTO(String name, String value, String type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Property with PropertyDTO and T a conversion of type field
     * @throws UnsupportedPropertyTypeException if type is not recognized
     */
    @SuppressWarnings("java:S1452")
    public Property<?> createProperty() {
        switch (type) {
            case "string" -> {
                return new Property<>(name, value);
            }
            case "integer" -> {
                return new Property<>(name, Integer.valueOf(value));
            }
            case "boolean" -> {
                return new Property<>(name, Boolean.valueOf(value));
            }
            case "double" -> {
                return new Property<>(name, Double.valueOf(value));
            }
            case "byteArray" -> {
                return new Property<>(name, parseByteArray(value));
            }
            case "date" -> {
                return new Property<>(name, parseDate(value));
            }
            default -> throw new UnsupportedPropertyTypeException("Unsupported property type: " + type);
        }

    }

    /**
     * Decode a representation in Base64 of byteArray to java object
     * @param value the string in Base64 of a byteArray
     * @return the byteArray Object converted from value
     */
    private byte[] parseByteArray(String value) {
        if (value == null) {
            return new byte[0];
        }

        String sanitized = value.trim();
        Matcher matcher = DATA_URI_BASE64_PREFIX.matcher(sanitized);
        if (matcher.find()) {
            sanitized = sanitized.substring(matcher.end());
        }

        sanitized = sanitized.replaceAll("\\s", "");
        if (sanitized.isEmpty()) {
            return new byte[0];
        }

        try {
            return Base64.getDecoder().decode(sanitized);
        } catch (IllegalArgumentException ex) {
            try {
                return Base64.getMimeDecoder().decode(sanitized);
            } catch (IllegalArgumentException mimeEx) {
                throw new IllegalArgumentException("Invalid Base64 encoding for byteArray property", mimeEx);
            }
        }
    }

    private Date parseDate(String value) {
        SimpleDateFormat formatter = new SimpleDateFormat(ISO_8601_DATETIME_PATTERN);
        try {
            return formatter.parse(value);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + value + " Date shall match " + ISO_8601_DATETIME_PATTERN);
        }
    }

    /**
     * @param reference String of Property name in Context
     * @return Property ref where field name is name from PropertyDTO and field reference is argument reference
     */
    @SuppressWarnings("java:S1452")
    public PropertyRef<?> createPropertyRef(String reference) {
        switch (type) {
            case "string" -> {
                return new PropertyRef<>(name, reference, String.class);
            }
            case "integer" -> {
                return new PropertyRef<>(name, reference, Integer.class);
            }
            case "boolean" -> {
                return new PropertyRef<>(name, reference, Boolean.class);
            }
            case "double" -> {
                return new PropertyRef<>(name, reference, Double.class);
            }
            case "byteArray" -> {
                return new PropertyRef<>(name, reference, byte[].class);
            }
            case "date" -> {
                return new PropertyRef<>(name, reference, Date.class);
            }
            default -> throw new UnsupportedPropertyTypeException("Unsupported property type: " + type);
        }
    }

}
