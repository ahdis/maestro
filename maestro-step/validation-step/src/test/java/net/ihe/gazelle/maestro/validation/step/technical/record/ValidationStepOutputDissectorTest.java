package net.ihe.gazelle.maestro.validation.step.technical.record;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.testreport.ByteArrayItemProperty;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.spi.business.recording.*;
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
        AttachmentPlan attachmentPlan = projection.attachments().getFirst();
        assertEquals(-1, attachmentPlan.outputIndex());
        assertNull(attachmentPlan.propertyFactory(), "Detached attachments must not target a step output");
        assertArrayEquals(sample.inputPayload(), attachmentPlan.attachment().content());

        assertEquals(1, projection.propertyMutations().size());
        PropertyMutation mutation = projection.propertyMutations().getFirst();
        assertEquals(0, mutation.outputIndex());
        assertEquals(0, mutation.sourceItemIndex(), "Single report item should be the mutation source");
        assertReplacementItemType(mutation, ValidationStepDefinition.REPORT);

        JsonNode content = OBJECT_MAPPER.readTree(reportPlan.item().content());
        JsonNode inputs = content.get("inputs");
        assertNotNull(inputs);
        assertEquals(2, inputs.size());
        JsonNode attachedInput = inputs.get(0);
        assertEquals("payload-input", attachedInput.get("id").asText());
        assertNull(attachedInput.get("content"), "Payload should be stripped");
        assertNull(attachedInput.get("itemId"));
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

    @Test
    void shouldPopulateAttachmentIdsInReportContentDuringMutation() throws Exception {
        SampleReport sample = sampleReport();
        StepRunReport step = new StepRunReport()
                .addOutput(new ByteArrayProperty(ValidationStepDefinition.REPORT, sample.serializedReport()));

        StepOutputProjection projection = dissector.dissect(new StepOutputContext(step, new AccessControlList()));
        ItemPlan reportPlan = projection.items().getFirst();

        assertNotNull(reportPlan.contentMutator(), "Expected content mutator to be provided");

        ContentMutationContext mutationContext = new ContentMutationContext(
                null,
                java.util.Map.of("payload-input", "att-42"),
                "http://dh");

        String mutated = reportPlan.contentMutator().mutate(reportPlan.item().content(), mutationContext);
        JsonNode root = OBJECT_MAPPER.readTree(mutated);
        JsonNode inputs = root.get("inputs");
        assertNotNull(inputs);
        JsonNode first = inputs.get(0);
        assertEquals("att-42", first.get("itemId").asText());
        assertEquals("/attachments/att-42", first.get("location").asText());
        assertNull(first.get("content"));
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
