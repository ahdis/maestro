package net.ihe.gazelle.maestro.simulation.step.record.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import net.ihe.gazelle.simulation.business.callback.Result;
import net.ihe.gazelle.simulation.business.callback.TransactionReport;

import static net.ihe.gazelle.maestro.simulation.step.record.ReferenceNames.resolve;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
      "transaction",
      "result",
      "referenceName",
      "referenceType"
})
public class TransactionReferenceDTO {

   private static final String DEFAULT_REFERENCE = "transaction";
   private static final String ITEM_TYPE_TRANSACTION = "SIMULATION_TRANSACTION";

   private final String transaction;
   private final String result;
   private final String referenceName;
   private final String referenceType;

   public TransactionReferenceDTO(TransactionReport transactionReport) {
      this.transaction = transactionReport.getTransaction();
      this.result = nameOf(transactionReport.getResult());
      this.referenceName = resolve(transactionReport.getTransaction(), DEFAULT_REFERENCE);
      this.referenceType = ITEM_TYPE_TRANSACTION;
   }

   @JsonGetter("transaction")
   public String getTransaction() {
      return transaction;
   }

   @JsonGetter("result")
   public String getResult() {
      return result;
   }

   @JsonGetter("referenceName")
   public String getReferenceName() {
      return referenceName;
   }

   @JsonGetter("referenceType")
   public String getReferenceType() {
      return referenceType;
   }

   private String nameOf(Result value) {
      return value == null ? null : value.name();
   }
}
