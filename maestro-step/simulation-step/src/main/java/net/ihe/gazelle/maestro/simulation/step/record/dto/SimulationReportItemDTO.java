package net.ihe.gazelle.maestro.simulation.step.record.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import net.ihe.gazelle.errorhandling.technical.UnexpectedErrorDTO;
import net.ihe.gazelle.simulation.business.callback.Result;
import net.ihe.gazelle.simulation.business.callback.SimulationReport;
import net.ihe.gazelle.simulation.business.callback.TransactionReport;
import net.ihe.gazelle.simulation.business.setup.Parameter;
import net.ihe.gazelle.simulation.jaxrs.api.technical.dto.setup.ParameterDTO;

import java.util.List;
import java.util.Objects;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
      "uuid",
      "dateTime",
      "result",
      "serviceName",
      "serviceVersion",
      "sequenceId",
      "transactionReportReferences",
      "simulationParameters",
      "unexpectedErrors"
})
public class SimulationReportItemDTO {

   private final String uuid;
   private final Instant dateTime;
   private final String result;
   private final String serviceName;
   private final String serviceVersion;
   private final String sequenceId;
   private final List<TransactionReferenceDTO> transactionReportReferences;
   private final List<ParameterDTO> simulationParameters;
   private final List<UnexpectedErrorDTO> unexpectedErrors;

   public SimulationReportItemDTO(SimulationReport simulationReport) {
      Objects.requireNonNull(simulationReport, "simulationReport");
      this.uuid = simulationReport.getUuid();
      this.dateTime = simulationReport.getDateTime();
      this.result = nameOf(simulationReport.getResult());
      this.serviceName = simulationReport.getServiceName();
      this.serviceVersion = simulationReport.getServiceVersion();
      this.sequenceId = simulationReport.getSequenceId();
      this.transactionReportReferences = buildTransactionReferences(simulationReport.getTransactionReports());
      this.simulationParameters = buildParameters(simulationReport.getSimulationParameters());
      this.unexpectedErrors = buildUnexpectedErrors(simulationReport.getUnexpectedErrors());
   }

   @JsonGetter("uuid")
   public String getUuid() {
      return uuid;
   }

   @JsonGetter("dateTime")
   public Instant getDateTime() {
      return dateTime;
   }

   @JsonGetter("result")
   public String getResult() {
      return result;
   }

   @JsonGetter("serviceName")
   public String getServiceName() {
      return serviceName;
   }

   @JsonGetter("serviceVersion")
   public String getServiceVersion() {
      return serviceVersion;
   }

   @JsonGetter("sequenceId")
   public String getSequenceId() {
      return sequenceId;
   }

   @JsonGetter("transactionReportReferences")
   public List<TransactionReferenceDTO> getTransactionReportReferences() {
      return transactionReportReferences;
   }

   @JsonGetter("simulationParameters")
   public List<ParameterDTO> getSimulationParameters() {
      return simulationParameters;
   }

   @JsonGetter("unexpectedErrors")
   public List<UnexpectedErrorDTO> getUnexpectedErrors() {
      return unexpectedErrors;
   }

   private List<TransactionReferenceDTO> buildTransactionReferences(List<TransactionReport> transactionReports) {
      if (transactionReports == null || transactionReports.isEmpty()) {
         return List.of();
      }
      return transactionReports.stream()
            .map(TransactionReferenceDTO::new)
            .toList();
   }

   private List<ParameterDTO> buildParameters(List<Parameter> parameters) {
      if (parameters == null || parameters.isEmpty()) {
         return List.of();
      }
      return parameters.stream()
            .map(ParameterDTO::new)
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
