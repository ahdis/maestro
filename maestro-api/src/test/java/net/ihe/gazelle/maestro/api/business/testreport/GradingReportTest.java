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

package net.ihe.gazelle.maestro.api.business.testreport;

import net.ihe.gazelle.maestro.api.business.message.*;
import net.ihe.gazelle.maestro.api.technical.dto.message.*;
import net.ihe.gazelle.maestro.api.technical.dto.report.CriterionReportDTO;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class GradingReportTest {
    @Test
    void gradingCriteriaSurviveMessageSerialization() throws Exception {
        var criterion = new CriterionReportBuilder().setName("Document is readable")
                .setGradingResult(CriterionResult.PASSED).build();
        var completed = new GradingCompleted().setSessionId("execution").setResult(StepResult.PASSED)
                .setCriteriaReport(List.of(criterion)).setNote("Reviewed");
        var bytes = new ByteArrayOutputStream();
        try (var output = new ObjectOutputStream(bytes)) { output.writeObject(completed); }
        GradingCompleted restored;
        try (var input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            restored = (GradingCompleted) input.readObject();
        }
        assertEquals("execution", restored.getSessionId());
        assertEquals(StepResult.PASSED, restored.getResult());
        assertEquals("Reviewed", restored.getNote());
        assertEquals(List.of(criterion), restored.getCriteriaReport());
        assertEquals(criterion.hashCode(), restored.getCriteriaReport().getFirst().hashCode());
        var dto = new GradingCompletedDTO(restored, "authorization");
        assertEquals("execution", dto.getMaestroSessionId());
        assertEquals("PASSED", dto.getResult());
        assertEquals("Reviewed", dto.getNote());
        assertEquals("authorization", dto.getAuthorization());
        assertSame(restored, dto.getBusinessObject());
        assertEquals(GradingCompleted.class, dto.getBusinessObjectClass());
        assertEquals("Document is readable", dto.getCriteriaReport().getFirst().getDescription());
        assertNotNull(new GradingCompletedDTO());
    }

    @Test
    void criterionDtoRoundTripPreservesResultAndBuilderRejectsIncompleteGrading() {
        var criterion = new CriterionReport().setDescription("Criterion").setCriterionResult(CriterionResult.FAILED);
        var serializer = new JacksonSerDes();
        var restored = serializer.deserialize(serializer.serializeAsString(new CriterionReportDTO(criterion)),
                CriterionReportDTO.class).getBusinessObject();
        assertEquals(criterion, restored);
        assertEquals(criterion, new CriterionReportBuilder(criterion).build());
        assertNotEquals(null, criterion);
        assertNotEquals(criterion, new CriterionReport().setDescription("Other"));
        assertTrue(criterion.isNameDefined());
        assertTrue(criterion.isGradingResultDefined());
        assertFalse(new CriterionReport().setDescription(" ").isNameDefined());
        assertFalse(new CriterionReport().setCriterionResult(null).isGradingResultDefined());
        var emptyBuilder = new CriterionReportBuilder();
        assertThrows(RuntimeException.class, emptyBuilder::build);
        var nullBuilder = new CriterionReportBuilder((CriterionReport) null);
        assertThrows(RuntimeException.class, nullBuilder::build);
        assertEquals("FAILED", new CriterionReportDTO(restored).getGradingResult());
    }

    @Test
    void gradingRequestDtoRoundTripPreservesInstructionsAndCriteria() {
        var request = new AskGrading().setDescription("Review the document").setCriteria(List.of("Readable"))
                .setTimeout(1000L);
        var serializer = new JacksonSerDes();
        var restored = serializer.deserialize(serializer.serializeAsString(new AskGradingDTO(request)),
                AskGradingDTO.class).getBusinessObject();
        assertEquals(request.getDescription(), restored.getDescription());
        assertEquals(request.getCriteria(), restored.getCriteria());
        assertEquals(1000L, request.getTimeout());
        assertEquals(AskGrading.class, new AskGradingDTO(request).getBusinessObjectClass());
    }
    @Test
    void evidenceDeletionWireFormatPreservesTarget() {
        var deletion = new DeleteEvidence().setSessionId("execution").setRunId(0)
                .setStepName("first").setEvidenceName("note");
        var serializer = new JacksonSerDes();
        var dto = new DeleteEvidenceDTO(deletion, "authorization");
        var restored = serializer.deserialize(serializer.serializeAsString(dto), DeleteEvidenceDTO.class);
        assertEquals(deletion, restored.getBusinessObject());
        assertEquals("authorization", dto.getAuthorization());
        assertEquals(DeleteEvidence.class, dto.getBusinessObjectClass());
    }
    @Test
    void gradingMetadataSurvivesSerializationAndJsonTransport() {
        var when = java.time.Instant.parse("2026-11-02T12:00:00Z");
        var criterion = new CriterionReport().setDescription("Readable")
                .setCriterionResult(CriterionResult.PASSED).setUserId("reviewer").setDateTime(when);
        var serializer = new JacksonSerDes();
        var restored = serializer.deserialize(serializer.serializeAsString(new CriterionReportDTO(criterion)),
                CriterionReportDTO.class).getBusinessObject();
        assertEquals(criterion, restored);
        assertEquals(criterion.hashCode(), restored.hashCode());
        assertEquals("reviewer", restored.getUserId());
        assertEquals(when, restored.getDateTime());
        var completed = new GradingCompleted().setResult(StepResult.PASSED).setCriteriaReport(List.of(criterion))
                .setUserId("reviewer").setDateTime(when);
        var dto = new GradingCompletedDTO(completed, "");
        assertEquals("reviewer", dto.getUserId());
        assertEquals(when, java.time.OffsetDateTime.parse(dto.getDateTime()).toInstant());
        var input = new GradingCompletedDTO();
        assertNull(input.getDateTime());
        input.setDateTime(null);
        assertNull(input.getBusinessObject().getDateTime());
        input.setDateTime(dto.getDateTime());
        input.setUserId(dto.getUserId());
        assertEquals(when, input.getBusinessObject().getDateTime());
        assertEquals("reviewer", input.getBusinessObject().getUserId());
        var emptyCriterion = new CriterionReportDTO();
        assertNull(emptyCriterion.getDateTime());
        emptyCriterion.setDateTime(null);
        assertNull(emptyCriterion.getBusinessObject().getDateTime());
    }
}
