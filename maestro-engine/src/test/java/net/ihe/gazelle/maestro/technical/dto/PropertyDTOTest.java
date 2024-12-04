package net.ihe.gazelle.maestro.technical.dto;

import net.ihe.gazelle.maestro.Property;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class PropertyDTOTest {
    @Test
    void integerTest() {
        PropertyDTO propertyDTO = new PropertyDTO("intValue", "5", "integer");
        assertEquals("integer", propertyDTO.getType());
        Property<?> property = propertyDTO.createProperty();
        assertEquals("intValue", property.getName());
        assertInstanceOf(Integer.class, property.getValue());
        assertEquals(5, property.getValue());
    }

    @Test
    void booleanTest() {
        PropertyDTO propertyDTO = new PropertyDTO("booleanValue", "true", "boolean");
        assertEquals("boolean", propertyDTO.getType());
        Property<?> property = propertyDTO.createProperty();
        assertEquals("booleanValue", property.getName());
        assertInstanceOf(Boolean.class, property.getValue());
        assertEquals(true, property.getValue());
    }

    @Test
    void doubleTest() {
        PropertyDTO propertyDTO = new PropertyDTO("doubleValue", "5.0", "double");
        assertEquals("double", propertyDTO.getType());
        Property<?> property = propertyDTO.createProperty();
        assertEquals("doubleValue", property.getName());
        assertInstanceOf(Double.class, property.getValue());
        assertEquals(5.0, property.getValue());
    }

    @Test
    void byteArrayTest() {
        byte[] array = "hello".getBytes();
        String base64 = Base64.encodeBase64String(array);
        PropertyDTO propertyDTO = new PropertyDTO("byteArrayValue", base64, "byteArray");
        assertEquals("byteArray", propertyDTO.getType());
        Property<?> property = propertyDTO.createProperty();
        assertEquals("byteArrayValue", property.getName());
        assertInstanceOf(byte[].class, property.getValue());
        assertArrayEquals(array, (byte[]) property.getValue());
    }
    @Test
    void dateTest() throws ParseException {
        String date = "2024-07-05T12:18:28.975+0000";
        PropertyDTO propertyDTO = new PropertyDTO("dateValue", date, "date");
        assertEquals("date", propertyDTO.getType());
        Property<?> property = propertyDTO.createProperty();
        assertEquals("dateValue", property.getName());
        assertInstanceOf(Date.class, property.getValue());
        Date expected = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(date);
        assertEquals(expected, property.getValue());
    }

    @Test
    void stringTest() {
        PropertyDTO propertyDTO = new PropertyDTO("stringValue", "string", "string");
        assertEquals("string", propertyDTO.getType());
        Property<?> property = propertyDTO.createProperty();
        assertEquals("stringValue", property.getName());
        assertInstanceOf(String.class, property.getValue());
        assertEquals("string", property.getValue());
    }

    @Test
    void notSupportedTest() {
        PropertyDTO propertyDTO = new PropertyDTO("notSupportedValue", "notSupported", "notSupported");
        assertEquals("notSupported", propertyDTO.getType());
        assertThrows(UnsupportedPropertyTypeException.class, propertyDTO::createProperty);
    }
}
