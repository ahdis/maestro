package net.ihe.gazelle.maestro.api.technical.dto.test;

import com.fasterxml.jackson.annotation.*;
import net.ihe.gazelle.maestro.api.business.test.SupportedInput;
import net.ihe.gazelle.modelmarshaller.technical.dto.SubTypingDTO;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "type",
        "label",
        "groupName",
        "description",
        "required"
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public abstract class SupportedInputDTO<T extends SupportedInput> implements SubTypingDTO<T> {

   @JsonIgnore
   protected T supportedInput;

   protected SupportedInputDTO(T supportedInput) {
      initWithBusinessObject(supportedInput);
   }

   @Override
   @JsonIgnore
   public T getBusinessObject() {
      return supportedInput;
   }

   @Override
   @JsonIgnore
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

   @JsonProperty("groupName")
   public String getGroupName() {
       return supportedInput.getGroupName();
   }

   public void setGroupName(String groupName) {
       supportedInput.setGroupName(groupName);
   }

   @JsonProperty("description")
   public String getDescription() {
       return supportedInput.getDescription();
   }

   public void setDescription(String description) {
       supportedInput.setDescription(description);
   }

   @JsonProperty("required")
   public boolean isRequired() {
      return supportedInput.isRequired();
   }

   public void setRequired(boolean required) {
      supportedInput.setRequired(required);
   }

}
