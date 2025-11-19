package net.ihe.gazelle.maestro.validation.step.technical.record.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import net.ihe.gazelle.validation.v2.api.business.Input;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
      "id",
      "itemId",
      "location",
      "referenceName",
      "referenceType"
})
public class ValidationInputReferenceDTO {

   private static final String DEFAULT_REFERENCE_NAME = "validation-input";

   private final String id;
   private final String itemId;
   private final String location;
   private final String referenceName;
   private final String referenceType;

   public ValidationInputReferenceDTO(Input input, String referenceType) {
      this.id = input.getId();
      this.itemId = input.getItemId();
      this.location = input.getLocation();
      this.referenceName = resolveReferenceName(input.getId());
      this.referenceType = referenceType;
   }

   @JsonGetter("id")
   public String getId() {
      return id;
   }

   @JsonGetter("itemId")
   public String getItemId() {
      return itemId;
   }

   @JsonGetter("location")
   public String getLocation() {
      return location;
   }

   @JsonGetter("referenceName")
   public String getReferenceName() {
      return referenceName;
   }

   @JsonGetter("referenceType")
   public String getReferenceType() {
      return referenceType;
   }

   @JsonIgnore
   public static String resolveReferenceName(String candidate) {
      if (candidate == null || candidate.isBlank()) {
         return DEFAULT_REFERENCE_NAME;
      }
      return candidate;
   }
}
