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
import net.ihe.gazelle.maestro.api.business.message.AskGrading;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Schema(
        name = "AskGrading",
        description = "Message sent when waiting for user grading."
)
@JsonTypeName(AskGradingDTO.TYPE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "type",
        "description",
        "criteria"
})
public class AskGradingDTO extends MessageDTO<AskGrading> {

    @JsonIgnore
    public static final String TYPE = "ASK_GRADING";

    public AskGradingDTO() {
        super(new AskGrading());
    }

    public AskGradingDTO(AskGrading message) {
        super(message);
    }

    @Schema(
            name = "description",
            description = "The description of what to do for grading the test."
    )
    @JsonProperty("description")
    public String getDescription() {
        return message.getDescription();
    }

    public void setDescription(String description) {
        message.setDescription(description);
    }

    @Schema(
            name = "criteria",
            description = "The list of acceptance criterion for grading the test."
    )
    @JsonProperty("criteria")
    public List<String> getCriteria() {
        return message.getCriteria();
    }

    public void setCriteria(List<String> criteria) {
        message.setCriteria(criteria);
    }

    @Override
    @JsonIgnore
    public Class<AskGrading> getBusinessObjectClass() {
        return AskGrading.class;
    }
}
