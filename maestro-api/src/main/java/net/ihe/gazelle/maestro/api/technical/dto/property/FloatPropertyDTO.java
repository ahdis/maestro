package net.ihe.gazelle.maestro.api.technical.dto.property;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeName;
import net.ihe.gazelle.maestro.api.business.property.FloatProperty;

@JsonTypeName(FloatPropertyDTO.TYPE)
public class FloatPropertyDTO extends PropertyDTO<FloatProperty> {

   public static final String TYPE = "FLOAT";

   public FloatPropertyDTO() {
      super(new FloatProperty());
   }

   public FloatPropertyDTO(FloatProperty property) {
      super(property);
   }

   @JsonProperty(VALUE)
   public Double getValue() {
      return property.getValue();
   }

   @JsonSetter(VALUE)
   public void setValue(Object value) {
      super.setResolvedValue(Double.class, Double::valueOf, value);
   }

   @Override
   public Class<FloatProperty> getBusinessObjectClass() {
      return FloatProperty.class;
   }

}
