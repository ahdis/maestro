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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import net.ihe.gazelle.maestro.api.business.message.PutEvidence;
import net.ihe.gazelle.maestro.api.technical.dto.property.PropertyDTO;
import net.ihe.gazelle.modelmarshaller.technical.dto.SubTypingDTO;

/** Outbound acknowledgement of a recorded evidence revision. */
@JsonTypeName("EVIDENCE_RECORDED")
public class EvidenceRecordedDTO extends MessageDTO<PutEvidence> {
    public EvidenceRecordedDTO(PutEvidence evidence) {
        super(evidence);
    }

    @JsonProperty("runId")
    public Integer getRunId() {
        return message.getRunId();
    }

    @JsonProperty("stepName")
    public String getStepName() {
        return message.getStepName();
    }

    @JsonProperty("content")
    public PropertyDTO<?> getContent() {
        return (PropertyDTO<?>) SubTypingDTO.fromBusinessObject(message.getContent());
    }

    @Override
    @JsonIgnore
    public Class<PutEvidence> getBusinessObjectClass() {
        return PutEvidence.class;
    }
}
