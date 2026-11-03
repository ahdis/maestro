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

import net.ihe.gazelle.maestro.api.business.message.DeleteEvidence;
import net.ihe.gazelle.maestro.api.business.message.GradingCompleted;
import net.ihe.gazelle.maestro.api.business.message.GradingResult;
import net.ihe.gazelle.maestro.api.technical.dto.message.DeleteEvidenceDTO;
import net.ihe.gazelle.maestro.api.technical.dto.message.GradingCompletedDTO;
import net.ihe.gazelle.maestro.api.technical.dto.report.GradingCriterionDTO;
import net.ihe.gazelle.modelmarshaller.technical.jackson.JacksonSerDes;
import net.ihe.gazelle.modelmarshaller.technical.serialization.TextSerDes;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GradingReportTest {

    @Test
    void gradingCriteriaSurviveMessageSerialization() throws Exception {
        GradingCriterion criterion = new GradingCriterionBuilder()
                .setName("Document is readable")
                .setGradingResult(CriterionResult.PASSED)
                .build();
        GradingCompleted completed = new GradingCompleted()
                .setSessionId("execution")
                .setResult(GradingResult.PASSED)
                .setGradingCriteria(List.of(criterion))
                .setNote("Reviewed");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(completed);
        }
        GradingCompleted restored;
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            restored = (GradingCompleted) input.readObject();
        }
        assertEquals("execution", restored.getSessionId());
        assertEquals(GradingResult.PASSED, restored.getResult());
        assertEquals("Reviewed", restored.getNote());
        assertEquals(List.of(criterion), restored.getGradingCriteria());
        assertEquals(criterion.hashCode(), restored.getGradingCriteria().getFirst().hashCode());
        GradingCompletedDTO dto = new GradingCompletedDTO(restored, "authorization");
        assertEquals("execution", dto.getMaestroSessionId());
        assertEquals("PASSED", dto.getResult());
        assertEquals("Reviewed", dto.getNote());
        assertEquals("authorization", dto.getAuthorization());
        assertSame(restored, dto.getBusinessObject());
        assertEquals(GradingCompleted.class, dto.getBusinessObjectClass());
        assertEquals("Document is readable", dto.getCriteriaReport().getFirst().getDescription());
    }

    @Test
    void criterionDtoRoundTripPreservesResultAndBuilderRejectsIncompleteGrading() {
        GradingCriterion criterion = new GradingCriterion()
                .setDescription("Criterion")
                .setCriterionResult(CriterionResult.FAILED);
        TextSerDes serializer = new JacksonSerDes();
        GradingCriterion restored = serializer.deserialize(serializer.serializeAsString(new GradingCriterionDTO(criterion)),
                GradingCriterionDTO.class).getBusinessObject();
        assertEquals(criterion, restored);
        assertEquals(criterion, new GradingCriterionBuilder(criterion).build());
        assertNotEquals(null, criterion);
        assertNotEquals(criterion, new GradingCriterion().setDescription("Other"));
        assertTrue(criterion.isNameDefined());
        assertTrue(criterion.isGradingResultDefined());
        assertFalse(new GradingCriterion().setDescription(" ").isNameDefined());
        assertFalse(new GradingCriterion().setCriterionResult(null).isGradingResultDefined());
        GradingCriterionBuilder emptyBuilder = new GradingCriterionBuilder();
        assertThrows(RuntimeException.class, emptyBuilder::build);
        GradingCriterionBuilder nullBuilder = new GradingCriterionBuilder((GradingCriterion) null);
        assertThrows(RuntimeException.class, nullBuilder::build);
        assertEquals("FAILED", new GradingCriterionDTO(restored).getGradingResult());
    }

    @Test
    void evidenceDeletionWireFormatPreservesTarget() {
        DeleteEvidence deletion = new DeleteEvidence()
                .setSessionId("execution")
                .setRunId(0)
                .setStepName("first")
                .setEvidenceName("note");
        TextSerDes serializer = new JacksonSerDes();
        DeleteEvidenceDTO dto = new DeleteEvidenceDTO(deletion, "authorization");
        DeleteEvidenceDTO restored = serializer.deserialize(serializer.serializeAsString(dto), DeleteEvidenceDTO.class);
        assertEquals(deletion, restored.getBusinessObject());
        assertEquals("authorization", dto.getAuthorization());
        assertEquals(DeleteEvidence.class, dto.getBusinessObjectClass());
    }
    @Test
    void gradingMetadataSurvivesSerializationAndJsonTransport() {
        Instant when = Instant.parse("2026-11-02T12:00:00Z");
        GradingCriterion criterion = new GradingCriterion()
                .setDescription("Readable")
                .setCriterionResult(CriterionResult.PASSED)
                .setUserId("reviewer")
                .setDateTime(when);
        TextSerDes serializer = new JacksonSerDes();
        GradingCriterion restored = serializer.deserialize(serializer.serializeAsString(new GradingCriterionDTO(criterion)),
                GradingCriterionDTO.class).getBusinessObject();
        assertEquals(criterion, restored);
        assertEquals(criterion.hashCode(), restored.hashCode());
        assertEquals("reviewer", restored.getUserId());
        assertEquals(when, restored.getDateTime());
        GradingCompleted completed = new GradingCompleted()
                .setResult(GradingResult.PASSED)
                .setGradingCriteria(List.of(criterion))
                .setUserId("reviewer")
                .setDateTime(when);
        GradingCompletedDTO dto = new GradingCompletedDTO(completed, "");
        assertEquals("reviewer", dto.getUserId());
        assertEquals(when, OffsetDateTime.parse(dto.getDateTime()).toInstant());
        GradingCompletedDTO input = new GradingCompletedDTO();
        assertNull(input.getDateTime());
        input.setDateTime(null);
        assertNull(input.getBusinessObject().getDateTime());
        input.setDateTime(dto.getDateTime());
        input.setUserId(dto.getUserId());
        assertEquals(when, input.getBusinessObject().getDateTime());
        assertEquals("reviewer", input.getBusinessObject().getUserId());
        GradingCriterionDTO emptyCriterion = new GradingCriterionDTO();
        assertNull(emptyCriterion.getDateTime());
        emptyCriterion.setDateTime(null);
        assertNull(emptyCriterion.getBusinessObject().getDateTime());
    }
}
