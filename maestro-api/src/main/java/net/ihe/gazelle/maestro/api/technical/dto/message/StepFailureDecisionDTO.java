/*
 * Copyright 2025-2026 IHE International.
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
import net.ihe.gazelle.oidc.common.technical.dto.SecuredMessageDTO;
import net.ihe.gazelle.maestro.api.business.message.StepFailureDecision;

/** Wire representation of {@link StepFailureDecision}. */
@JsonTypeName(StepFailureDecisionDTO.TYPE)
public class StepFailureDecisionDTO extends MessageDTO<StepFailureDecision> implements SecuredMessageDTO {
    private String authorization;

    @Override
    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    @JsonIgnore
    public static final String TYPE = "STEP_FAILURE_DECISION";

    public StepFailureDecisionDTO() {
        this(null);
    }

    public StepFailureDecisionDTO(StepFailureDecision message) {
        super(message);
    }

    @JsonCreator
    public StepFailureDecisionDTO(
            @JsonProperty(value = "maestroSessionId", required = true) String maestroSessionId,
            @JsonProperty(value = "stepIndex", required = true) int stepIndex,
            @JsonProperty(value = "continueExecution", required = true) boolean continueExecution) {
        this(new StepFailureDecision(maestroSessionId, stepIndex, continueExecution));
    }

    @JsonProperty("maestroSessionId")
    public String getMaestroSessionId() {
        return message.maestroSessionId();
    }

    @JsonProperty("stepIndex")
    public int getStepIndex() {
        return message.stepIndex();
    }

    @JsonProperty("continueExecution")
    public boolean getContinueExecution() {
        return message.continueExecution();
    }

    @Override
    @JsonIgnore
    public Class<StepFailureDecision> getBusinessObjectClass() {
        return StepFailureDecision.class;
    }
}
