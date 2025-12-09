package net.ihe.gazelle.maestro.api.technical.dto.property;

import com.fasterxml.jackson.annotation.*;
import net.ihe.gazelle.maestro.api.business.property.IntegerProperty;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName(IntegerPropertyDTO.TYPE)
public class IntegerPropertyDTO extends PropertyDTO<IntegerProperty> {

    @JsonIgnore
   public static final String TYPE = "INTEGER";

   public IntegerPropertyDTO() {
      super(new IntegerProperty());
   }

   public IntegerPropertyDTO(IntegerProperty property) {
      super(property);
   }

   @JsonProperty(VALUE)
   public Integer getValue() {
      return property.getValue();
   }

   @JsonSetter(VALUE)
   public void setValue(Serializable value) {
      super.setResolvedValue(Integer.class, Integer::valueOf, value);
   }

   @Override
   @JsonIgnore
   public Class<IntegerProperty> getBusinessObjectClass() {
      return IntegerProperty.class;
   }
}
