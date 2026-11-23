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
import net.ihe.gazelle.maestro.api.business.message.StepFailureDecisionRequested;
import net.ihe.gazelle.maestro.api.business.testreport.StepResult;


/** Wire representation of {@link StepFailureDecisionRequested}. */
@JsonTypeName(StepFailureDecisionRequestedDTO.TYPE)
public class StepFailureDecisionRequestedDTO extends MessageDTO<StepFailureDecisionRequested> {
    @JsonIgnore
    public static final String TYPE = "STEP_FAILURE_DECISION_REQUESTED";

    public StepFailureDecisionRequestedDTO() {
        this(null);
    }

    public StepFailureDecisionRequestedDTO(StepFailureDecisionRequested message) {
        super(message);
    }

    @JsonCreator
    public StepFailureDecisionRequestedDTO(
            @JsonProperty(value = "maestroSessionId", required = true) String maestroSessionId,
            @JsonProperty(value = "stepIndex", required = true) int stepIndex,
            @JsonProperty(value = "stepName", required = true) String stepName,
            @JsonProperty(value = "result", required = true) StepResult result) {
        this(new StepFailureDecisionRequested(maestroSessionId, stepIndex, stepName, result));
    }

    @JsonProperty("maestroSessionId")
    public String getMaestroSessionId() {
        return message.maestroSessionId();
    }

    @JsonProperty("stepIndex")
    public int getStepIndex() {
        return message.stepIndex();
    }

    @JsonProperty("stepName")
    public String getStepName() {
        return message.stepName();
    }

    @JsonProperty("result")
    public StepResult getResult() {
        return message.result();
    }

    @Override
    @JsonIgnore
    public Class<StepFailureDecisionRequested> getBusinessObjectClass() {
        return StepFailureDecisionRequested.class;
    }
}
