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
import net.ihe.gazelle.maestro.api.business.message.GradingCompleted;
import net.ihe.gazelle.maestro.api.business.testreport.StepResult;
import net.ihe.gazelle.maestro.api.technical.dto.report.CriterionReportDTO;
import net.ihe.gazelle.oidc.common.technical.dto.SecuredMessageDTO;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Schema(
        name = "GradingCompleted",
        description = "Message sent when the grading is completed."
)
@JsonTypeName(GradingCompletedDTO.TYPE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "type",
        "maestroSessionId",
        "result",
        "criteriaReport",
        "note"
})
public class GradingCompletedDTO extends MessageDTO<GradingCompleted> implements SecuredMessageDTO {

    @JsonIgnore
    public static final String TYPE = "GRADING_COMPLETED";

    private final String authorization;

    public GradingCompletedDTO() {
        this(new GradingCompleted(), "");
    }

    public GradingCompletedDTO(GradingCompleted message, String authorization) {
        super(message);
        this.authorization = authorization;
    }

    @Schema(
            name = "maestroSessionId",
            description = "The id of the maestro execution session where this test is grading."
    )
    @JsonProperty("maestroSessionId")
    public String getMaestroSessionId() {
        return message.getSessionId();
    }

    @Schema(
            name = "result",
            description = "The result of the grading.",
            enumeration = {StepResult.RESULT_PASSED, StepResult.RESULT_FAILED}
    )
    @JsonProperty("result")
    public String getResult() {
        return message.getResult().name();
    }

    @Schema(
            name = "criteriaReport",
            description = "The list of acceptance criterion associated to this test."
    )
    @JsonProperty("criteriaReport")
    public List<CriterionReportDTO> getCriteriaReport() {
        return message.getCriteriaReport()
                .stream()
                .map(CriterionReportDTO::new)
                .toList();
    }

    @Schema(
            name = "note",
            description = "The note associated with the grading."
    )
    @JsonProperty("note")
    public String getNote() {
        return message.getNote();
    }

    @Override
    public Class<GradingCompleted> getBusinessObjectClass() {
        return GradingCompleted.class;
    }

    @Override
    public String getAuthorization() {
        return authorization;
    }
}
