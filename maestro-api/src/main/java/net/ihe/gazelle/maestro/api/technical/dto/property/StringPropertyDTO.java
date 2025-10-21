package net.ihe.gazelle.maestro.api.technical.dto.property;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;

@JsonTypeName(StringPropertyDTO.TYPE)
public class StringPropertyDTO extends PropertyDTO<StringProperty> {

   public static final String TYPE = "STRING";

   public StringPropertyDTO() {
      super(new StringProperty());
   }

   public StringPropertyDTO(StringProperty property) {
      super(property);
   }

   @JsonProperty(VALUE)
   public String getValue() {
      return property.getValue();
   }

   public void setValue(String value) {
      property.setValue(value);
   }

   @Override
   public Class<StringProperty> getBusinessObjectClass() {
      return StringProperty.class;
   }
}
