package net.ihe.gazelle.maestro.validation.step.technical.record;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.dissection.*;
import net.ihe.gazelle.maestro.validation.step.business.ValidationStepDefinition;
import net.ihe.gazelle.security.business.acl.AccessControlList;
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
    void shouldReturnEmptyProjectionWhenNoValidationReportProperty() {
        StepRunReport step = new StepRunReport();
        StepOutputProjection projection = dissector.dissect(new StepOutputContext(step, new AccessControlList()));

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

        StepOutputProjection projection = dissector.dissect(new StepOutputContext(step, new AccessControlList()));

        assertEquals(1, projection.items().size(), "One validation report item expected");
        ItemPlan reportPlan = projection.items().get(0);
        assertEquals(ValidationStepDefinition.REPORT, reportPlan.item().type());

        assertEquals(1, projection.attachments().size(), "Only inputs with content should become attachments");
        AttachmentPlan attachmentPlan = projection.attachments().get(0);
        assertEquals(-1, attachmentPlan.outputIndex());
        assertNull(attachmentPlan.propertyFactory(), "Detached attachments must not target a step output");
        assertArrayEquals(sample.inputPayload(), attachmentPlan.attachment().getContent());

        assertEquals(1, projection.propertyMutations().size());
        PropertyMutation mutation = projection.propertyMutations().get(0);
        assertEquals(0, mutation.outputIndex());
        assertEquals(0, mutation.sourceItemIndex(), "Single report item should be the mutation source");

        JsonNode content = OBJECT_MAPPER.readTree(reportPlan.item().content());
        assertFalse(content.has("inputs"), "Raw inputs collection should be replaced by references");
        JsonNode inputRefs = content.get("inputReferences");
        assertNotNull(inputRefs);
        assertEquals(1, inputRefs.size(), "Only inputs stored as attachments should be referenced");
        JsonNode firstRef = inputRefs.get(0);
        assertEquals("payload-input", firstRef.get("referenceName").asText());
        assertEquals(ValidationStepOutputDissector.VALIDATION_INPUT, firstRef.get("referenceType").asText());
    }

    @Test
    void shouldStripInputPayloadFromSerializedReportContent() throws Exception {
        SampleReport sample = sampleReport();
        StepRunReport step = new StepRunReport()
                .addOutput(new ByteArrayProperty(ValidationStepDefinition.REPORT, sample.serializedReport()));

        StepOutputProjection projection = dissector.dissect(new StepOutputContext(step, new AccessControlList()));
        String persistedContent = projection.items().getFirst().item().content();

        assertNotNull(persistedContent);
        assertFalse(persistedContent.contains(sample.encodedPayload()),
                "Detached attachment payload must not remain in the stored report JSON");
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
