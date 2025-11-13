package net.ihe.gazelle.maestro.simulation.step.record.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import net.ihe.gazelle.simulation.business.callback.ValidationReport;
import net.ihe.gazelle.simulation.business.callback.ValidationResult;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
      "validationProfileId",
      "validationResult",
      "subject",
      "referenceName",
      "itemType"
})
public class ValidationReferenceDTO {

   private final String validationProfileId;
   private final String validationResult;
   private final String subject;
   private final String referenceName;
   private final String itemType;

   public ValidationReferenceDTO(ValidationReport validationReport, String referenceName, String itemType) {
      this.validationProfileId = validationReport.getValidationProfileId();
      this.validationResult = nameOf(validationReport.getValidationResult());
      this.subject = validationReport.getSubject();
      this.referenceName = referenceName;
      this.itemType = itemType;
   }

   @JsonGetter("validationProfileId")
   public String getValidationProfileId() {
      return validationProfileId;
   }

   @JsonGetter("validationResult")
   public String getValidationResult() {
      return validationResult;
   }

   @JsonGetter("subject")
   public String getSubject() {
      return subject;
   }

   @JsonGetter("referenceName")
   public String getReferenceName() {
      return referenceName;
   }

   @JsonGetter("itemType")
   public String getItemType() {
      return itemType;
   }

   private String nameOf(ValidationResult value) {
      return value == null ? null : value.name();
   }
}
