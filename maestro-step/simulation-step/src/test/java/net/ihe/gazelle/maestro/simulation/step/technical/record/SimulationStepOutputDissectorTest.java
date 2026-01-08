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

import net.ihe.gazelle.maestro.api.business.property.ByteArrayItemProperty;
import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.simulation.step.business.SimulationStepDefinition;
import net.ihe.gazelle.maestro.spi.business.recording.*;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import net.ihe.gazelle.modelmarshaller.technical.serialization.TextSerDes;
import net.ihe.gazelle.security.business.acl.AccessControlList;
import net.ihe.gazelle.simulation.business.callback.*;
import net.ihe.gazelle.simulation.jaxrs.api.technical.dto.callback.SimulationReportDTO;
import net.ihe.gazelle.validation.v2.api.business.Input;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SimulationStepOutputDissectorTest {

   private static final TextSerDes SER_DES = new JacksonSerDes();
   private final SimulationStepOutputDissector dissector = new SimulationStepOutputDissector();

   @Test
   void shouldPersistValidationReportAsSubItemWithAttachmentsAndMutations() {
      // Prepare validation report v2 with an input payload
      net.ihe.gazelle.validation.v2.api.business.report.ValidationReport validationReportV2 =
            new net.ihe.gazelle.validation.v2.api.business.report.ValidationReport()
            .addInput(new Input().setId("input-1").setContent("data".getBytes()));
      byte[] validationContent = SER_DES.serializeAsString(
            new net.ihe.gazelle.validation.v2.api.technical.dto.report.ValidationReportDTO(validationReportV2))
            .getBytes();

      ValidationReport simValidation = new ValidationReport()
            .setValidationProfileId("profile")
            .setValidationResult(ValidationResult.PASSED)
            .setContent(validationContent);

      TransactionReport transactionReport = new TransactionReport()
            .setResult(Result.DONE)
            .setInitiator(new Role().setName("init"))
            .setResponder(new Role().setName("resp"))
            .setValidationReports(List.of(simValidation));
      SimulationReport simulationReport = new SimulationReport()
            .setDateTime(Instant.now())
            .setResult(Result.DONE)
            .setTransactionReports(List.of(transactionReport));

      byte[] simulationBytes = SER_DES.serializeAsString(new SimulationReportDTO(simulationReport)).getBytes();
      StepRunReport step = new StepRunReport()
            .addOutput(new ByteArrayProperty(SimulationStepDefinition.REPORT, simulationBytes));

      StepOutputProjection projection = dissector.dissect(new StepOutputContext(step, new AccessControlList()));

      // Items: validation sub-item + simulation root
      assertEquals(2, projection.items().size());
      ItemPlan validationItem = projection.items().getFirst();
      ItemPlan simulationItem = projection.items().getLast();
      assertEquals("VALIDATION_REPORT", validationItem.item().type());
      assertEquals("SIMULATION_REPORT", simulationItem.item().type());

      assertEquals(1, projection.propertyMutations().size());
      PropertyMutation outputMutation = projection.propertyMutations().getFirst();
      assertEquals(0, outputMutation.outputIndex());
      assertEquals(simulationItemIndex(projection), outputMutation.sourceItemIndex());
      assertReplacementItemType(outputMutation, "SIMULATION_REPORT");

      // Attachments: validation input content
      assertEquals(1, projection.attachments().size());
      AttachmentPlan attachmentPlan = projection.attachments().getFirst();
      assertEquals("input-1", attachmentPlan.referenceName());
      assertEquals("VALIDATION_INPUT", attachmentPlan.referenceType());

      // Validation content mutation sets attachment references
      ContentMutationContext validationCtx = new ContentMutationContext(
            null,
            Map.of("input-1", "att-1"),
            "http://dh");
      String mutatedValidationContent = validationItem.contentMutator().mutate(validationItem.item().content(), validationCtx);
      net.ihe.gazelle.validation.v2.api.business.report.ValidationReport mutatedValidation =
            SER_DES.deserialize(mutatedValidationContent.getBytes(),
                        net.ihe.gazelle.validation.v2.api.technical.dto.report.ValidationReportDTO.class)
                  .getBusinessObject();
      Input mutatedInput = mutatedValidation.getInputs().getFirst();
      assertNull(mutatedInput.getContent());
      assertEquals("att-1", mutatedInput.getItemId());
      assertEquals("/attachments/att-1", mutatedInput.getLocation());

      // Simulation content mutation replaces validation report content with reference
      ContentMutationContext simCtx = new ContentMutationContext(
            Map.of("validation-report-1", "itm-2"),
            Map.of("input-1", "att-1"),
            "http://dh");
      String mutatedSimulationContent = simulationItem.contentMutator().mutate(simulationItem.item().content(), simCtx);
      SimulationReport mutatedSim = SER_DES.deserialize(mutatedSimulationContent.getBytes(), SimulationReportDTO.class).getBusinessObject();
      ValidationReport mutatedSimValidation = mutatedSim.getTransactionReports().getFirst().getValidationReports().getFirst();
      assertNull(mutatedSimValidation.getContent());
      assertEquals("http://dh/items/itm-2", mutatedSimValidation.getReference());
   }

   @Test
   void shouldSkipValidationReportWhenOnlyReferenceProvided() {
      ValidationReport simValidation = new ValidationReport()
            .setValidationResult(ValidationResult.PASSED)
            .setReference("http://existing");

      TransactionReport tx = new TransactionReport()
            .setResult(Result.DONE)
            .setInitiator(new Role().setName("init"))
            .setResponder(new Role().setName("resp"))
            .setValidationReports(List.of(simValidation));
      SimulationReport simulationReport = new SimulationReport()
            .setDateTime(Instant.now())
            .setResult(Result.DONE)
            .setTransactionReports(List.of(tx));

      byte[] simulationBytes = SER_DES.serializeAsString(new SimulationReportDTO(simulationReport)).getBytes();
      StepRunReport step = new StepRunReport()
            .addOutput(new ByteArrayProperty(SimulationStepDefinition.REPORT, simulationBytes));

      StepOutputProjection projection = dissector.dissect(new StepOutputContext(step, new AccessControlList()));

      assertEquals(1, projection.items().size(), "Only simulation report item should be present");
      assertTrue(projection.attachments().isEmpty());
      assertEquals(1, projection.propertyMutations().size());
   }

   @Test
   void shouldRaiseUnexpectedErrorWhenValidationReportHasNoContentAndNoReference() {
      ValidationReport simValidation = new ValidationReport()
            .setValidationResult(ValidationResult.PASSED);

      TransactionReport tx = new TransactionReport()
            .setResult(Result.DONE)
            .setInitiator(new Role().setName("init"))
            .setResponder(new Role().setName("resp"))
            .setValidationReports(List.of(simValidation));
      SimulationReport simulationReport = new SimulationReport()
            .setDateTime(Instant.now())
            .setResult(Result.DONE)
            .setTransactionReports(List.of(tx));

      byte[] simulationBytes = SER_DES.serializeAsString(new SimulationReportDTO(simulationReport)).getBytes();
      StepRunReport step = new StepRunReport()
            .addOutput(new ByteArrayProperty(SimulationStepDefinition.REPORT, simulationBytes));

      assertThrows(IllegalStateException.class,
            () -> dissector.dissect(new StepOutputContext(step, new AccessControlList())));
   }

   private void assertReplacementItemType(PropertyMutation mutation, String expectedItemType) {
      assertNotNull(mutation.factory());
      var property = mutation.factory().create("item-1", "http://dh/items/item-1");
      assertInstanceOf(ByteArrayItemProperty.class, property);
      ByteArrayItemProperty itemProperty = (ByteArrayItemProperty) property;
      assertEquals(expectedItemType, itemProperty.getItemType());
      assertEquals("http://dh/items/item-1", itemProperty.getReference());
      assertNull(itemProperty.getValue());
   }

   private int simulationItemIndex(StepOutputProjection projection) {
      for (int i = 0; i < projection.items().size(); i++) {
         if ("SIMULATION_REPORT".equals(projection.items().get(i).item().type())) {
            return i;
         }
      }
      throw new AssertionError("Simulation report item not found");
   }
}
