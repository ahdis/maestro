package net.ihe.gazelle.maestro.simulation.step.record;

import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.*;
import net.ihe.gazelle.maestro.simulation.step.business.SimulationStepDefinition;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import net.ihe.gazelle.modelmarshaller.technical.serialization.TextSerDes;
import net.ihe.gazelle.security.business.acl.AccessControlList;
import net.ihe.gazelle.simulation.business.callback.SimulationReport;
import net.ihe.gazelle.simulation.business.callback.TransactionReport;
import net.ihe.gazelle.simulation.business.callback.ValidationReport;
import net.ihe.gazelle.simulation.business.callback.Result;
import net.ihe.gazelle.simulation.business.callback.Role;
import net.ihe.gazelle.simulation.business.callback.ValidationResult;
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
}
