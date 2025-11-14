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
import net.ihe.gazelle.maestro.api.business.testreport.Result;
import net.ihe.gazelle.maestro.api.business.testreport.TestReportSummary;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@JsonTypeName(TestSuiteRunFinishedDTO.TYPE)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "type",
        "dateTime",
        "result",
        "unexpectedErrors"
})
public class TestSuiteRunFinishedDTO extends MessageDTO<TestReportSummary> {

    @JsonIgnore
    public static final String TYPE = "TEST_SUITE_RUN_FINISHED";

    public TestSuiteRunFinishedDTO() {
        this(new TestReportSummary());
    }

    public TestSuiteRunFinishedDTO(TestReportSummary testReportSummary) {
        super(testReportSummary);
    }

    @JsonGetter("dateTime")
    public String getDateTime() {
        return ZonedDateTime.ofInstant(getBusinessObject().getDateTime(), ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    @JsonSetter("dateTime")
    public void setDateTime(String dateTime) {
        getBusinessObject().setDateTime(
                Instant.from(ZonedDateTime.parse(dateTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME))
        );
    }

    @JsonGetter("result")
    public String getResult() {
        return getBusinessObject().getResult().name();
    }

    @JsonSetter("result")
    public void setResult(String result) {
        getBusinessObject().setResult(Result.valueOf(result));
    }

    @JsonGetter("unexpectedErrors")
    public List<UnexpectedErrorDTO> getUnexpectedErrors() {
        return getBusinessObject().getUnexpectedErrors().stream().map(UnexpectedErrorDTO::new).toList();
    }

    @JsonSetter("unexpectedErrors")
    public void setUnexpectedErrors(List<UnexpectedErrorDTO> unexpectedErrorDTOList) {
        getBusinessObject().setUnexpectedErrors(
                unexpectedErrorDTOList.stream().map(UnexpectedErrorDTO::getBusinessObject).toList()
        );
    }

    @Override
    public Class<TestReportSummary> getBusinessObjectClass() {
        return TestReportSummary.class;
    }
}
