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

package net.ihe.gazelle.maestro.quarkus.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import net.ihe.gazelle.maestro.api.technical.dto.message.PutEvidenceDTO;
import net.ihe.gazelle.maestro.api.technical.dto.property.PropertyDTO;
import net.ihe.gazelle.maestro.api.technical.dto.report.StepRunReportDTO;
import net.ihe.gazelle.maestro.api.technical.dto.testrun.TestRunDTO;
import net.ihe.gazelle.maestro.engine.business.context.TestRunSession;
import net.ihe.gazelle.modelmarshaller.technical.dto.DTO;
import net.ihe.gazelle.modelmarshaller.technical.dto.SubTypingDTO;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "testSessionId",
        "testRun",
        "context",
        "stepCursor",
        "stepReports",
        "evidences",
        "expirationTimestamp"
})
public class TestRunSessionDTO implements DTO<TestRunSession> {

    @JsonIgnore
    private final TestRunSession testRunSession;

    public TestRunSessionDTO() {
        this(new TestRunSession());
    }

    public TestRunSessionDTO(TestRunSession testRunSession) {
        this.testRunSession = testRunSession;
    }

    @JsonProperty("testSessionId")
    public String getTestSessionId() {
        return testRunSession.getTestSessionId();
    }

    public void setTestSessionId(String testSessionId) {
        testRunSession.setTestSessionId(testSessionId);
    }

    @JsonProperty("promptOnStepFailure")
    public boolean isPromptOnStepFailure() {
        return testRunSession.isPromptOnStepFailure();
    }

    public void setPromptOnStepFailure(boolean promptOnStepFailure) {
        testRunSession.setPromptOnStepFailure(promptOnStepFailure);
    }

    @JsonProperty("testRun")
    public TestRunDTO getTestRun() {
        return new TestRunDTO(testRunSession.getTestRun());
    }

    public void setTestRun(TestRunDTO testRun) {
        testRunSession.setTestRun(testRun.getBusinessObject());
    }

    @SuppressWarnings("rawtypes")
    @JsonProperty("context")
    public List<PropertyDTO> getContext() {
        return testRunSession.getContext().stream()
                .map(SubTypingDTO::fromBusinessObject)
                .map(PropertyDTO.class::cast)
                .toList();
    }

    @SuppressWarnings("rawtypes")
    public void setContext(List<PropertyDTO> context) {
        testRunSession.setContext(
                context.stream()
                        .map(PropertyDTO::getBusinessObject)
                        .toList()
        );
    }

    @JsonProperty("stepCursor")
    public int getStepCursor() {
        return testRunSession.currentStepIndex();
    }

    public void setStepCursor(int stepCursor) {
        testRunSession.setStepCursor(stepCursor);
    }

    @JsonProperty("stepReports")
    public List<StepRunReportDTO> getStepReports() {
        return testRunSession.getStepReports().stream()
                .map(StepRunReportDTO::new)
                .toList();
    }

    public void setStepReports(List<StepRunReportDTO> stepReports) {
        testRunSession.setStepReports(
                stepReports.stream()
                        .map(StepRunReportDTO::getBusinessObject)
                        .toList()
        );
    }

    @JsonProperty("evidences")
    public List<PutEvidenceDTO> getEvidences() {
        return testRunSession.getEvidences().stream()
                .map(PutEvidenceDTO::new)
                .toList();
    }

    public void setEvidences(List<PutEvidenceDTO> evidences) {
        testRunSession.setEvidences(
                evidences.stream()
                        .map(PutEvidenceDTO::getBusinessObject)
                        .toList()
        );
    }

    @Schema(
            name = "expirationTimestamp",
            description = "The zoned date time of the step execution expiration.",
            examples = "2025-01-01T16:30:00.387+01:00",
            required = true
    )
    @JsonProperty("expirationTimestamp")
    public String getExpirationTimestamp() {
        if (testRunSession.getExpirationTimestamp() != null) {
            return testRunSession.getExpirationTimestamp().toString();
        } else {
            return null;
        }
    }

    public void setExpirationTimestamp(String expirationTimestamp) {
        if (expirationTimestamp != null) {
            testRunSession.setExpirationTimestamp(
                    Instant.parse(expirationTimestamp)
            );
        }
    }

    @Override
    public TestRunSession getBusinessObject() {
        return testRunSession;
    }
}
