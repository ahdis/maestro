package net.ihe.gazelle.maestro.api.technical.dto.test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import net.ihe.gazelle.maestro.api.business.test.SupportedValueSetInput;

@JsonTypeName(SupportedValueSetInputDTO.TYPE)
@JsonPropertyOrder({"id", "type", "label", "required", "valueSetId", "defaultValue"})
public class SupportedValueSetInputDTO extends SupportedInputDTO<SupportedValueSetInput> {

   public static final String TYPE = "VALUE_SET";

   public SupportedValueSetInputDTO() {
      super(new SupportedValueSetInput());
   }

   public SupportedValueSetInputDTO(SupportedValueSetInput supportedInput) {
      super(supportedInput);
   }

   @JsonProperty("defaultValue")
   public String getDefaultValue() {
      return supportedInput.getDefaultValue();
   }

   public void setDefaultValue(String defaultValue) {
      supportedInput.setDefaultValue(defaultValue);
   }

   @JsonProperty("valueSetId")
   public String getValueSetId() {
      return supportedInput.getValueSetId();
   }

   public void setValueSetId(String valueSetId) {
      supportedInput.setValueSetId(valueSetId);
   }

   @Override
   public Class<SupportedValueSetInput> getBusinessObjectClass() {
      return SupportedValueSetInput.class;
   }

}
