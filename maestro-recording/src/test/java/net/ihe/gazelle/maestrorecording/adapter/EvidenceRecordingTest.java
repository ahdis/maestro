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
import net.ihe.gazelle.maestro.api.business.message.PutEvidence;
import net.ihe.gazelle.maestro.api.business.message.StepRunFinished;
import net.ihe.gazelle.maestro.api.business.property.*;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestRunReport;
import net.ihe.gazelle.maestro.api.technical.dto.message.EvidenceRecordedDTO;
import net.ihe.gazelle.maestro.engine.business.TestReportRecordingException;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import net.ihe.gazelle.security.business.acl.AccessControlList;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EvidenceRecordingTest {
    private final RecordItemClientMock client = new RecordItemClientMock();
    private final AccessControlList acl =
            new AccessControlList().setOwners(Set.of("owner")).setPublic(false);
    private final DatahouseTestReportRecordingService recorder =
            new DatahouseTestReportRecordingService(client, "http://datahouse", report -> null);

    @Test
    void recordedEventContainsOnlyTheReferenceAndItsAcceptedRevision() {
        var file = new ByteArrayItemProperty(file()).setReference("/items/owner/attachments/file");
        file.setValue(null);
        var event =
                new EvidenceRecordedDTO(
                        new PutEvidence()
                                .setRunId(1)
                                .setStepName("step")
                                .setSessionId("session")
                                .setContent(file));
        String json = new JacksonSerDes().serializeAsString(event);
        assertTrue(json.contains("\"type\":\"EVIDENCE_RECORDED\""));
        assertTrue(json.contains("\"type\":\"BYTE_ARRAY_ITEM\""));
        assertTrue(json.contains("/items/owner/attachments/file"));
        assertFalse(json.contains("AQID"));
    }

    @Test
    void finalRecordingExtractsFilesForAnyStepAndKeepsTextAndLinksInline() {
        ByteArrayProperty file = file();
        StringProperty text = new StringProperty("note", "review notes");
        LinkProperty link = new LinkProperty("link", "https://example.org/evidence");
        StepRunReport step = step(List.of(file, text, link));
        TestReport report = report(step);
        recorder.recordTestReport(report, List.of());
        assertEquals(1, client.uploadAttempts);
        ByteArrayItemProperty recorded =
                assertInstanceOf(ByteArrayItemProperty.class, step.getEvidences().getFirst());
        assertEquals("/attachments/att-1", recorded.getReference());
        assertNull(recorded.getValue());
        assertEquals("owner", recorded.getUserId());
        assertEquals(file.getDateTime(), recorded.getDateTime());
        assertSame(text, step.getEvidences().get(1));
        assertSame(link, step.getEvidences().get(2));
        Item stored = (Item) client.recordedItems.getLast();
        assertSame(acl, stored.getAccessControlList());
        assertEquals("EVIDENCE_ATTACHMENT", stored.getReferences().getFirst().getType());
        assertFalse(stored.getContent().contains("AQID"));
        assertEquals(
                "proof.txt", client.uploadedAttachmentsBatches.getFirst().getFirst().getFilename());
    }

    @Test
    void liveEvidenceHasAnOwnerAndIsReusedAfterCachedStepRecording() throws Exception {
        var definition =
                new net.ihe.gazelle.maestro.api.business.test.Test().setId("test").setName("Test");
        var session = recorder.openSession(List.of(definition), () -> acl, List::of);
        StepRunReport step = step(List.of());
        session.recordStep(new StepRunFinished("session", "test", "step", null, step));
        ByteArrayProperty file = file();
        ByteArrayItemProperty live =
                assertInstanceOf(ByteArrayItemProperty.class, session.recordEvidence(file));
        assertEquals("/items/itm-1/attachments/att-1", live.getReference());
        assertArrayEquals(new byte[] {1, 2, 3}, file.getValue());
        Item owner = (Item) client.recordedItems.getFirst();
        assertEquals("TEST_EVIDENCE", owner.getType());
        var ownerContent = new ObjectMapper().readTree(owner.getContent());
        assertTrue(ownerContent.isObject());
        assertEquals(
                "/attachments/att-1",
                ownerContent.path("evidences").get(0).path("reference").asText());
        assertFalse(ownerContent.path("evidences").get(0).hasNonNull("value"));
        assertSame(acl, owner.getAccessControlList());
        assertEquals("att-1", owner.getReferences().getFirst().getValue());
        step.setEvidences(List.of(live));
        var saved = session.finish(report(step));
        assertEquals(1, client.uploadAttempts);
        assertEquals(2, client.recordedItems.size());
        assertNull(
                ((ByteArrayItemProperty)
                                saved.report()
                                        .getTestRunReports()
                                        .getFirst()
                                        .getStepRunReports()
                                        .getFirst()
                                        .getEvidences()
                                        .getFirst())
                        .getValue());
        assertEquals(
                "att-1",
                ((Item) client.recordedItems.getLast()).getReferences().getFirst().getValue());
    }

    @Test
    void finalizationDoesNotResurrectReplacedOrDeletedLiveFiles() {
        var session = recorder.openSession(List.of(), () -> acl, List::of);
        Property deleted = session.recordEvidence(file());
        Property replacement = new StringProperty("proof", "replacement note");
        var result = session.finish(report(step(List.of(replacement))));
        assertEquals(1, client.uploadAttempts);
        assertTrue(((Item) client.recordedItems.getLast()).getReferences().isEmpty());
        assertEquals(
                "replacement note",
                result.report()
                        .getTestRunReports()
                        .getFirst()
                        .getStepRunReports()
                        .getFirst()
                        .getEvidences()
                        .getFirst()
                        .getValue());
        assertNotNull(deleted);
    }

    @Test
    void uncertainUploadFailureIsNotRetriedAtFinalization() {
        var session = recorder.openSession(List.of(), () -> acl, List::of);
        client.loseUploadAcknowledgement = true;
        ByteArrayProperty file = file();
        TestReport finalReport = report(step(List.of(file)));
        assertThrows(RuntimeException.class, () -> session.recordEvidence(file));
        assertThrows(TestReportRecordingException.class, () -> session.finish(finalReport));
        assertEquals(1, client.uploadAttempts);
        assertTrue(client.recordedItems.isEmpty());
    }

    private ByteArrayProperty file() {
        ByteArrayProperty property =
                new ByteArrayProperty(
                        "proof",
                        new byte[] {1, 2, 3},
                        "owner",
                        Instant.parse("2026-11-05T12:00:00Z"));
        property.setFileName("proof.txt");
        property.setMimeType("text/plain");
        return property;
    }

    private StepRunReport step(List<Property> evidence) {
        return new StepRunReport()
                .setStepName("step")
                .setDateTime(Instant.parse("2026-11-05T11:00:00Z"))
                .setEvidences(evidence);
    }

    private TestReport report(StepRunReport step) {
        TestRunReport run = new TestRunReport();
        run.setTest(
                new net.ihe.gazelle.maestro.api.business.testreport.Test()
                        .setId("test")
                        .setName("Test"));
        run.setStepRunReports(List.of(step));
        TestReport report = new TestReport();
        report.setAccessControlList(acl);
        report.setTestRunReports(List.of(run));
        return report;
    }
}
