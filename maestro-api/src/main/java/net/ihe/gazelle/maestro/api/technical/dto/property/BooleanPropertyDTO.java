package net.ihe.gazelle.maestro.api.technical.dto.property;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import net.ihe.gazelle.maestro.api.business.property.BooleanProperty;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.io.Serializable;

@Schema(
      name = "BooleanProperty",
      description = "A property that supports boolean value."
)
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

   @Schema(
         name = "value",
         description = "The boolean typed value of the property."
   )
   @JsonProperty(VALUE)
   public Boolean getValue() {
      return property.getValue();
   }

   public void setValue(Serializable value) {
      super.setResolvedValue(Boolean.class, Boolean::valueOf, value);
   }

   @Override
   public Class<BooleanProperty> getBusinessObjectClass() {
      return BooleanProperty.class;
   }
}
