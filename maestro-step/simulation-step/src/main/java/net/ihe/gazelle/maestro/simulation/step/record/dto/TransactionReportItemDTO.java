package net.ihe.gazelle.maestro.simulation.step.record.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import net.ihe.gazelle.errorhandling.technical.UnexpectedErrorDTO;
import net.ihe.gazelle.simulation.business.callback.Message;
import net.ihe.gazelle.simulation.business.callback.Result;
import net.ihe.gazelle.simulation.business.callback.TransactionReport;
import net.ihe.gazelle.simulation.business.callback.ValidationReport;
import net.ihe.gazelle.simulation.jaxrs.api.technical.dto.callback.RoleDTO;

import java.util.List;
import java.util.Objects;

import static net.ihe.gazelle.maestro.simulation.step.record.ReferenceNames.resolve;


@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
      "result",
      "transaction",
      "standards",
      "initiator",
      "responder",
      "messageReferences",
      "validationReportReferences",
      "unexpectedErrors"
})
public class TransactionReportItemDTO {

   private static final String DEFAULT_MESSAGE_REFERENCE = "message";
   private static final String DEFAULT_VALIDATION_REFERENCE = "validation";

   private final String result;
   private final String transaction;
   private final List<String> standards;
   private final RoleDTO initiator;
   private final RoleDTO responder;
   private final List<MessageReferenceDTO> messageReferences;
   private final List<ValidationReferenceDTO> validationReportReferences;
   private final List<UnexpectedErrorDTO> unexpectedErrors;

   public TransactionReportItemDTO(TransactionReport transactionReport,
         String messageReferenceType,
         String validationReferenceType) {
      Objects.requireNonNull(transactionReport, "transactionReport");
      this.result = nameOf(transactionReport.getResult());
      this.transaction = transactionReport.getTransaction();
      this.standards = copy(transactionReport.getStandards());
      this.initiator = transactionReport.getInitiator() == null ? null : new RoleDTO(transactionReport.getInitiator());
      this.responder = transactionReport.getResponder() == null ? null : new RoleDTO(transactionReport.getResponder());
      this.messageReferences = buildMessageReferences(transactionReport.getMessages(), messageReferenceType);
      this.validationReportReferences = buildValidationReferences(transactionReport.getValidationReports(),
            validationReferenceType);
      this.unexpectedErrors = buildUnexpectedErrors(transactionReport.getUnexpectedErrors());
   }

   @JsonGetter("result")
   public String getResult() {
      return result;
   }

   @JsonGetter("transaction")
   public String getTransaction() {
      return transaction;
   }

   @JsonGetter("standards")
   public List<String> getStandards() {
      return standards;
   }

   @JsonGetter("initiator")
   public RoleDTO getInitiator() {
      return initiator;
   }

   @JsonGetter("responder")
   public RoleDTO getResponder() {
      return responder;
   }

   @JsonGetter("messageReferences")
   public List<MessageReferenceDTO> getMessageReferences() {
      return messageReferences;
   }

   @JsonGetter("validationReportReferences")
   public List<ValidationReferenceDTO> getValidationReportReferences() {
      return validationReportReferences;
   }

   @JsonGetter("unexpectedErrors")
   public List<UnexpectedErrorDTO> getUnexpectedErrors() {
      return unexpectedErrors;
   }

   private List<String> copy(List<String> values) {
      return values == null ? List.of() : List.copyOf(values);
   }

   private List<MessageReferenceDTO> buildMessageReferences(List<Message> messages, String referenceType) {
      if (messages == null || messages.isEmpty()) {
         return List.of();
      }
      return messages.stream()
            .map(message -> new MessageReferenceDTO(message,
                  resolve(message.getName(), DEFAULT_MESSAGE_REFERENCE),
                  referenceType))
            .toList();
   }

   private List<ValidationReferenceDTO> buildValidationReferences(List<ValidationReport> validations, String referenceType) {
      if (validations == null || validations.isEmpty()) {
         return List.of();
      }
      return validations.stream()
            .map(validation -> new ValidationReferenceDTO(validation,
                  resolve(validation.getSubject(), DEFAULT_VALIDATION_REFERENCE),
                  referenceType))
            .toList();
   }

   private List<UnexpectedErrorDTO> buildUnexpectedErrors(List<? extends net.ihe.gazelle.errorhandling.business.UnexpectedError> errors) {
      if (errors == null || errors.isEmpty()) {
         return List.of();
      }
      return errors.stream()
            .map(UnexpectedErrorDTO::new)
            .toList();
   }

   private String nameOf(Result value) {
      return value == null ? null : value.name();
   }
}
