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

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import net.ihe.gazelle.maestro.api.business.message.StepRunStarted;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@JsonTypeName(StepRunStartedDTO.TYPE)
@JsonPropertyOrder({
        "type",
        "testId",
        "stepId",
        "dateTime"
})
public class StepRunStartedDTO extends MessageDTO<StepRunStarted> {

    @JsonIgnore
    public static final String TYPE = "START_STEP_RUN";

    public StepRunStartedDTO() {
        super(new StepRunStarted());
    }

    public StepRunStartedDTO(StepRunStarted message) {
        super(message);
    }

    @JsonGetter("testId")
    public String getTestRunId() {
        return getBusinessObject().getTestId();
    }

    @JsonGetter("stepId")
    public String getStepName() {
        return getBusinessObject().getStepId();
    }

    @JsonGetter("dateTime")
    public String getDateTime() {
        return ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    @Override
    @JsonIgnore
    public Class<StepRunStarted> getBusinessObjectClass() {
        return StepRunStarted.class;
    }
}
