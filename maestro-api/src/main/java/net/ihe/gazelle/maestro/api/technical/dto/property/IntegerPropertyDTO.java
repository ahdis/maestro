package net.ihe.gazelle.maestro.api.technical.dto.property;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import net.ihe.gazelle.maestro.api.business.property.BooleanProperty;
import net.ihe.gazelle.maestro.api.business.property.IntegerProperty;

@JsonTypeName(IntegerPropertyDTO.TYPE)
public class IntegerPropertyDTO extends PropertyDTO<IntegerProperty> {

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

   public void setValue(Integer value) {
      property.setValue(value);
   }

   @Override
   public Class<IntegerProperty> getBusinessObjectClass() {
      return IntegerProperty.class;
   }
}
