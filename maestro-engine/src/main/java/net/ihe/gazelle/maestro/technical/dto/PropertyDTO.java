package net.ihe.gazelle.maestro.technical.dto;

import net.ihe.gazelle.maestro.Property;
import net.ihe.gazelle.maestro.PropertyRef;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

/**
 * <p>
 *     DTO For Property Objects. It has 3 attributes
 *     <p>
 *         <b>name</b> Property name
 *     </p>
 *     <p>
 *         <b>value</b> property value represented as a String,
 *         but the final Property value can be of various type describe in type field
 *     </p>
 *     <p>
 *         <b>type</b> property type. There are different possible value for this field :
 *         <p>
 *             - <b>string</b> corresponding to String
 *         </p>
 *         <p>
 *             - <b>integer</b> corresponding to Integer
 *         </p>
 *         <p>
 *             - <b>boolean</b> corresponding to Boolean
 *         </p>
 *         <p>
 *             - <b>double</b> corresponding to Double
 *         </p>
 *         <p>
 *             - <b>byteArray</b> corresponding to byte[]
 *         </p>
 *         <p>
 *             - <b>date</b> corresponding to Date from java.util library
 *         </p>
 *     </p>
 * </p>
 */
public class PropertyDTO {

    public static final String ISO_8601_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

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
        return Base64.getDecoder().decode(value);
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
        return new PropertyRef<>(name, reference);
    }

}
