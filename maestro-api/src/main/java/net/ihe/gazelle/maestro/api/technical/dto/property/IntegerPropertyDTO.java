package net.ihe.gazelle.maestro.api.technical.dto.property;

import com.fasterxml.jackson.annotation.*;
import net.ihe.gazelle.maestro.api.business.property.IntegerProperty;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.io.Serializable;

@Schema(
      name = "IntegerProperty",
      description = "A property that supports integer value."
)
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

   @Schema(
         name = "value",
         description = "The integer typed value of the property.",
         examples = "3"
   )
   @JsonProperty(VALUE)
   public Integer getValue() {
      return property.getValue();
   }

   public void setValue(Serializable value) {
      super.setResolvedValue(Integer.class, Integer::valueOf, value);
   }

   @Override
   @JsonIgnore
   public Class<IntegerProperty> getBusinessObjectClass() {
      return IntegerProperty.class;
   }
}
