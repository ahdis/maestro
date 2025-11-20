/*
 * Copyright 2025 Kereval.
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

package net.ihe.gazelle.maestro.quarkus.websocket.dto;

import com.fasterxml.jackson.annotation.*;
import net.ihe.gazelle.errorhandling.technical.UnexpectedErrorDTO;
import net.ihe.gazelle.maestro.api.business.message.StepRunFinished;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@JsonTypeName(StepRunFinishedDTO.TYPE)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "type",
        "testId",
        "stepId",
        "dateTime",
        "result",
        "unexpectedErrors"
})
public class StepRunFinishedDTO extends MessageDTO<StepRunFinished> {

    @JsonIgnore
    public static final String TYPE = "STEP_RUN_FINISHED";

    public StepRunFinishedDTO() {
        this(new StepRunFinished());
    }

    public StepRunFinishedDTO(StepRunFinished message) {
        super(message);
    }

    @JsonGetter("testId")
    public String getTestRunId() {
        return message.getTestId();
    }

    @JsonGetter("stepId")
    public String getStepName() {
        return message.getStepRun().getId();
    }

    @JsonGetter("dateTime")
    public String getDateTime() {
        return ZonedDateTime.ofInstant(message.getStepRunReport().getDateTime(), ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    @JsonGetter("result")
    public String getResult() {
        return message.getStepRunReport().getResult().name();
    }

    @JsonGetter("unexpectedErrors")
    public List<UnexpectedErrorDTO> getUnexpectedErrors() {
        return message.getStepRunReport()
                .getUnexpectedErrors()
                .stream()
                .map(UnexpectedErrorDTO::new)
                .toList();
    }

    @Override
    @JsonIgnore
    public Class<StepRunFinished> getBusinessObjectClass() {
        return StepRunFinished.class;
    }
}
