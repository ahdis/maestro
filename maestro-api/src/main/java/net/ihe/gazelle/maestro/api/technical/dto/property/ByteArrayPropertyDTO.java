package net.ihe.gazelle.maestro.api.technical.dto.property;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;

@JsonTypeName(ByteArrayPropertyDTO.TYPE)
public class ByteArrayPropertyDTO extends PropertyDTO<ByteArrayProperty> {

   public static final String TYPE = "BYTE_ARRAY";

   public ByteArrayPropertyDTO() {
      super(new ByteArrayProperty());
   }

   public ByteArrayPropertyDTO(ByteArrayProperty property) {
      super(property);
   }

   @JsonProperty(VALUE)
   public byte[] getValue() {
      return property.getValue();
   }

   public void setValue(byte[] value) {
      property.setValue(value);
   }

   @JsonProperty("fileName")
   public String getFileName() {
      return property.getFileName();
   }

   public void setFileName(String originalFileName) {
      property.setFileName(originalFileName);
   }

   @Override
   public Class<ByteArrayProperty> getBusinessObjectClass() {
      return ByteArrayProperty.class;
   }

}
