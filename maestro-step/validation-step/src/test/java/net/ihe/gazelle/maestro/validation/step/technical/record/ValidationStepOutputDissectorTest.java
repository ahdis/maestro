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
import net.ihe.gazelle.maestro.api.business.testreport.TestRunReport;
import net.ihe.gazelle.maestro.spi.business.recording.plan.AttachmentPlan;
import net.ihe.gazelle.maestro.spi.business.recording.StepOutputPlan;
import net.ihe.gazelle.maestro.spi.business.recording.StepOutputPlanContext;
import net.ihe.gazelle.maestro.spi.business.recording.plan.FutureReferencePlan;
import net.ihe.gazelle.maestro.spi.business.recording.plan.ItemPlan;
import net.ihe.gazelle.maestro.spi.business.recording.plan.MaestroRefType;
import net.ihe.gazelle.maestro.validation.step.business.ValidationStepDefinition;
import net.ihe.gazelle.validation.v2.api.business.Input;
import net.ihe.gazelle.validation.v2.api.business.report.ValidationReport;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class ValidationStepOutputDissectorTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final ValidationStepOutputDissector dissector = new ValidationStepOutputDissector();

    @Test
    void shouldReturnEmptyPlanWhenNoValidationReportProperty() {
        StepRunReport step = new StepRunReport();
        StepOutputPlan plan = dissector.dissect(new StepOutputPlanContext(step, null, "http://dh"));

        assertNotNull(plan);
        assertTrue(plan.reportReferencePlans().isEmpty());
    }

    @Test
    void shouldCreateReportItemAndAttachmentsForInputsWithContent() throws Exception {
        SampleReport sample = sampleReport();
        StepRunReport step = new StepRunReport()
                .addOutput(new ByteArrayProperty(ValidationStepDefinition.REPORT, sample.serializedReport()));

        StepOutputPlan plan = dissector.dissect(new StepOutputPlanContext(step, null, "http://dh"));

        assertEquals(1, plan.reportReferencePlans().size(), "One report reference expected");
        FutureReferencePlan<TestReport> reportReference = plan.reportReferencePlans().getFirst();
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
        TestRunReport run = new TestRunReport().addStepRunReport(step);
        TestReport report = new TestReport().addTestRunReport(run);

        StepOutputPlan plan = dissector.dissect(new StepOutputPlanContext(step, null, "http://dh"));
        FutureReferencePlan<TestReport> reportReference = plan.reportReferencePlans().getFirst();

        reportReference.mutation().mutateReference(report, reportReference.referencePlan().getName(), "itm-1");

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

        StepOutputPlan plan = dissector.dissect(new StepOutputPlanContext(step, null, "http://dh"));
        @SuppressWarnings("unchecked")
        ItemPlan<ValidationReport> reportPlan =
              (ItemPlan<ValidationReport>) plan.reportReferencePlans().getFirst().referencePlan().getTarget();

        FutureReferencePlan<ValidationReport> attachmentRef = reportPlan.futureReferencePlans().getFirst();
        attachmentRef.mutation().mutateReference(reportPlan.itemObject(), attachmentRef.referencePlan().getName(), "att-42");

        ValidationReport mutated = reportPlan.itemObject();
        Input input = mutated.getInputs().getFirst();
        assertEquals("att-42", input.getItemId());
        assertEquals("/attachments/att-42", input.getLocation());
        assertNull(input.getContent());
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

        byte[] serialized = OBJECT_MAPPER.writeValueAsBytes(report);
        return new SampleReport(serialized, payload);
    }

    private record SampleReport(byte[] serializedReport, byte[] inputPayload) {
        String encodedPayload() {
            return Base64.getEncoder().encodeToString(inputPayload);
        }
    }
}
