package net.ihe.gazelle.maestro.api.technical.dto.test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import net.ihe.gazelle.maestro.api.business.test.SupportedTextInput;

import java.util.List;

@JsonTypeName(SupportedTextInputDTO.TYPE)
@JsonPropertyOrder({"id", "type", "label", "required", "possibleValues", "defaultValue"})
public class SupportedTextInputDTO extends SupportedInputDTO<SupportedTextInput> {

   public static final String TYPE = "TEXT";

   public SupportedTextInputDTO() {
      super(new SupportedTextInput());
   }

   public SupportedTextInputDTO(SupportedTextInput supportedInput) {
      super(supportedInput);
   }

   @JsonProperty("possibleValues")
   public List<String> getPossibleValues() {
      return supportedInput.getPossibleValues();
   }

   public void setPossibleValues(List<String> possibleValues) {
      supportedInput.setPossibleValues(possibleValues);
   }

   @JsonProperty("defaultValue")
   public String getDefaultValue() {
      return supportedInput.getDefaultValue();
   }

   public void setDefaultValue(String defaultValue) {
      supportedInput.setDefaultValue(defaultValue);
   }

   @Override
   public Class<SupportedTextInput> getBusinessObjectClass() {
      return SupportedTextInput.class;
   }

}
