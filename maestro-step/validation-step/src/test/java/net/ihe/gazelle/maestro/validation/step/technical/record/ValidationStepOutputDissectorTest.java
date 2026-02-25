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
import net.ihe.gazelle.maestro.api.business.property.PropertyBindingPayload;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.spi.business.recording.FutureReferenceCallbackRegistry;
import net.ihe.gazelle.maestro.spi.business.recording.StepOutputPlan;
import net.ihe.gazelle.maestro.spi.business.recording.plan.AttachmentPlan;
import net.ihe.gazelle.maestro.spi.business.recording.plan.FutureReferencePlan;
import net.ihe.gazelle.maestro.spi.business.recording.plan.ItemPlan;
import net.ihe.gazelle.maestro.spi.business.recording.plan.MaestroRefType;
import net.ihe.gazelle.maestro.spi.business.recording.plan.ReferencePlan;
import net.ihe.gazelle.maestro.validation.step.business.ValidationStepDefinition;
import net.ihe.gazelle.modelmarshaller.technical.marshalling.MarshallingException;
import net.ihe.gazelle.modelmarshaller.technical.marshalling.TextMarshaller;
import net.ihe.gazelle.validation.v2.api.business.Input;
import net.ihe.gazelle.validation.v2.api.business.report.ValidationReport;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

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
    void shouldCreateReportItemAndPersistMappedInputAsAttachmentFallback() throws Exception {
        SampleReport sample = sampleReport();
        StepRunReport step = new StepRunReport()
              .addOutput(new ByteArrayProperty(ValidationStepDefinition.REPORT, sample.serializedReport()))
              .addOutput(new StringProperty(PropertyBindingPayload.PROPERTY_NAME, sample.propertyBindingPayload()));
        Fixture fixture = newFixture(step);

        dissector.dissect(fixture.stepOutputPlan());

        assertEquals(1, fixture.rootPlan().futureReferencePlans().size(), "One report reference expected");
        FutureReferencePlan<TestReport> reportReference = fixture.rootPlan().futureReferencePlans().getFirst();
        assertEquals(ValidationStepDefinition.REPORT, reportReference.referencePlan().getName());
        assertEquals(MaestroRefType.ITEM_ID, reportReference.referencePlan().getRefType());

        @SuppressWarnings("unchecked")
        ItemPlan<ValidationReport> reportPlan = (ItemPlan<ValidationReport>) reportReference.referencePlan().getTarget();
        assertEquals(ValidationStepDefinition.REPORT, reportPlan.type());

        assertEquals(1, reportPlan.futureReferencePlans().size(), "Validation report should create one input attachment future");
        ReferencePlan plannedInputReference = reportPlan.futureReferencePlans().getFirst().referencePlan();
        assertEquals("payload-input", plannedInputReference.getName());
        assertEquals(MaestroRefType.ATTACHMENT, plannedInputReference.getRefType());
        assertEquals("inputFile1", ((AttachmentPlan) plannedInputReference.getTarget()).filename());
        ValidationReport report = reportPlan.itemObject();
        assertNotNull(report.getInputs());
        assertEquals(2, report.getInputs().size());
        assertNull(report.getInputs().getFirst().getContent());
        assertNull(report.getInputs().get(1).getContent());
        assertNull(step.getOutput(PropertyBindingPayload.PROPERTY_NAME), "Ephemeral propertyBinding output must be removed");
    }

    @Test
    void shouldReplaceOutputWithItemReferenceWhenPersisted() throws Exception {
        SampleReport sample = sampleReport();
        StepRunReport step = new StepRunReport()
              .addOutput(new ByteArrayProperty(ValidationStepDefinition.REPORT, sample.serializedReport()))
              .addOutput(new StringProperty(PropertyBindingPayload.PROPERTY_NAME, sample.propertyBindingPayload()));
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
    void shouldPopulateAttachmentIdsInReportDuringFallbackMutation() throws Exception {
        SampleReport sample = sampleReport();
        StepRunReport step = new StepRunReport()
              .addOutput(new ByteArrayProperty(ValidationStepDefinition.REPORT, sample.serializedReport()))
              .addOutput(new StringProperty(PropertyBindingPayload.PROPERTY_NAME, sample.propertyBindingPayload()));
        Fixture fixture = newFixture(step);

        dissector.dissect(fixture.stepOutputPlan());
        @SuppressWarnings("unchecked")
        ItemPlan<ValidationReport> reportPlan =
              (ItemPlan<ValidationReport>) fixture.rootPlan().futureReferencePlans().getFirst().referencePlan().getTarget();

        FutureReferencePlan<ValidationReport> inputFutureReference = reportPlan.futureReferencePlans().stream()
              .filter(plan -> "payload-input".equals(plan.referencePlan().getName()))
              .findFirst()
              .orElseThrow();
        inputFutureReference.mutation().mutateReference(reportPlan.itemObject(), "payload-input", "att-42");

        ValidationReport mutated = reportPlan.itemObject();
        Input input = mutated.getInputs().getFirst();
        assertEquals("att-42", input.getItemId());
        assertEquals("/attachments/att-42", input.getLocation());
        assertNull(input.getContent());
        ReferencePlan inputReference = inputFutureReference.referencePlan();
        assertEquals(MaestroRefType.ATTACHMENT, inputReference.getRefType());
        assertEquals("payload-input", inputReference.getName());
        assertEquals("inputFile1", ((AttachmentPlan) inputReference.getTarget()).filename());
    }

    @Test
    void shouldPopulateAttachmentIdsFromCallbackWhenInputIsSynthesizedFromBinding() throws Exception {
        ValidationReport reportWithoutInputs = new ValidationReport().setUuid("validation-report-id");
        String payload = OBJECT_MAPPER.writeValueAsString(new PropertyBindingPayload()
              .setStepType(ValidationStepDefinition.TYPE)
              .addBinding(new PropertyBindingPayload.Binding()
                    .setTargetKind("validationInput")
                    .setTargetId("payload-input")
                    .setReferenceName("inputFile1")
                    .setPropertyName("contentToValidate")));

        StepRunReport step = new StepRunReport()
              .addOutput(new ByteArrayProperty(ValidationStepDefinition.REPORT, OBJECT_MAPPER.writeValueAsBytes(reportWithoutInputs)))
              .addOutput(new StringProperty(PropertyBindingPayload.PROPERTY_NAME, payload));
        Fixture fixture = newFixture(step);

        dissector.dissect(fixture.stepOutputPlan());
        @SuppressWarnings("unchecked")
        ItemPlan<ValidationReport> reportPlan =
              (ItemPlan<ValidationReport>) fixture.rootPlan().futureReferencePlans().getFirst().referencePlan().getTarget();

        fixture.callbackRegistry().notifyResolved("inputFile1", "att-99");

        ValidationReport mutated = reportPlan.itemObject();
        assertNotNull(mutated.getInputs());
        assertEquals(1, mutated.getInputs().size());
        assertEquals("payload-input", mutated.getInputs().getFirst().getId());
        assertEquals("att-99", mutated.getInputs().getFirst().getItemId());
        assertEquals("/attachments/att-99", mutated.getInputs().getFirst().getLocation());

        ReferencePlan inputReference = reportPlan.futureReferencePlans().stream()
              .map(FutureReferencePlan::referencePlan)
              .filter(referencePlan -> "payload-input".equals(referencePlan.getName())
                    && referencePlan.getRefType() == MaestroRefType.ATTACHMENT)
              .findFirst()
              .orElseThrow();
        assertEquals(MaestroRefType.ATTACHMENT, inputReference.getRefType());
        assertEquals("att-99", inputReference.getTarget());
    }

    @Test
    void shouldPreferBindingFileNameForFallbackAttachment() throws Exception {
        ValidationReport report = new ValidationReport()
              .setUuid("validation-report-id")
              .addInput(new Input().setId("contentToValidate").setContent("x".getBytes(StandardCharsets.UTF_8)));
        String payload = OBJECT_MAPPER.writeValueAsString(new PropertyBindingPayload()
              .setStepType(ValidationStepDefinition.TYPE)
              .addBinding(new PropertyBindingPayload.Binding()
                    .setTargetKind("validationInput")
                    .setTargetId("contentToValidate")
                    .setReferenceName("inputFile1")
                    .setPropertyName("contentToValidate")
                    .setFileName("audit-message.xml")));

        StepRunReport step = new StepRunReport()
              .addOutput(new ByteArrayProperty(ValidationStepDefinition.REPORT, OBJECT_MAPPER.writeValueAsBytes(report)))
              .addOutput(new StringProperty(PropertyBindingPayload.PROPERTY_NAME, payload));
        Fixture fixture = newFixture(step);

        dissector.dissect(fixture.stepOutputPlan());
        @SuppressWarnings("unchecked")
        ItemPlan<ValidationReport> reportPlan =
              (ItemPlan<ValidationReport>) fixture.rootPlan().futureReferencePlans().getFirst().referencePlan().getTarget();
        ReferencePlan plannedInputReference = reportPlan.futureReferencePlans().getFirst().referencePlan();
        assertEquals("audit-message.xml", ((AttachmentPlan) plannedInputReference.getTarget()).filename());
    }

    private Fixture newFixture(StepRunReport stepRunReport) {
        ItemPlan<TestReport> rootPlan = new ItemPlan<>("TEST_REPORT", new TestReport(), new TestReportMarshaller());
        TestFutureReferenceCallbackRegistry callbackRegistry = new TestFutureReferenceCallbackRegistry();
        StepOutputPlan stepOutputPlan = new StepOutputPlan(rootPlan, stepRunReport, "http://dh", callbackRegistry);
        return new Fixture(rootPlan, stepOutputPlan, callbackRegistry);
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

        PropertyBindingPayload propertyBindingPayload = new PropertyBindingPayload()
              .setStepType(ValidationStepDefinition.TYPE)
              .addBinding(new PropertyBindingPayload.Binding()
                    .setTargetKind("validationInput")
                    .setTargetId("payload-input")
                    .setReferenceName("inputFile1")
                    .setPropertyName("contentToValidate"));

        return new SampleReport(
              OBJECT_MAPPER.writeValueAsBytes(report),
              OBJECT_MAPPER.writeValueAsString(propertyBindingPayload)
        );
    }

    private record SampleReport(byte[] serializedReport, String propertyBindingPayload) {
    }

    private record Fixture(ItemPlan<TestReport> rootPlan,
                           StepOutputPlan stepOutputPlan,
                           TestFutureReferenceCallbackRegistry callbackRegistry) {
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
            return marshallAsString(report).getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public TestReport unmarshall(byte[] payload) {
            throw new UnsupportedOperationException();
        }
    }

    private static final class TestFutureReferenceCallbackRegistry implements FutureReferenceCallbackRegistry {
        private final java.util.Map<String, java.util.List<java.util.function.BiConsumer<String, String>>> callbacksByReferenceName =
              new java.util.HashMap<>();

        @Override
        public void registerCallback(String referenceName, java.util.function.BiConsumer<String, String> callback) {
            callbacksByReferenceName.computeIfAbsent(referenceName, key -> new java.util.ArrayList<>()).add(callback);
        }

        @Override
        public void notifyResolved(String referenceName, String referenceId) {
            java.util.List<java.util.function.BiConsumer<String, String>> callbacks = callbacksByReferenceName.get(referenceName);
            if (callbacks == null) {
                return;
            }
            for (java.util.function.BiConsumer<String, String> callback : callbacks) {
                callback.accept(referenceName, referenceId);
            }
        }
    }
}
