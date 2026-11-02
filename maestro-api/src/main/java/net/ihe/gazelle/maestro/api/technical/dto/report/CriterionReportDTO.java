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

package net.ihe.gazelle.maestro.api.technical.dto.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import net.ihe.gazelle.maestro.api.business.testreport.CriterionReport;
import net.ihe.gazelle.maestro.api.business.testreport.CriterionResult;
import net.ihe.gazelle.modelmarshaller.technical.dto.DTO;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(
        name = "CriterionReport",
        description = "The report for an acceptance criterion."
)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "description",
        "result"
})
public class CriterionReportDTO implements DTO<CriterionReport> {

    @JsonIgnore
    private final CriterionReport criterionReport;

    public CriterionReportDTO() {
        this(new CriterionReport());
    }

    public CriterionReportDTO(CriterionReport criterionReport) {
        this.criterionReport = criterionReport;
    }

    @Override
    @JsonIgnore
    public CriterionReport getBusinessObject() {
        return criterionReport;
    }

    @Schema(
            name = "criterionDescription",
            description = "The description of the criterion.",
            required = true
    )
    @JsonProperty(value = "criterionDescription")
    public String getDescription() {
        return criterionReport.getDescription();
    }

    public void setDescription(String description) {
        criterionReport.setDescription(description);
    }

    @Schema(
            name = "criterionResult",
            description = "The result of the criterion.",
            enumeration = {CriterionResult.RESULT_PASSED, CriterionResult.RESULT_FAILED, CriterionResult.RESULT_NOT_VERIFIED},
            required = true
    )
    @JsonProperty(value = "criterionResult")
    public String getGradingResult() {
        return criterionReport.getCriterionResult().name();
    }

    public void setGradingResult(String gradingResult) {
        criterionReport.setCriterionResult(CriterionResult.valueOf(gradingResult));
    }
}
