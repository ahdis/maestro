/*
 * Copyright 2025 Kereval.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ihe.gazelle.maestro.simulation.step.technical.record;

import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.testreport.ByteArrayItemProperty;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.simulation.step.business.SimulationStepDefinition;
import net.ihe.gazelle.maestro.spi.business.recording.*;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import net.ihe.gazelle.modelmarshaller.technical.serialization.TextSerDes;
import net.ihe.gazelle.simulation.business.callback.SimulationReport;
import net.ihe.gazelle.simulation.business.callback.TransactionReport;
import net.ihe.gazelle.simulation.business.callback.ValidationReport;
import net.ihe.gazelle.simulation.jaxrs.api.technical.dto.callback.SimulationReportDTO;
import net.ihe.gazelle.validation.v2.api.business.Input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Structures the simulation step report into a set of hierarchical Datahouse items.
 */
class SimulationStepOutputDissector implements StepOutputDissector {

    private static final TextSerDes serDes = new JacksonSerDes();

   private static final String ITEM_TYPE_SIMULATION_REPORT = "SIMULATION_REPORT";
   private static final String ITEM_TYPE_VALIDATION_REPORT = "VALIDATION_REPORT";
   private static final String VALIDATION_INPUT_TYPE = "VALIDATION_INPUT";

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


      ByteArrayProperty reportProperty = (ByteArrayProperty) outputs.stream()
              .filter(output -> SimulationStepDefinition.REPORT.equals(output.getName()))
              .findFirst()
              .orElseThrow(() -> new IllegalStateException("Simulation report output property not found"));
      SimulationReport simulationReport = deserializeReport(reportProperty);

      StepOutputProjectionBuilder graph = new StepOutputProjectionBuilder();
      MaestroItem simulationReportItem = createReportItem(context, simulationReport);
      ItemOutputBuilder simulationReportBuilder = graph.addItem(simulationReportItem);
      //This reference will be added to the enclosing TestRunReport item later on
      simulationReportBuilder.addReferenceBinding(new ReferenceBinding(
              SimulationStepDefinition.REPORT,
              ITEM_TYPE_SIMULATION_REPORT,
              MaestroRefType.ITEM_ID
      ));

      // Replace the original byte[] output with a reference to the persisted report hierarchy.
      PropertyReferenceFactory factory = (itemId, itemUrl) -> {
         ByteArrayItemProperty replacement = new ByteArrayItemProperty(reportProperty);
         replacement.setValue(null);
         return replacement.setReference(itemUrl);
      };
      simulationReportBuilder.addPropertyReplacement(SimulationStepDefinition.REPORT, outputs, factory);

      addValidationReportSubItems(simulationReportBuilder, context, simulationReport);
      simulationReportBuilder.mutateContent(this::mutateSimulationReportContent);

      return graph.build();
   }

   private void addValidationReportSubItems(ItemOutputBuilder simulationReportBuilder,
                                            StepOutputContext context,
                                            SimulationReport simulationReport) {
      if (simulationReport.getTransactionReports() == null) {
         return;
      }
      int refCounter = 0;
      for (TransactionReport transaction : simulationReport.getTransactionReports()) {
         if (transaction.getValidationReports() == null) {
            continue;
         }
         for (ValidationReport validationReport : transaction.getValidationReports()) {
            if (validationReport.getContent() == null || validationReport.getContent().length == 0) {
               continue; // skip when only reference is present
            }
            net.ihe.gazelle.validation.v2.api.business.report.ValidationReport validationReportV2 =
                  deserializeValidationReportV2(validationReport.getContent());
            List<AttachmentOutputBuilder> attachments = extractValidationInputAttachments(validationReportV2);
            String refName = "validation-report-" + ++refCounter;

            MaestroItem validationItem = new MaestroItem(
                  context.accessControlList(),
                  ITEM_TYPE_VALIDATION_REPORT,
                  serializeValidationReport(validationReportV2),
                  new ArrayList<>(), // references are not yet known
                  new HashMap<>()
            );

            SubItemOutputBuilder validationBuilder = simulationReportBuilder.addSubItem(
                  validationItem,
                  refName,
                  ITEM_TYPE_VALIDATION_REPORT,
                  MaestroRefType.ITEM_ID);
            validationBuilder.addAttachments(attachments);
            validationBuilder.mutateContent(this::mutateValidationReportContent);
         }
      }
   }

   private List<AttachmentOutputBuilder> extractValidationInputAttachments(
         net.ihe.gazelle.validation.v2.api.business.report.ValidationReport validationReport) {
      List<AttachmentOutputBuilder> plans = new ArrayList<>();
      if (validationReport.getInputs() == null) {
         return plans;
      }
      for (Input input : validationReport.getInputs()) {
         if (input.getContent() != null) {
            plans.add(createValidationInputAttachment(input));
            input.setContent(null);
         }
      }
      return plans;
   }

   private AttachmentOutputBuilder createValidationInputAttachment(Input input) {
      MaestroAttachment attachment = new MaestroAttachment(
            input.getContent(),
            VALIDATION_INPUT_TYPE,
            input.getId()
      );
      return new AttachmentOutputBuilder(attachment)
            .withReference(resolveInputReferenceName(input.getId()), VALIDATION_INPUT_TYPE);
   }

   private String mutateValidationReportContent(String content, ContentMutationContext context) {
      net.ihe.gazelle.validation.v2.api.business.report.ValidationReport validationReport =
            deserializeValidationReportContent(content);
      if (validationReport.getInputs() != null) {
         for (Input input : validationReport.getInputs()) {
            String refName = resolveInputReferenceName(input.getId());
            String attachmentId = context.findAttachmentId(refName);
            if (attachmentId != null) {
               input.setItemId(attachmentId);
               input.setLocation("/attachments/" + attachmentId);
               input.setContent(null);
            }
         }
      }
      return serializeValidationReport(validationReport);
   }

   private String mutateSimulationReportContent(String content, ContentMutationContext context) {
      SimulationReportDTO dto = serDes.deserialize(content.getBytes(), SimulationReportDTO.class);
      SimulationReport report = dto.getBusinessObject();
      if (report.getTransactionReports() != null) {
         int refCounter = 0;
         for (TransactionReport transaction : report.getTransactionReports()) {
            if (transaction.getValidationReports() == null) {
               continue;
            }
            for (ValidationReport validationReport : transaction.getValidationReports()) {
               String refName = "validation-report-" + ++refCounter;
               String itemId = context.findItemId(refName);
               if (itemId != null) {
                  validationReport.setReference(context.datahouseUrl() + "/items/" + itemId);
                  validationReport.setContent(null);
               }
            }
         }
      }
      return serDes.serializeAsString(new SimulationReportDTO(report));
   }

   private MaestroItem createReportItem(StepOutputContext context, SimulationReport simulationReport) {
      SimulationReportDTO dto = new SimulationReportDTO(simulationReport);
      return createItem(context, dto);
   }

   // Reads the binary report output and converts it to the SimulationReport business object.
   private SimulationReport deserializeReport(ByteArrayProperty reportProperty) {
      byte[] value = reportProperty.getValue();
      if (value == null || value.length == 0) {
         throw new IllegalStateException("Simulation report output is empty");
      }
       return serDes.deserialize(value, SimulationReportDTO.class).getBusinessObject();
   }

   private net.ihe.gazelle.validation.v2.api.business.report.ValidationReport deserializeValidationReportV2(byte[] content) {
      return serDes.deserialize(content, net.ihe.gazelle.validation.v2.api.technical.dto.report.ValidationReportDTO.class)
            .getBusinessObject();
   }

   private net.ihe.gazelle.validation.v2.api.business.report.ValidationReport deserializeValidationReportContent(String content) {
      return serDes.deserialize(content.getBytes(),
                  net.ihe.gazelle.validation.v2.api.technical.dto.report.ValidationReportDTO.class)
            .getBusinessObject();
   }

   private String serializeValidationReport(
         net.ihe.gazelle.validation.v2.api.business.report.ValidationReport validationReport) {
      return serDes.serializeAsString(
            new net.ihe.gazelle.validation.v2.api.technical.dto.report.ValidationReportDTO(validationReport));
   }

   // Creates a bare Item with the correct ACL, type, serialized payload, and empty references list.
   private MaestroItem createItem(StepOutputContext context, Object payload) {
      return new MaestroItem(
            context.accessControlList(),
            ITEM_TYPE_SIMULATION_REPORT,
            serialize(payload),
            new ArrayList<>(),
            new HashMap<>()
      );
   }

   // Serializes DTOs to JSON for persistence in Datahouse.
   private String serialize(Object payload) {
       return serDes.serializeAsString(payload);
   }

   private String resolveInputReferenceName(String candidate) {
      if (candidate == null || candidate.isBlank()) {
         return "validation-input";
      }
      return candidate;
   }

}
