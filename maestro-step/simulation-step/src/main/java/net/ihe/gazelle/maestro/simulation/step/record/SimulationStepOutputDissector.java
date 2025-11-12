package net.ihe.gazelle.maestro.simulation.step.record;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ihe.gazelle.errorhandling.technical.UnexpectedErrorDTO;
import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.recording.MaestroItem;
import net.ihe.gazelle.maestro.api.business.recording.MaestroRefType;
import net.ihe.gazelle.maestro.api.business.testreport.ByteArrayItemProperty;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.*;
import net.ihe.gazelle.maestro.recording.DatahouseMaestroItem;
import net.ihe.gazelle.maestro.simulation.step.business.SimulationStepDefinition;
import net.ihe.gazelle.simulation.business.callback.Message;
import net.ihe.gazelle.simulation.business.callback.SimulationReport;
import net.ihe.gazelle.simulation.business.callback.TransactionReport;
import net.ihe.gazelle.simulation.business.callback.ValidationReport;
import net.ihe.gazelle.simulation.jaxrs.api.technical.dto.callback.MessageDTO;
import net.ihe.gazelle.simulation.jaxrs.api.technical.dto.callback.SimulationReportDTO;
import net.ihe.gazelle.simulation.jaxrs.api.technical.dto.callback.TransactionReportDTO;
import net.ihe.gazelle.simulation.jaxrs.api.technical.dto.callback.ValidationReportDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Structures the simulation step report into a set of hierarchical Datahouse items.
 */
class SimulationStepOutputDissector implements StepOutputDissector {

   // Shared mapper for translating between DTOs, business objects, and stored JSON content.
   private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

   // Item type constants understood by the Datahouse persistence layer.
   private static final String ITEM_TYPE_REPORT = "SIMULATION_REPORT";
   private static final String ITEM_TYPE_TRANSACTION = "SIMULATION_TRANSACTION";
   private static final String ITEM_TYPE_MESSAGE = "SIMULATION_MESSAGE";
   private static final String ITEM_TYPE_VALIDATION = "SIMULATION_VALIDATION_REPORT";
   private static final String ITEM_TYPE_UNEXPECTED_ERROR = "SIMULATION_UNEXPECTED_ERROR";

   @Override
   public StepOutputProjection dissect(StepOutputContext context) {
      if (context == null || context.stepRunReport() == null) {
         return StepOutputProjection.empty();
      }
      StepRunReport stepRunReport = context.stepRunReport();
      List<Property> outputs = stepRunReport.getOutputs();
      if (outputs == null || outputs.isEmpty()) {
         return StepOutputProjection.empty();
      }

      int reportIndex = findReportOutputIndex(outputs);
      if (reportIndex < 0) {
         return StepOutputProjection.empty();
      }

      ByteArrayProperty reportProperty = (ByteArrayProperty) outputs.get(reportIndex);
      SimulationReport simulationReport = deserializeReport(reportProperty);

      StepOutputProjectionBuilder builder = new StepOutputProjectionBuilder();
      ItemHandle reportHandle = appendReportItem(context, simulationReport, builder);
      buildTransactionHierarchy(context, simulationReport, builder, reportHandle);
      appendUnexpectedErrorItems(context, simulationReport, builder, reportHandle);

      // Replace the original byte[] output with a reference to the persisted report hierarchy.
      PropertyReferenceFactory factory = (itemId, itemUrl) -> {
         ByteArrayItemProperty replacement = new ByteArrayItemProperty(reportProperty);
         replacement.setValue(null);
         return replacement.setReference(itemUrl);
      };
      builder.addPropertyMutation(reportIndex, reportHandle, factory);

      // Produce the projection: the builder handles ordering, bindings, and property mutations.
      return builder.build();
   }


   private void buildTransactionHierarchy(StepOutputContext context,
         SimulationReport simulationReport,
         StepOutputProjectionBuilder builder,
         ItemHandle reportHandle) {
      List<TransactionReport> transactionReports = simulationReport.getTransactionReports();
      if (transactionReports == null) {
         return;
      }
      for (TransactionReport transactionReport : transactionReports) {
         // Each transaction becomes a child of the simulation report.
         ItemHandle transactionHandle = builder.addItem(createTransactionItem(context, transactionReport));
         builder.linkParent(transactionHandle,
               reportHandle,
               referenceName(transactionReport.getTransaction(), "transaction"),
               ITEM_TYPE_TRANSACTION,
               MaestroRefType.ITEM_ID);

         Map<String, ItemHandle> messageHandleByName = new HashMap<>();
         List<Message> messages = transactionReport.getMessages();
         if (messages != null) {
            for (Message message : messages) {
               // Messages link to their transaction and (optionally) expose their name for validation lookups.
               ItemHandle messageHandle = builder.addItem(createMessageItem(context, message));
               builder.linkParent(messageHandle,
                     transactionHandle,
                     referenceName(message.getName(), "message"),
                     ITEM_TYPE_MESSAGE,
                     MaestroRefType.ITEM_ID);
               if (message.getName() != null && !message.getName().isBlank()) {
                  messageHandleByName.put(message.getName(), messageHandle);
               }
            }
         }

         List<ValidationReport> validationReports = transactionReport.getValidationReports();
         if (validationReports != null) {
            for (ValidationReport validation : validationReports) {
               // Every validation references its transaction or a specific message.
               ItemHandle validationHandle = builder.addItem(createValidationItem(context, validation));
               builder.linkParent(validationHandle,
                     transactionHandle,
                     referenceName(validation.getSubject(), "validation"),
                     ITEM_TYPE_VALIDATION,
                     MaestroRefType.ITEM_ID);

               if (validation.getSubject() != null) {
                  ItemHandle targetMessage = messageHandleByName.get(validation.getSubject());
                  if (targetMessage != null) {
                     builder.linkParent(validationHandle,
                           targetMessage,
                           validation.getSubject(),
                           ITEM_TYPE_VALIDATION,
                           MaestroRefType.ITEM_ID);
                  }
               }
            }
         }
      }
   }

   private void appendUnexpectedErrorItems(StepOutputContext context,
         SimulationReport simulationReport,
         StepOutputProjectionBuilder builder,
         ItemHandle reportHandle) {
      if (simulationReport.getUnexpectedErrors() == null) {
         return;
      }
      for (UnexpectedErrorDTO errorDTO : simulationReport.getUnexpectedErrors().stream()
            .map(UnexpectedErrorDTO::new)
            .toList()) {
         ItemHandle errorHandle = builder.addItem(createErrorItem(context, errorDTO));
         builder.linkParent(errorHandle,
               reportHandle,
               "unexpected-error",
               ITEM_TYPE_UNEXPECTED_ERROR,
               MaestroRefType.ITEM_ID);
      }
   }

   // Builds the top-level simulation report item and marks it as the property replacement target.
   private ItemHandle appendReportItem(StepOutputContext context,
         SimulationReport simulationReport,
         StepOutputProjectionBuilder builder) {
      MaestroItem reportItem = createReportItem(context, simulationReport);
      ItemHandle handle = builder.addItem(reportItem);
      builder.addReportBinding(handle, new ReportReferenceBinding(
            SimulationStepDefinition.REPORT,
            ITEM_TYPE_REPORT,
            MaestroRefType.ITEM_ID
      ));
      return handle;
   }

   private MaestroItem createReportItem(StepOutputContext context, SimulationReport simulationReport) {
      SimulationReportDTO dto = new SimulationReportDTO(simulationReport);
       return createItem(context, ITEM_TYPE_REPORT, dto);
   }

   private MaestroItem createTransactionItem(StepOutputContext context, TransactionReport transactionReport) {
      TransactionReportDTO dto = new TransactionReportDTO(transactionReport);
       return createItem(context, ITEM_TYPE_TRANSACTION, dto);
   }

   private MaestroItem createMessageItem(StepOutputContext context, Message message) {
      MessageDTO dto = new MessageDTO(message);
       return createItem(context, ITEM_TYPE_MESSAGE, dto);
   }

   private MaestroItem createValidationItem(StepOutputContext context, ValidationReport validationReport) {
      ValidationReportDTO dto = new ValidationReportDTO(validationReport);
       return createItem(context, ITEM_TYPE_VALIDATION, dto);
   }

   private MaestroItem createErrorItem(StepOutputContext context, UnexpectedErrorDTO errorDTO) {
      DatahouseMaestroItem item = createItem(context, ITEM_TYPE_UNEXPECTED_ERROR, errorDTO);
      if (errorDTO.getName() != null) {
         item.addAdditionalParameter("errorName", errorDTO.getName());
      }
      return item;
   }

   // Reads the binary report output and converts it to the SimulationReport business object.
   private SimulationReport deserializeReport(ByteArrayProperty reportProperty) {
      byte[] value = reportProperty.getValue();
      if (value == null || value.length == 0) {
         throw new IllegalStateException("Simulation report output is empty");
      }
      try {
         SimulationReportDTO dto = OBJECT_MAPPER.readValue(value, SimulationReportDTO.class);
         return dto.getBusinessObject();
      } catch (IOException e) {
         throw new IllegalStateException("Unable to parse simulation report output", e);
      }
   }

   // Creates a bare Item with the correct ACL, type, serialized payload, and empty references list.
   private DatahouseMaestroItem createItem(StepOutputContext context, String type, Object payload) {
      DatahouseMaestroItem item = new DatahouseMaestroItem()
            .setAccessControlList(context.accessControlList())
            .setType(type)
            .setContent(serialize(payload));
      item.setReferences(new ArrayList<>());
      return item;
   }

   // Serializes DTOs to JSON for persistence in Datahouse.
   private String serialize(Object payload) {
      try {
         return OBJECT_MAPPER.writeValueAsString(payload);
      } catch (IOException e) {
         throw new IllegalStateException("Unable to serialize payload of type " + payload.getClass(), e);
      }
   }

   // Finds the index of the REPORT output property so we know which property to mutate later.
   private int findReportOutputIndex(List<Property> outputs) {
      for (int i = 0; i < outputs.size(); i++) {
         Property property = outputs.get(i);
         if (property instanceof ByteArrayProperty && SimulationStepDefinition.REPORT.equals(property.getName())) {
            return i;
         }
      }
      return -1;
   }

   private String referenceName(String candidate, String fallback) {
      return (candidate == null || candidate.isBlank()) ? fallback : candidate;
   }
}
