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
import net.ihe.gazelle.maestro.api.business.testreport.GradingCriterion;
import net.ihe.gazelle.maestro.api.business.testreport.CriterionResult;
import net.ihe.gazelle.modelmarshaller.technical.dto.DTO;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Schema(
        name = "GradingCriterion",
        description = "The report for an acceptance criterion."
)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "criterionDescription",
        "criterionResult",
        "userId",
        "dateTime"
})
public class GradingCriterionDTO implements DTO<GradingCriterion> {

    @JsonIgnore
    private final GradingCriterion gradingCriterion;

    public GradingCriterionDTO() {
        this(new GradingCriterion());
    }

    public GradingCriterionDTO(GradingCriterion gradingCriterion) {
        this.gradingCriterion = gradingCriterion;
    }

    @Override
    @JsonIgnore
    public GradingCriterion getBusinessObject() {
        return gradingCriterion;
    }

    @Schema(
            name = "criterionDescription",
            description = "The description of the criterion.",
            required = true
    )
    @JsonProperty(value = "criterionDescription")
    public String getDescription() {
        return gradingCriterion.getDescription();
    }

    public void setDescription(String description) {
        gradingCriterion.setDescription(description);
    }

    @Schema(
            name = "criterionResult",
            description = "The result of the criterion.",
            enumeration = {CriterionResult.RESULT_PASSED, CriterionResult.RESULT_FAILED, CriterionResult.RESULT_NOT_VERIFIED},
            required = true
    )
    @JsonProperty(value = "criterionResult")
    public String getGradingResult() {
        return gradingCriterion.getCriterionResult().name();
    }

    public void setGradingResult(String gradingResult) {
        gradingCriterion.setCriterionResult(CriterionResult.valueOf(gradingResult));
    }

    @Schema(
            name = "userId",
            description = "The id of the user who assessed the criterion."
    )
    @JsonProperty("userId")
    public String getUserId() {
        return gradingCriterion.getUserId();
    }

    public void setUserId(String userId) {
        gradingCriterion.setUserId(userId);
    }

    @Schema(
            name = "dateTime",
            description = "The date time of the criterion assessment."
    )
    @JsonProperty("dateTime")
    public String getDateTime() {
        if (gradingCriterion.getDateTime() != null) {
            return ZonedDateTime.ofInstant(gradingCriterion.getDateTime(), ZoneId.systemDefault())
                    .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } else {
            return null;
        }
    }

    public void setDateTime(String dateTime) {
        if (dateTime != null) {
            gradingCriterion.setDateTime(
                    Instant.from(ZonedDateTime.parse(dateTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME))
            );
        }
    }
}
