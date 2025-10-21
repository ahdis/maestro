package net.ihe.gazelle.maestro.api.technical.dto.test;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import net.ihe.gazelle.maestro.api.business.test.SupportedInput;
import net.ihe.gazelle.modelmarshaller.technical.dto.SubTypingDTO;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "type", "label", "required"})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public abstract class SupportedInputDTO<T extends SupportedInput> implements SubTypingDTO<T> {

   @JsonIgnore
   protected T supportedInput;

   protected SupportedInputDTO(T supportedInput) {
      initWithBusinessObject(supportedInput);
   }

   @Override
   public T getBusinessObject() {
      return supportedInput;
   }

   @Override
   public SubTypingDTO<T> initWithBusinessObject(T supportedInput) {
      this.supportedInput = supportedInput;
      return this;
   }

   @JsonProperty("id")
   public String getId() {
      return supportedInput.getId();
   }

   public void setId(String id) {
      supportedInput.setId(id);
   }

   @JsonProperty("label")
   public String getLabel() {
      return supportedInput.getLabel();
   }

   public void setLabel(String label) {
      supportedInput.setLabel(label);
   }

   @JsonProperty("required")
   public boolean isRequired() {
      return supportedInput.isRequired();
   }

   public void setRequired(boolean required) {
      supportedInput.setRequired(required);
   }

}
