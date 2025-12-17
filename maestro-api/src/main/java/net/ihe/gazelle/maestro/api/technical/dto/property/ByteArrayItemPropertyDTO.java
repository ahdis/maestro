package net.ihe.gazelle.maestro.api.technical.dto.property;

import com.fasterxml.jackson.annotation.*;
import net.ihe.gazelle.maestro.api.business.testreport.ByteArrayItemProperty;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(
      name = "ByteArrayItemProperty",
      description = "A property containing a byte array value that references an item."
)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
      "name",
      "type",
      "value",
      "fileName",
      "mimeType",
      "itemType",
      "reference"
})
@JsonTypeName(ByteArrayItemPropertyDTO.TYPE)
public class ByteArrayItemPropertyDTO extends PropertyDTO<ByteArrayItemProperty> {

   @JsonIgnore
   public static final String TYPE = "BYTE_ARRAY_ITEM";

   public ByteArrayItemPropertyDTO() {
      super(new ByteArrayItemProperty());
   }

   public ByteArrayItemPropertyDTO(ByteArrayItemProperty property) {
      super(property);
   }

   @Schema(
         name = "value",
         description = "The byte array typed value of the property."
   )
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

   @Schema(
         name = "fileName",
         description = "The name of the file of the referenced resource."
   )
   @JsonProperty("fileName")
   public String getFileName() {
      return property.getFileName();
   }

   public void setFileName(String originalFileName) {
      property.setFileName(originalFileName);
   }

   @Schema(
         name = "mimeType",
         description = "The mimetype of the referenced resource."
   )
   @JsonProperty("mimeType")
   public String getMimeType() {
      return property.getMimeType();
   }

   public void setMimeType(String mimeType) {
      property.setMimeType(mimeType);
   }

   @Schema(
         name = "itemType",
         description = "The type of the referenced item."
   )
   @JsonProperty("itemType")
   public String getItemType() {
      return property.getItemType();
   }

   public void setItemType(String itemType) {
      property.setItemType(itemType);
   }

   @Schema(
         name = "reference",
         description = "A link referencing a remote binary resource."
   )
   @JsonProperty("reference")
   public String getReference() {
      return property.getReference();
   }

   public void setReference(String reference) {
      property.setReference(reference);
   }

   @Override
   @JsonIgnore
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
