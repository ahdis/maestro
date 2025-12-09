package net.ihe.gazelle.maestro.api.technical.dto.property;

import com.fasterxml.jackson.annotation.*;
import net.ihe.gazelle.maestro.api.business.property.FloatProperty;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName(FloatPropertyDTO.TYPE)
public class FloatPropertyDTO extends PropertyDTO<FloatProperty> {

    @JsonIgnore
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
   public void setValue(Serializable value) {
      super.setResolvedValue(Double.class, Double::valueOf, value);
   }

   @Override
   @JsonIgnore
   public Class<FloatProperty> getBusinessObjectClass() {
      return FloatProperty.class;
   }

}
