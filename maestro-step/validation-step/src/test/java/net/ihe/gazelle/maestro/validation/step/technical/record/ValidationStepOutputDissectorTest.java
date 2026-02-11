/*
 * Copyright 2026 IHE International.
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

package net.ihe.gazelle.maestro.validation.step.technical.record;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ihe.gazelle.maestro.api.business.property.ByteArrayItemProperty;
import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.spi.business.recording.StepOutputPlan;
import net.ihe.gazelle.maestro.spi.business.recording.plan.AttachmentPlan;
import net.ihe.gazelle.maestro.spi.business.recording.plan.FutureReferencePlan;
import net.ihe.gazelle.maestro.spi.business.recording.plan.ItemPlan;
import net.ihe.gazelle.maestro.spi.business.recording.plan.MaestroRefType;
import net.ihe.gazelle.maestro.validation.step.business.ValidationStepDefinition;
import net.ihe.gazelle.modelmarshaller.technical.marshalling.MarshallingException;
import net.ihe.gazelle.modelmarshaller.technical.marshalling.TextMarshaller;
import net.ihe.gazelle.validation.v2.api.business.Input;
import net.ihe.gazelle.validation.v2.api.business.report.ValidationReport;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationStepOutputDissectorTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final ValidationStepOutputDissector dissector = new ValidationStepOutputDissector();

    @Test
    void shouldCreateNoReferenceWhenNoValidationReportProperty() {
        StepRunReport step = new StepRunReport();
        Fixture fixture = newFixture(step);

        dissector.dissect(fixture.stepOutputPlan());

        assertTrue(fixture.rootPlan().futureReferencePlans().isEmpty());
    }

    @Test
    void shouldCreateReportItemAndAttachmentsForInputsWithContent() throws Exception {
        SampleReport sample = sampleReport();
        StepRunReport step = new StepRunReport()
              .addOutput(new ByteArrayProperty(ValidationStepDefinition.REPORT, sample.serializedReport()));
        Fixture fixture = newFixture(step);

        dissector.dissect(fixture.stepOutputPlan());

        assertEquals(1, fixture.rootPlan().futureReferencePlans().size(), "One report reference expected");
        FutureReferencePlan<TestReport> reportReference = fixture.rootPlan().futureReferencePlans().getFirst();
        assertEquals(ValidationStepDefinition.REPORT, reportReference.referencePlan().getName());
        assertEquals(MaestroRefType.ITEM_ID, reportReference.referencePlan().getRefType());

        @SuppressWarnings("unchecked")
        ItemPlan<ValidationReport> reportPlan = (ItemPlan<ValidationReport>) reportReference.referencePlan().getTarget();
        assertEquals(ValidationStepDefinition.REPORT, reportPlan.type());

        assertEquals(1, reportPlan.futureReferencePlans().size(), "Only inputs with content should become attachments");
        ValidationReport report = reportPlan.itemObject();
        assertNotNull(report.getInputs());
        assertEquals(2, report.getInputs().size());
        assertNull(report.getInputs().getFirst().getContent());
        assertNull(report.getInputs().get(1).getContent());

        FutureReferencePlan<ValidationReport> attachmentRef = reportPlan.futureReferencePlans().getFirst();
        assertEquals("payload-input", attachmentRef.referencePlan().getName());
        assertArrayEquals(sample.inputPayload(),
              ((AttachmentPlan) attachmentRef.referencePlan().getTarget()).content());
    }

    @Test
    void shouldReplaceOutputWithItemReferenceWhenPersisted() throws Exception {
        SampleReport sample = sampleReport();
        StepRunReport step = new StepRunReport()
              .addOutput(new ByteArrayProperty(ValidationStepDefinition.REPORT, sample.serializedReport()));
        Fixture fixture = newFixture(step);

        dissector.dissect(fixture.stepOutputPlan());
        FutureReferencePlan<TestReport> reportReference = fixture.rootPlan().futureReferencePlans().getFirst();
        reportReference.mutation().mutateReference(new TestReport(), reportReference.referencePlan().getName(), "itm-1");

        ByteArrayItemProperty output = (ByteArrayItemProperty) step.getOutputs().getFirst();
        assertEquals("http://dh/items/itm-1", output.getReference());
        assertNull(output.getValue());
        assertEquals(ValidationStepDefinition.REPORT, output.getItemType());
    }

    @Test
    void shouldPopulateAttachmentIdsInReportDuringMutation() throws Exception {
        SampleReport sample = sampleReport();
        StepRunReport step = new StepRunReport()
              .addOutput(new ByteArrayProperty(ValidationStepDefinition.REPORT, sample.serializedReport()));
        Fixture fixture = newFixture(step);

        dissector.dissect(fixture.stepOutputPlan());
        @SuppressWarnings("unchecked")
        ItemPlan<ValidationReport> reportPlan =
              (ItemPlan<ValidationReport>) fixture.rootPlan().futureReferencePlans().getFirst().referencePlan().getTarget();

        FutureReferencePlan<ValidationReport> attachmentRef = reportPlan.futureReferencePlans().getFirst();
        attachmentRef.mutation().mutateReference(reportPlan.itemObject(), attachmentRef.referencePlan().getName(), "att-42");

        ValidationReport mutated = reportPlan.itemObject();
        Input input = mutated.getInputs().getFirst();
        assertEquals("att-42", input.getItemId());
        assertEquals("/attachments/att-42", input.getLocation());
        assertNull(input.getContent());
    }

    private Fixture newFixture(StepRunReport stepRunReport) {
        ItemPlan<TestReport> rootPlan = new ItemPlan<>("TEST_REPORT", new TestReport(), new TestReportMarshaller());
        StepOutputPlan stepOutputPlan = new StepOutputPlan(rootPlan, stepRunReport, "http://dh");
        return new Fixture(rootPlan, stepOutputPlan);
    }

    private SampleReport sampleReport() throws Exception {
        byte[] payload = "validation-input".getBytes(StandardCharsets.UTF_8);
        Input inputWithContent = new Input()
              .setId("payload-input")
              .setContent(payload);
        Input inputWithoutContent = new Input()
              .setId("metadata-input");

        ValidationReport report = new ValidationReport()
              .setUuid("validation-report-id")
              .addInput(inputWithContent)
              .addInput(inputWithoutContent);

        return new SampleReport(OBJECT_MAPPER.writeValueAsBytes(report), payload);
    }

    private record SampleReport(byte[] serializedReport, byte[] inputPayload) {
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
