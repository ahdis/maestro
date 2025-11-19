package net.ihe.gazelle.maestro.validation.step.technical.record.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import net.ihe.gazelle.validation.v2.api.business.Input;
import net.ihe.gazelle.validation.v2.api.business.report.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
      "uuid",
      "modelVersion",
      "dateTime",
      "disclaimer",
      "validationMethod",
      "overallResult",
      "additionalMetadata",
      "reports",
      "inputReferences",
      "counters"
})
public class ValidationReportItemDTO {

   private final String uuid;
   private final String modelVersion;
   private final Date dateTime;
   private final String disclaimer;
   private final ValidationMethod validationMethod;
   private final String overallResult;
   private final List<Metadata> additionalMetadata;
   private final List<ValidationSubReport> reports;
   private final List<ValidationInputReferenceDTO> inputReferences;
   private final ValidationCounters counters;

   public ValidationReportItemDTO(ValidationReport report,
         String inputReferenceType,
         List<Input> attachmentInputs) {
      Objects.requireNonNull(report, "report");
      this.uuid = report.getUuid();
      this.modelVersion = report.getModelVersion();
      this.dateTime = report.getDateTime();
      this.disclaimer = report.getDisclaimer();
      this.validationMethod = report.getValidationMethod();
      this.overallResult = nameOf(report.getOverallResult());
      this.additionalMetadata = copy(report.getAdditionalMetadata());
      this.reports = copy(report.getReports());
      this.inputReferences = buildInputReferences(attachmentInputs, inputReferenceType);
      this.counters = report.getCounters();
   }

   @JsonGetter("uuid")
   public String getUuid() {
      return uuid;
   }

   @JsonGetter("modelVersion")
   public String getModelVersion() {
      return modelVersion;
   }

   @JsonGetter("dateTime")
   public Date getDateTime() {
      return dateTime;
   }

   @JsonGetter("disclaimer")
   public String getDisclaimer() {
      return disclaimer;
   }

   @JsonGetter("validationMethod")
   public ValidationMethod getValidationMethod() {
      return validationMethod;
   }

   @JsonGetter("overallResult")
   public String getOverallResult() {
      return overallResult;
   }

   @JsonGetter("additionalMetadata")
   public List<Metadata> getAdditionalMetadata() {
      return additionalMetadata;
   }

   @JsonGetter("reports")
   public List<ValidationSubReport> getReports() {
      return reports;
   }

   @JsonGetter("inputReferences")
   public List<ValidationInputReferenceDTO> getInputReferences() {
      return inputReferences;
   }

   @JsonGetter("counters")
   public ValidationCounters getCounters() {
      return counters;
   }

   private <T> List<T> copy(List<T> values) {
      if (values == null || values.isEmpty()) {
         return List.of();
      }
      return List.copyOf(values);
   }

   private List<ValidationInputReferenceDTO> buildInputReferences(List<Input> inputs, String referenceType) {
      if (inputs == null || inputs.isEmpty()) {
         return List.of();
      }
      return inputs.stream()
            .map(input -> new ValidationInputReferenceDTO(input, referenceType))
            .toList();
   }

   private String nameOf(ValidationTestResult result) {
      return result == null ? null : result.name();
   }
}
