/*
 * Copyright 2025-2026 IHE International.
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
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.simulation.step.business.SimulationStepDefinition;
import net.ihe.gazelle.maestro.spi.business.recording.StepOutputPlan;
import net.ihe.gazelle.maestro.spi.business.recording.plan.FutureReferencePlan;
import net.ihe.gazelle.maestro.spi.business.recording.plan.ItemPlan;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import net.ihe.gazelle.modelmarshaller.technical.marshalling.MarshallingException;
import net.ihe.gazelle.modelmarshaller.technical.marshalling.TextMarshaller;
import net.ihe.gazelle.modelmarshaller.technical.serialization.TextSerDes;
import net.ihe.gazelle.simulation.business.callback.*;
import net.ihe.gazelle.simulation.jaxrs.api.technical.dto.callback.SimulationReportDTO;
import net.ihe.gazelle.validation.v2.api.business.Input;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimulationStepOutputDissectorTest {

   private static final TextSerDes SER_DES = new JacksonSerDes();
   private final SimulationStepOutputDissector dissector = new SimulationStepOutputDissector();

   @Test
   void shouldPersistValidationReportAsSubItemWithAttachmentsAndMutations() {
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
            .setTransaction("iti-42")
            .setResult(Result.DONE)
            .setInitiator(new Role().setName("init"))
            .setResponder(new Role().setName("resp"))
            .setValidationReports(List.of(simValidation));
      SimulationReport simulationReport = new SimulationReport()
            .setDateTime(Instant.now())
            .setResult(Result.DONE)
            .setTransactionReports(List.of(transactionReport));

      StepRunReport step = new StepRunReport()
            .addOutput(new ByteArrayProperty(SimulationStepDefinition.REPORT, simulationBytes(simulationReport)));
      Fixture fixture = newFixture(step);

      dissector.dissect(fixture.stepOutputPlan());

      assertEquals(1, fixture.rootPlan().futureReferencePlans().size());
      @SuppressWarnings("unchecked")
      ItemPlan<SimulationReport> simulationPlan =
            (ItemPlan<SimulationReport>) fixture.rootPlan().futureReferencePlans().getFirst().referencePlan().getTarget();

      assertEquals(1, simulationPlan.futureReferencePlans().size());
      FutureReferencePlan<SimulationReport> validationRef = simulationPlan.futureReferencePlans().getFirst();
      @SuppressWarnings("unchecked")
      ItemPlan<net.ihe.gazelle.validation.v2.api.business.report.ValidationReport> validationPlan =
            (ItemPlan<net.ihe.gazelle.validation.v2.api.business.report.ValidationReport>)
                  validationRef.referencePlan().getTarget();

      assertEquals(1, validationPlan.futureReferencePlans().size());
      validationPlan.futureReferencePlans().getFirst()
            .mutation()
            .mutateReference(validationPlan.itemObject(), "input-1", "att-1");

      Input mutatedInput = validationPlan.itemObject().getInputs().getFirst();
      assertNull(mutatedInput.getContent());
      assertEquals("att-1", mutatedInput.getItemId());
      assertEquals("/attachments/att-1", mutatedInput.getLocation());

      validationRef.mutation().mutateReference(simulationReport, validationRef.referencePlan().getName(), "itm-2");
      ValidationReport mutatedSimValidation = simulationReport.getTransactionReports().getFirst()
            .getValidationReports().getFirst();
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

      StepRunReport step = new StepRunReport()
            .addOutput(new ByteArrayProperty(SimulationStepDefinition.REPORT, simulationBytes(simulationReport)));
      Fixture fixture = newFixture(step);

      dissector.dissect(fixture.stepOutputPlan());

      assertEquals(1, fixture.rootPlan().futureReferencePlans().size(), "Only simulation report should be referenced");
      @SuppressWarnings("unchecked")
      ItemPlan<SimulationReport> simulationPlan =
            (ItemPlan<SimulationReport>) fixture.rootPlan().futureReferencePlans().getFirst().referencePlan().getTarget();
      assertTrue(simulationPlan.futureReferencePlans().isEmpty());
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

      StepRunReport step = new StepRunReport()
            .addOutput(new ByteArrayProperty(SimulationStepDefinition.REPORT, simulationBytes(simulationReport)));
      Fixture fixture = newFixture(step);

      assertThrows(IllegalStateException.class, () -> dissector.dissect(fixture.stepOutputPlan()));
   }

   @Test
   void shouldReplaceOutputWithItemReferenceWhenPersisted() {
      SimulationReport simulationReport = new SimulationReport()
            .setDateTime(Instant.now())
            .setResult(Result.DONE)
            .setTransactionReports(List.of());

      StepRunReport step = new StepRunReport()
            .addOutput(new ByteArrayProperty(SimulationStepDefinition.REPORT, simulationBytes(simulationReport)));
      Fixture fixture = newFixture(step);

      dissector.dissect(fixture.stepOutputPlan());
      FutureReferencePlan<TestReport> reportReference = fixture.rootPlan().futureReferencePlans().getFirst();
      reportReference.mutation().mutateReference(new TestReport(), reportReference.referencePlan().getName(), "itm-1");

      ByteArrayItemProperty output = (ByteArrayItemProperty) step.getOutputs().getFirst();
      assertEquals("http://dh/items/itm-1", output.getReference());
      assertNull(output.getValue());
      assertEquals("SIMULATION_REPORT", output.getItemType());
   }

   private Fixture newFixture(StepRunReport stepRunReport) {
      ItemPlan<TestReport> rootPlan = new ItemPlan<>("TEST_REPORT", new TestReport(), new TestReportMarshaller());
      StepOutputPlan stepOutputPlan = new StepOutputPlan(rootPlan, stepRunReport, "http://dh");
      return new Fixture(rootPlan, stepOutputPlan);
   }

   private byte[] simulationBytes(SimulationReport simulationReport) {
      return SER_DES.serializeAsString(new SimulationReportDTO(simulationReport)).getBytes();
   }

   private record Fixture(ItemPlan<TestReport> rootPlan, StepOutputPlan stepOutputPlan) {
   }

   private static final class TestReportMarshaller implements TextMarshaller<TestReport> {
      @Override
      public String marshallAsString(TestReport report) throws MarshallingException {
         return "{}";
      }

      @Override
      public TestReport unmarshall(String payload) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte[] marshall(TestReport report) throws MarshallingException {
         return marshallAsString(report).getBytes();
      }

      @Override
      public TestReport unmarshall(byte[] payload) {
         throw new UnsupportedOperationException();
      }
   }
}
