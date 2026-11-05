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
import java.time.format.DateTimeFormatter;
import java.time.ZoneOffset;
import java.util.List;
import net.ihe.gazelle.maestro.api.business.message.StepOutputsAvailable;
import net.ihe.gazelle.maestro.api.technical.dto.property.PropertyDTO;
import net.ihe.gazelle.modelmarshaller.technical.dto.SubTypingDTO;

/** Outbound notification; execution status and step completion remain separate messages. */
@JsonTypeName(StepOutputsAvailableDTO.TYPE)
public class StepOutputsAvailableDTO extends MessageDTO<StepOutputsAvailable> {
    @JsonIgnore public static final String TYPE = "STEP_OUTPUTS_AVAILABLE";
    public StepOutputsAvailableDTO() { this(new StepOutputsAvailable(null, null, null, List.of())); }
    public StepOutputsAvailableDTO(StepOutputsAvailable message) { super(message); }
    @JsonProperty("testId") public String getTestId() { return message.testId(); }
    @JsonProperty("stepName") public String getStepName() { return message.stepName(); }
    @JsonProperty("dateTime") public String getDateTime() {
        return message.dateTime() == null ? null : DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(message.dateTime().atOffset(ZoneOffset.UTC));
    }
    @SuppressWarnings("rawtypes")
    @JsonProperty("outputs")
    public List<PropertyDTO> getOutputs() {
        return message.outputs().stream()
                .map(SubTypingDTO::fromBusinessObject)
                .map(PropertyDTO.class::cast)
                .toList();
    }
    @Override @JsonIgnore public Class<StepOutputsAvailable> getBusinessObjectClass() { return StepOutputsAvailable.class; }
}
