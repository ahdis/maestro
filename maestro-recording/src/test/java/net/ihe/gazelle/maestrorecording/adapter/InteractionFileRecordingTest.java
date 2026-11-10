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

package net.ihe.gazelle.maestrorecording.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kereval.gazelle.datahouse.api.business.record.Item;
import net.ihe.gazelle.maestro.api.business.message.StepRunFinished;
import net.ihe.gazelle.maestro.api.business.property.ByteArrayItemProperty;
import net.ihe.gazelle.maestro.api.business.property.ByteArrayProperty;
import net.ihe.gazelle.maestro.api.business.property.PropertyBindingPayload;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestRunReport;
import net.ihe.gazelle.maestro.engine.business.TestReportRecordingException;
import net.ihe.gazelle.maestro.userinteraction.step.technical.record.UserInteractionStepOutputDissector;
import net.ihe.gazelle.maestro.validation.step.business.ValidationStepDefinition;
import net.ihe.gazelle.maestro.validation.step.technical.record.ValidationStepOutputDissector;
import net.ihe.gazelle.validation.v2.api.business.Input;
import net.ihe.gazelle.validation.v2.api.business.report.ValidationReport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class InteractionFileRecordingTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void eachValidationIsLinkedToTheProducerOfItsAlias() throws Exception {
        var first = interaction("first.xml", (byte) 1).setStepName("First interaction");
        var firstValidation = validation("firstDocument").setStepName("First validation");
        var second = interaction("second.xml", (byte) 2).setStepName("Second interaction");
        var secondValidation = validation("secondDocument").setStepName("Second validation");
        var client = new RecordItemClientMock();
        recorder(client)
                .recordTestReport(
                        report(List.of(first, firstValidation, second, secondValidation)),
                        definitions(
                                List.of(
                                        "First interaction",
                                        "First validation",
                                        "Second interaction",
                                        "Second validation"),
                                List.of(
                                        Map.of("document", "firstDocument"),
                                        Map.of(),
                                        Map.of("document", "secondDocument"),
                                        Map.of())));

        var validations =
                client.recordedItems.stream()
                        .map(Item.class::cast)
                        .filter(item -> ValidationStepDefinition.REPORT.equals(item.getType()))
                        .toList();
        assertEquals(2, validations.size());
        for (int index = 0; index < validations.size(); index++) {
            String expectedId = "att-" + (index + 1);
            Item item = validations.get(index);
            assertEquals(
                    expectedId,
                    objectMapper
                            .readTree(item.getContent())
                            .path("inputs")
                            .get(0)
                            .path("itemId")
                            .asText());
            assertEquals(expectedId, item.getReferences().getFirst().getValue());
        }
        assertEquals(2, client.uploadedAttachmentsBatches.size());
    }

    @Test
    void reversedReportsStillLinkEachValidationToTheRightFile() throws Exception {
        var first = interaction("first.xml", (byte) 1).setStepName("First interaction");
        var firstValidation = validation("firstDocument").setStepName("First validation");
        var second = interaction("second.xml", (byte) 2).setStepName("Second interaction");
        var secondValidation = validation("secondDocument").setStepName("Second validation");
        var client = new RecordItemClientMock();
        var definitions =
                definitions(
                        List.of(
                                "First interaction",
                                "First validation",
                                "Second interaction",
                                "Second validation"),
                        List.of(
                                Map.of("document", "firstDocument"),
                                Map.of(),
                                Map.of("document", "secondDocument"),
                                Map.of()));
        recorder(client)
                .recordTestReport(
                        report(List.of(secondValidation, second, firstValidation, first)),
                        definitions);
        var validations = validationItems(client);
        assertEquals(2, validations.size());
        assertValidationAttachment(validations.get(0), "att-1");
        assertValidationAttachment(validations.get(1), "att-2");
    }

    @Test
    void unknownStepNameDoesNotReceiveAliasesFromItsPosition() throws Exception {
        var producer = interaction("unknown.xml", (byte) 1).setStepName("Unknown interaction");
        var consumer = validation("sharedDocument");
        var client = new RecordItemClientMock();
        recorder(client)
                .recordTestReport(
                        report(List.of(producer, consumer)),
                        definitions(
                                List.of("Expected interaction", "Validation"),
                                List.of(Map.of("document", "sharedDocument"), Map.of())));
        var validationItem = validationItems(client).getFirst();
        assertTrue(validationItem.getReferences().isEmpty());
        var attachmentId =
                objectMapper
                        .readTree(validationItem.getContent())
                        .path("inputs")
                        .get(0)
                        .path("itemId");
        assertTrue(attachmentId.isMissingNode() || attachmentId.isNull());
    }

    @Test
    void nullTestIdsDoNotMatchEachOther() throws Exception {
        var producer = interaction("unknown.xml", (byte) 1);
        var report = report(List.of(producer, validation("sharedDocument")));
        report.getTestRunReports().getFirst().getTest().setId(null);
        var definitions =
                definitions(
                        List.of("Interaction", "Validation"),
                        List.of(Map.of("document", "sharedDocument"), Map.of()));
        definitions.getFirst().setId(null);
        var client = new RecordItemClientMock();
        recorder(client).recordTestReport(report, definitions);
        assertTrue(validationItems(client).getFirst().getReferences().isEmpty());
    }

    @Test
    void partialRunUploadsProducerWithoutAConsumerReport() {
        StepRunReport producer = interaction("partial.xml", (byte) 1);
        RecordItemClientMock client = new RecordItemClientMock();
        assertDoesNotThrow(
                () ->
                        recorder(client)
                                .recordTestReport(
                                        report(List.of(producer)),
                                        definitions(
                                                List.of("Interaction", "Validation"),
                                                List.of(
                                                        Map.of("document", "sharedDocument"),
                                                        Map.of()))));
        assertEquals(1, client.uploadedAttachmentsBatches.size());
        assertEquals(
                "att-1",
                ((Item) client.recordedItems.getLast()).getReferences().getFirst().getValue());
    }

    @Test
    void sameAliasInTwoTestsUsesEachRunsOwnFile() throws Exception {
        TestRunReport firstRun =
                report(List.of(interaction("first.xml", (byte) 1), validation("sharedDocument")))
                        .getTestRunReports()
                        .getFirst();
        TestRunReport secondRun =
                report(List.of(interaction("second.xml", (byte) 2), validation("sharedDocument")))
                        .getTestRunReports()
                        .getFirst();
        secondRun.setRunId("second-run");
        secondRun.getTest().setId("second-test");
        net.ihe.gazelle.maestro.api.business.test.Test firstDefinition =
                definitions(
                                List.of("Interaction", "Validation"),
                                List.of(Map.of("document", "sharedDocument"), Map.of()))
                        .getFirst();
        net.ihe.gazelle.maestro.api.business.test.Test secondDefinition =
                definitions(
                                List.of("Interaction", "Validation"),
                                List.of(Map.of("document", "sharedDocument"), Map.of()))
                        .getFirst()
                        .setId("second-test");
        TestReport suiteReport =
                new TestReport()
                        .setUuid("suite")
                        .setDateTime(Instant.now())
                        .addTestRunReport(firstRun)
                        .addTestRunReport(secondRun);
        RecordItemClientMock client = new RecordItemClientMock();
        recorder(client).recordTestReport(suiteReport, List.of(firstDefinition, secondDefinition));
        List<Item> validations = validationItems(client);
        assertEquals(2, validations.size());
        assertValidationAttachment(validations.getFirst(), "att-1");
        assertValidationAttachment(validations.get(1), "att-2");
    }

    private List<Item> validationItems(RecordItemClientMock client) {
        return client.recordedItems.stream()
                .map(Item.class::cast)
                .filter(item -> ValidationStepDefinition.REPORT.equals(item.getType()))
                .toList();
    }

    private void assertValidationAttachment(Item validationItem, String expectedId)
            throws Exception {
        assertEquals(
                expectedId,
                objectMapper
                        .readTree(validationItem.getContent())
                        .path("inputs")
                        .get(0)
                        .path("itemId")
                        .asText());
        assertEquals(expectedId, validationItem.getReferences().getFirst().getValue());
    }

    @Test
    void unmappedOutputCannotReplaceATestInputWithTheSameName() throws Exception {
        StepRunReport interaction = interaction("new.xml", (byte) 2);
        StepRunReport validation = validation("document");
        TestReport report = report(List.of(interaction, validation));
        report.getTestRunReports()
                .getFirst()
                .addInput(
                        new ByteArrayProperty("document", new byte[] {1})
                                .setFileName("original.xml"));
        RecordItemClientMock client = new RecordItemClientMock();
        recorder(client).recordTestReport(report);
        Item item =
                client.recordedItems.stream()
                        .map(Item.class::cast)
                        .filter(
                                recorded ->
                                        ValidationStepDefinition.REPORT.equals(recorded.getType()))
                        .findFirst()
                        .orElseThrow();
        assertEquals(
                "att-1",
                objectMapper
                        .readTree(item.getContent())
                        .path("inputs")
                        .get(0)
                        .path("itemId")
                        .asText());
        assertEquals("att-1", item.getReferences().getFirst().getValue());
    }

    @Test
    void retryAfterUploadReusesAttachmentAndPublishesItsSource() {
        AtomicBoolean failRoot = new AtomicBoolean(true);
        RecordItemClientMock client =
                new RecordItemClientMock() {
                    @Override
                    public String recordItem(Item item) {
                        if ("TEST_REPORT".equals(item.getType()) && failRoot.getAndSet(false)) {
                            throw new IllegalStateException(
                                    "Root persistence failed after attachment upload");
                        }
                        return super.recordItem(item);
                    }
                };
        StepRunReport step = interaction("patient.xml", (byte) 1);
        TestReport report = report(List.of(step));
        DatahouseTestReportRecordingService recorder = recorder(client);
        List<net.ihe.gazelle.maestro.api.business.test.Test> tests =
                definitions(List.of("Interaction"), List.of(Map.of("document", "sharedDocument")));
        assertThrows(RuntimeException.class, () -> recorder.recordTestReport(report, tests));
        recorder.recordTestReport(
                report,
                definitions(List.of("Interaction"), List.of(Map.of("document", "sharedDocument"))));
        assertEquals(1, client.uploadedAttachmentsBatches.size());
        Item root = (Item) client.recordedItems.getLast();
        assertEquals("att-1", root.getReferences().getFirst().getValue());
        assertEquals("document", root.getReferences().getFirst().getName());
    }

    private StepRunReport interaction(String filename, byte value) {
        ByteArrayProperty file =
                new ByteArrayProperty("document", new byte[] {value}).setFileName(filename);
        return new StepRunReport()
                .setType("USER_INTERACTION")
                .setStepName("Interaction")
                .setInputs(List.of(file.copy()))
                .setOutputs(List.of(file.copy()));
    }

    private StepRunReport validation(String sourceAlias) throws Exception {
        PropertyBindingPayload bindings =
                new PropertyBindingPayload()
                        .addBinding(
                                new PropertyBindingPayload.Binding()
                                        .setTargetKind("validationInput")
                                        .setTargetId("file")
                                        .setReferenceName(sourceAlias)
                                        .setPropertyName("file"));
        ValidationReport validationReport =
                new ValidationReport().setUuid("validation").addInput(new Input().setId("file"));
        return new StepRunReport()
                .setType("VALIDATION")
                .setStepName("Validation")
                .addOutput(
                        new ByteArrayProperty(
                                ValidationStepDefinition.REPORT,
                                objectMapper.writeValueAsBytes(validationReport)))
                .addOutput(
                        new StringProperty(
                                PropertyBindingPayload.PROPERTY_NAME,
                                objectMapper.writeValueAsString(bindings)));
    }

    private TestReport report(List<StepRunReport> steps) {
        return new TestReport()
                .setUuid("recording-test")
                .setDateTime(Instant.now())
                .addTestRunReport(
                        new TestRunReport()
                                .setRunId("run")
                                .setDateTime(Instant.now())
                                .setTest(
                                        new net.ihe.gazelle.maestro.api.business.testreport.Test()
                                                .setId("test")
                                                .setName("Test"))
                                .setStepRunReports(steps));
    }

    @Test
    void incrementalRecordingKeepsRawValuesReusesWritesAndPreservesLateEvidence() throws Exception {
        var client = new RecordItemClientMock();
        var producer = interaction("first.xml", (byte) 1).setStepName("First interaction");
        var consumer = validation("documentAlias").setStepName("First validation");
        var definitions =
                definitions(
                        List.of("First interaction", "First validation"),
                        List.of(Map.of("document", "documentAlias"), Map.of()));
        var finalReport = report(List.of(producer, consumer));
        var session =
                recorder(client)
                        .openSession(definitions, finalReport::getAccessControlList, List::of);
        var first = new StepRunFinished("session", "test", producer.getStepName(), null, producer);
        var published = session.recordStep(first);
        assertNotNull(published);
        assertNotNull(producer.getOutputs().getFirst().getValue());
        assertNull(published.getStepRunReport().getOutputs().getFirst().getValue());
        assertNull(session.recordStep(first));
        session.recordStep(
                new StepRunFinished("session", "test", consumer.getStepName(), null, consumer));
        assertEquals(1, validationItems(client).size());
        assertValidationAttachment(validationItems(client).getFirst(), "att-1");
        producer.addEvidence(new StringProperty("late", "evidence"));
        session.finish(finalReport);
        assertEquals(1, validationItems(client).size());
        assertEquals(1, client.uploadedAttachmentsBatches.size());
        var root = (Item) client.recordedItems.getLast();
        assertTrue(root.getContent().contains("evidence"));
        assertNotNull(consumer.getOutput(ValidationStepDefinition.REPORT).getValue());
        assertNull(session.recordStep(first));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void failedIncrementalWriteDoesNotRetryAnUncertainOutcome(boolean acceptedBeforeFailure) {
        var client = new RecordItemClientMock();
        client.loseUploadAcknowledgement = acceptedBeforeFailure;
        client.uploadFailure =
                acceptedBeforeFailure
                        ? null
                        : new IllegalStateException("Upload outcome uncertain");
        var producer = interaction("first.xml", (byte) 1).setStepName("First interaction");
        var finalReport = report(List.of(producer));
        var session =
                recorder(client)
                        .openSession(
                                definitions(List.of("First interaction"), List.of(Map.of())),
                                finalReport::getAccessControlList,
                                List::of);
        var stepRunFinished =
                new StepRunFinished("session", "test", producer.getStepName(), null, producer);
        assertThrows(IllegalStateException.class, () -> session.recordStep(stepRunFinished));
        assertNull(
                session.recordStep(
                        new StepRunFinished(
                                "session", "test", producer.getStepName(), null, producer)),
                "No incremental write after a failure");
        assertEquals(1, client.uploadAttempts);

        client.uploadFailure = null;
        client.loseUploadAcknowledgement = false;
        assertEquals(acceptedBeforeFailure ? 1 : 0, client.uploadedAttachmentsBatches.size());
        assertThrows(TestReportRecordingException.class, () -> session.finish(finalReport));
        assertEquals(
                1,
                client.uploadAttempts,
                "An uncertain upload must not be retried during finalization");
        assertTrue(client.recordedItems.isEmpty());
        assertNotNull(producer.getOutputs().getFirst().getValue());
    }

    @Test
    void finalizationReconcilesStepsBeforeTheirObserverCallbacksArrive() {
        var client = new RecordItemClientMock();
        var producer = interaction("first.xml", (byte) 1).setStepName("First interaction");
        var finalReport = report(List.of(producer));
        var session =
                recorder(client)
                        .openSession(
                                definitions(List.of("First interaction"), List.of(Map.of())),
                                finalReport::getAccessControlList,
                                List::of);
        session.finish(finalReport);
        assertEquals(1, client.uploadedAttachmentsBatches.size());
        assertNull(
                session.recordStep(
                        new StepRunFinished(
                                "session", "test", producer.getStepName(), null, producer)));
        assertEquals(1, client.uploadedAttachmentsBatches.size());
    }

    @Test
    void fullBackgroundQueueDefersOutputsToFinalRecordingWithoutLosingThem() throws Exception {
        var client = new BlockingUploadRecordItemClient();
        List<StepRunReport> steps =
                IntStream.range(0, 70)
                        .mapToObj(
                                index ->
                                        interaction("file-" + index + ".xml", (byte) index)
                                                .setStepName("Interaction " + index))
                        .toList();
        var finalReport = report(steps);
        var session =
                recorder(client)
                        .openSession(
                                definitions(
                                        steps.stream().map(StepRunReport::getStepName).toList(),
                                        steps.stream()
                                                .map(step -> Map.<String, String>of())
                                                .toList()),
                                finalReport::getAccessControlList,
                                List::of);
        try {
            for (int index = 0; index < steps.size(); index++) {
                StepRunReport step = steps.get(index);
                session.submitStep(
                        new StepRunFinished("session", "test", step.getStepName(), null, step),
                        null);
                if (index == 0) {
                    assertTrue(client.writing.await(5, TimeUnit.SECONDS));
                }
            }
        } finally {
            client.release.countDown();
        }
        var result = session.finish(finalReport);
        assertNotNull(result.location());
        assertEquals(
                70, client.uploadAttempts, "Queued and deferred outputs must each be stored once");
        assertEquals(1, client.recordedItems.size(), "Only the final parent item is created");
        assertEquals(70, result.report().getTestRunReports().getFirst().getStepRunReports().size());
        assertTrue(
                result.report().getTestRunReports().getFirst().getStepRunReports().stream()
                        .allMatch(
                                step ->
                                        step.getOutput("document")
                                                        instanceof ByteArrayItemProperty item
                                                && item.getReference() != null
                                                && item.getValue() == null));
        assertTrue(
                finalReport.getTestRunReports().getFirst().getStepRunReports().stream()
                        .allMatch(step -> step.getOutput("document").getValue() != null));
    }

    private List<net.ihe.gazelle.maestro.api.business.test.Test> definitions(
            List<String> stepNames, List<Map<String, String>> aliases) {
        List<Step> steps =
                IntStream.range(0, aliases.size())
                        .mapToObj(
                                index ->
                                        new Step()
                                                .setName(stepNames.get(index))
                                                .setOutputMappings(aliases.get(index)))
                        .toList();
        return List.of(
                new net.ihe.gazelle.maestro.api.business.test.Test().setId("test").setSteps(steps));
    }

    private DatahouseTestReportRecordingService recorder(RecordItemClientMock client) {
        return new DatahouseTestReportRecordingService(
                client,
                "http://dh",
                step ->
                        switch (step.getType()) {
                            case "USER_INTERACTION" -> new UserInteractionStepOutputDissector();
                            case "VALIDATION" -> new ValidationStepOutputDissector();
                            default -> null;
                        });
    }
}
