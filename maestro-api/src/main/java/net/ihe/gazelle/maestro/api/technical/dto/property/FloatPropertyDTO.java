package net.ihe.gazelle.maestro.api.technical.dto.property;

import com.fasterxml.jackson.annotation.*;
import net.ihe.gazelle.maestro.api.business.property.FloatProperty;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.io.Serializable;

@Schema(
        name = "FloatProperty",
        description = "A property that supports float value."
)
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

    @Schema(
            name = "value",
            description = "The float typed value of the property.",
            examples = "3.14"
    )
   @JsonProperty(VALUE)
   public Double getValue() {
      return property.getValue();
   }

   public void setValue(Serializable value) {
      super.setResolvedValue(Double.class, Double::valueOf, value);
   }

   @Override
   @JsonIgnore
   public Class<FloatProperty> getBusinessObjectClass() {
      return FloatProperty.class;
   }

}
