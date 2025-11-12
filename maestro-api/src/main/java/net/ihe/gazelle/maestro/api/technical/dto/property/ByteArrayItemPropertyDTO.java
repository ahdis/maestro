package net.ihe.gazelle.maestro.api.technical.dto.property;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import net.ihe.gazelle.maestro.api.business.testreport.ByteArrayItemProperty;

@JsonPropertyOrder({"name", "type", "value", "fileName", "mimeType", "reference"})
@JsonTypeName(ByteArrayItemPropertyDTO.TYPE)
public class ByteArrayItemPropertyDTO extends PropertyDTO<ByteArrayItemProperty> {

   public static final String TYPE = "BYTE_ARRAY_ITEM";

   public ByteArrayItemPropertyDTO() {
      super(new ByteArrayItemProperty());
   }

   public ByteArrayItemPropertyDTO(ByteArrayItemProperty property) {
      super(property);
   }

   @JsonProperty(VALUE)
   public byte[] getValue() {
      return property.getValue();
   }

   public void setValue(String value) {
      if (value == null) {
         property.setValue(null);
         return;
      }
      if (isReference(value)) {
         property.setValue(value);
      } else {
         property.setValue(decodeBase64(value));
      }
   }

   @JsonProperty("fileName")
   public String getFileName() {
      return property.getFileName();
   }

   public void setFileName(String originalFileName) {
      property.setFileName(originalFileName);
   }

   @JsonProperty("mimeType")
   public String getMimeType() {
      return property.getMimeType();
   }

   public void setMimeType(String mimeType) {
      property.setMimeType(mimeType);
   }

   @JsonProperty("reference")
   public String getReference() {
      return property.getReference();
   }

   public void setReference(String reference) {
      property.setReference(reference);
   }

   @Override
   public Class<ByteArrayItemProperty> getBusinessObjectClass() {
      return ByteArrayItemProperty.class;
   }

   private byte[] decodeBase64(String value) {
      String trimmed = value.strip();
      if (trimmed.startsWith("data:")) {
         int commaIndex = trimmed.indexOf(',');
         if (commaIndex >= 0 && commaIndex + 1 < trimmed.length()) {
            trimmed = trimmed.substring(commaIndex + 1);
         }
      }
      return java.util.Base64.getDecoder().decode(trimmed.replaceAll("\\s", ""));
   }
}
