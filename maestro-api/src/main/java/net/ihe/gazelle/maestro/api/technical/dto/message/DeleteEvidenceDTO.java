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

package net.ihe.gazelle.maestro.api.technical.dto.message;

import com.fasterxml.jackson.annotation.*;
import net.ihe.gazelle.maestro.api.business.message.DeleteEvidence;
import net.ihe.gazelle.oidc.common.technical.dto.SecuredMessageDTO;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(
        name = "DeleteEvidence",
        description = "Message sent when an evidence is deleted from a test step."
)
@JsonTypeName(DeleteEvidenceDTO.TYPE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "type",
        "maestroSessionId",
        "runId",
        "stepName",
        "evidenceName"
})
public class DeleteEvidenceDTO extends MessageDTO<DeleteEvidence> implements SecuredMessageDTO {

    @JsonIgnore
    public static final String TYPE = "DELETE_EVIDENCE";

    private final String authorization;

    public DeleteEvidenceDTO() {
        this(new DeleteEvidence(), "");
    }

    public DeleteEvidenceDTO(DeleteEvidence message, String authorization) {
        super(message);
        this.authorization = authorization;
    }

    @Schema(
            name = "maestroSessionId",
            description = "The id of maestro the execution session where this evidence is deleted."
    )
    @JsonProperty("maestroSessionId")
    public String getMaestroSessionId() {
        return message.getSessionId();
    }

    public void setMaestroSessionId(String sessionId) {
        message.setSessionId(sessionId);
    }

    @Schema(
            name = "runId",
            description = "The id of the test run execution where this evidence is deleted."
    )
    @JsonProperty("runId")
    public Integer getRunId() {
        return message.getRunId();
    }

    public void setRunId(Integer runId) {
        message.setRunId(runId);
    }

    @Schema(
            name = "stepName",
            description = "The name of the step where this evidence is deleted."
    )
    @JsonProperty("stepName")
    public String getStepName() {
        return message.getStepName();
    }

    public void setStepName(String stepId) {
        message.setStepName(stepId);
    }

    @Schema(
            name = "evidenceName",
            description = "The name of the evidence to delete."
    )
    @JsonProperty("evidenceName")
    public String getEvidenceName() {
        return message.getEvidenceName();
    }

    public void setEvidenceName(String evidenceName) {
        message.setEvidenceName(evidenceName);
    }

    @Override
    @JsonIgnore
    public Class<DeleteEvidence> getBusinessObjectClass() {
        return DeleteEvidence.class;
    }

    @Override
    public String getAuthorization() {
        return authorization;
    }
}
