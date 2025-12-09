package net.ihe.gazelle.maestro.api.technical.dto.property;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeName;
import net.ihe.gazelle.maestro.api.business.property.BooleanProperty;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
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

   @JsonSetter(VALUE)
   public void setValue(Serializable value) {
       super.setResolvedValue(Boolean.class, Boolean::valueOf, value);
   }

   @Override
   public Class<BooleanProperty> getBusinessObjectClass() {
      return BooleanProperty.class;
   }
}
