package net.ihe.gazelle.maestro.api.technical.dto.test;

import com.fasterxml.jackson.annotation.*;
import net.ihe.gazelle.maestro.api.business.test.SupportedValueSetInput;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(
      name = "SupportedValueSetInput",
      description = "A value set input that can be used as a step or test input."
)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName(SupportedValueSetInputDTO.TYPE)
@JsonPropertyOrder({
      "id",
      "type",
      "label",
      "required",
      "valueSetId",
      "defaultValue"
})
public class SupportedValueSetInputDTO extends SupportedInputDTO<SupportedValueSetInput> {

   @JsonIgnore
   public static final String TYPE = "VALUE_SET";

   public SupportedValueSetInputDTO() {
      super(new SupportedValueSetInput());
   }

   public SupportedValueSetInputDTO(SupportedValueSetInput supportedInput) {
      super(supportedInput);
   }

   @Schema(
         name = "defaultValue",
         description = "A default value provided by the step definition."
   )
   @JsonProperty("defaultValue")
   public String getDefaultValue() {
      return supportedInput.getDefaultValue();
   }

   public void setDefaultValue(String defaultValue) {
      supportedInput.setDefaultValue(defaultValue);
   }

   @Schema(
         name = "valueSetId",
         description = "The unique identifier of the value set from which to retrieve the possible values for the input."
   )
   @JsonProperty("valueSetId")
   public String getValueSetId() {
      return supportedInput.getValueSetId();
   }

   public void setValueSetId(String valueSetId) {
      supportedInput.setValueSetId(valueSetId);
   }

   @Override
   @JsonIgnore
   public Class<SupportedValueSetInput> getBusinessObjectClass() {
      return SupportedValueSetInput.class;
   }

}
