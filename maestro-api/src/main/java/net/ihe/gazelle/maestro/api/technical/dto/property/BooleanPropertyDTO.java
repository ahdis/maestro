package net.ihe.gazelle.maestro.api.technical.dto.property;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import net.ihe.gazelle.maestro.api.business.property.BooleanProperty;

@JsonTypeName(BooleanPropertyDTO.TYPE)
public class BooleanPropertyDTO extends PropertyDTO<BooleanProperty> {

   public static final String TYPE = "BOOLEAN";

   public BooleanPropertyDTO() {
      super(new BooleanProperty());
   }

   public BooleanPropertyDTO(BooleanProperty property) {
      super(property);
   }

   @JsonProperty(VALUE)
   public Boolean getValue() {
      return property.getValue();
   }

   public void setValue(Boolean value) {
      property.setValue(value);
   }

   @Override
   public Class<BooleanProperty> getBusinessObjectClass() {
      return BooleanProperty.class;
   }
}
