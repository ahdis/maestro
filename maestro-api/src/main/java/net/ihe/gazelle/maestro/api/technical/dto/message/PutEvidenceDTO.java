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
import net.ihe.gazelle.maestro.api.business.message.PutEvidence;
import net.ihe.gazelle.maestro.api.technical.dto.property.PropertyDTO;
import net.ihe.gazelle.modelmarshaller.technical.dto.SubTypingDTO;
import net.ihe.gazelle.oidc.common.technical.dto.SecuredMessageDTO;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(
        name = "PutEvidence",
        description = "Message sent when a new evidence is added to a test step."
)
@JsonTypeName(PutEvidenceDTO.TYPE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "type",
        "runId",
        "stepId",
        "content"
})
public class PutEvidenceDTO extends MessageDTO<PutEvidence> implements SecuredMessageDTO {

    @JsonIgnore
    public static final String TYPE = "PUT_EVIDENCE";

    private final String authorization;

    public PutEvidenceDTO() {
        this(new PutEvidence(), "");
    }

    public PutEvidenceDTO(PutEvidence message, String authorization) {
        super(message);
        this.authorization = authorization;
    }

    @Schema(
            name = "runId",
            description = "The id of the test run execution where this evidence is added."
    )
    @JsonProperty("runId")
    public Integer getRunId() {
        return message.getRunId();
    }

    public void setRunId(Integer runId) {
        message.setRunId(runId);
    }

    @Schema(
            name = "stepId",
            description = "The id of the step where this evidence is added."
    )
    @JsonProperty("stepId")
    public Integer getStepId() {
        return message.getStepId();
    }

    public void setStepId(Integer stepId) {
        message.setStepId(stepId);
    }

    @Schema(
            name = "content",
            description = "The content of the evidence."
    )
    @JsonProperty("content")
    public PropertyDTO<?> getContent() {
        return (PropertyDTO<?>) SubTypingDTO.fromBusinessObject(message.getContent());
    }

    public void setContent(PropertyDTO<?> content) {
        message.setContent(content.getBusinessObject());
    }

    @Override
    public Class<PutEvidence> getBusinessObjectClass() {
        return PutEvidence.class;
    }

    @Override
    public String getAuthorization() {
        return authorization;
    }
}
