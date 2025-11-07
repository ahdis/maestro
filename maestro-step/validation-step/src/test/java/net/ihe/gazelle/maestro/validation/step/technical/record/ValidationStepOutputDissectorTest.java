package net.ihe.gazelle.maestro.validation.step.technical.record;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.*;
import net.ihe.gazelle.maestro.validation.step.business.ValidationStepDefinition;
import net.ihe.gazelle.validation.v2.api.business.Input;
import net.ihe.gazelle.validation.v2.api.business.report.ValidationReport;
import net.ihe.gazelle.security.business.acl.AccessControlList;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidationStepOutputDissectorTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final ValidationStepOutputDissector dissector = new ValidationStepOutputDissector();

    @Test
    void shouldReturnEmptyProjectionWhenNoValidationReportProperty() {
        StepRunReport step = new StepRunReport();
        StepOutputProjection projection = dissect(step);

        assertNotNull(projection);
        assertTrue(projection.items().isEmpty());
        assertTrue(projection.attachments().isEmpty());
        assertTrue(projection.propertyMutations().isEmpty());
    }

    @Test
    void shouldCreateReportItemAndAttachmentsForInputsWithContent() throws Exception {
        SampleReport sample = sampleReport();
        StepRunReport step = new StepRunReport()
                .addOutput(new ByteArrayProperty(ValidationStepDefinition.REPORT, sample.serializedReport()));

        StepOutputProjection projection = dissect(step);

        assertEquals(1, projection.items().size(), "One validation report item expected");
        ItemPlan reportPlan = projection.items().get(0);
        assertEquals(ValidationStepDefinition.REPORT, reportPlan.item().getType());

        assertEquals(1, projection.attachments().size(), "Only inputs with content should become attachments");
        AttachmentPlan attachmentPlan = projection.attachments().get(0);
        assertEquals(-1, attachmentPlan.outputIndex());
        assertNull(attachmentPlan.propertyFactory(), "Detached attachments must not target a step output");
        assertArrayEquals(sample.inputPayload(), attachmentPlan.attachment().getContent());

        assertEquals(1, projection.propertyMutations().size());
        PropertyMutation mutation = projection.propertyMutations().get(0);
        assertEquals(0, mutation.outputIndex());
        assertEquals(0, mutation.sourceItemIndex(), "Single report item should be the mutation source");
    }

    @Test
    void shouldStripInputPayloadFromSerializedReportContent() throws Exception {
        SampleReport sample = sampleReport();
        StepRunReport step = new StepRunReport()
                .addOutput(new ByteArrayProperty(ValidationStepDefinition.REPORT, sample.serializedReport()));

        StepOutputProjection projection = dissect(step);
        String persistedContent = projection.items().get(0).item().getContent();

        assertNotNull(persistedContent);
        assertFalse(persistedContent.contains(sample.encodedPayload()),
                "Detached attachment payload must not remain in the stored report JSON");
    }

    private StepOutputProjection dissect(StepRunReport stepRunReport) {
        StepOutputContext context = new StepOutputContext(stepRunReport, new TestRunReport(), new AccessControlList());
        return dissector.dissect(context);
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
