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

package net.ihe.gazelle.maestro.technical.dto.report;

import com.fasterxml.jackson.annotation.*;
import net.ihe.gazelle.maestro.api.business.testreport.Result;
import net.ihe.gazelle.maestro.api.business.testreport.TestRunReport;

import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "runId", "dateTime", "result", "test", "inputs", "outputs", "stepRunReports", "urlToTestRun", "unexpectedErrors"
})
public class TestRunReportDTO {

    public static final String ISO_8601_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    @JsonIgnore
    private final TestRunReport domain;

    public TestRunReportDTO() {
        this.domain = new TestRunReport();
    }

    public TestRunReportDTO(TestRunReport domain) {
        this.domain = domain;
    }

    @JsonGetter(value = "runId")
    public String getRunId() {
        return domain.getRunId();
    }

    @JsonSetter(value = "runId")
    public void setRunId(String runId) {
        domain.setRunId(runId);
    }

    @JsonGetter(value = "dateTime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ISO_8601_DATETIME_PATTERN)
    public Date getDateTime() {
        return domain.getDateTime();
    }

    @JsonSetter(value = "dateTime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ISO_8601_DATETIME_PATTERN)
    public void setDateTime(Date dateTime) {
        domain.setDateTime(dateTime);
    }

    @JsonGetter(value = "result")
    public String result() {
        return domain.getResult().name();
    }

    @JsonSetter(value = "result")
    public void setResult(String result) {
        domain.setResult(Result.valueOf(result));
    }

    @JsonGetter(value = "test")
    public TestDTO test() {
        return new TestDTO(domain.getTest());
    }

    @JsonSetter(value = "test")
    public void setTest(TestDTO testDTO) {
        domain.setTest(testDTO.getDomain());
    }

    @JsonGetter(value = "inputs")
    public List<ItemDTO> getInputs() {
        return domain.getInputs().stream()
                .map(ItemDTO::new)
                .toList();
    }

    @JsonSetter(value = "inputs")
    public void setInputs(List<ItemDTO> inputs) {
        domain.setInputs(inputs.stream()
                .map(ItemDTO::getDomain)
                .toList());
    }

    @JsonGetter(value = "outputs")
    public List<ItemDTO> getOutputs() {
        return domain.getOutputs().stream()
                .map(ItemDTO::new)
                .toList();
    }

    @JsonSetter(value = "outputs")
    public void setOutputs(List<ItemDTO> outputs) {
        domain.setInputs(outputs.stream()
                .map(ItemDTO::getDomain)
                .toList());
    }

    @JsonGetter(value = "stepRunReports")
    public List<StepRunReportDTO> getStepRunReports() {
        return domain.getStepRunReports().stream()
                .map(StepRunReportDTO::new)
                .toList();
    }

    @JsonSetter(value = "stepRunReports")
    public void setStepRunReports(List<StepRunReportDTO> stepRunReports) {
        domain.setStepRunReports(stepRunReports.stream()
                .map(StepRunReportDTO::getDomain)
                .toList());
    }

    @JsonGetter(value = "urlToTestRun")
    public String getUrlToTestRun() {
        return domain.getUrlToTestRun();
    }

    @JsonSetter(value = "urlToTestRun")
    public void setUrlToTestRun(String urlToTestRun) {
        domain.setUrlToTestRun(urlToTestRun);
    }

    @JsonGetter(value = "unexpectedErrors")
    public List<UnexpectedErrorDTO> getUnexpectedErrors() {
        return domain.getUnexpectedErrors().stream()
                .map(UnexpectedErrorDTO::new)
                .toList();
    }

    @JsonSetter(value = "unexpectedErrors")
    public void setUnexpectedErrors(List<UnexpectedErrorDTO> unexpectedErrors) {
        domain.setUnexpectedErrors(unexpectedErrors.stream()
                .map(UnexpectedErrorDTO::getDomain)
                .toList());
    }

    @JsonIgnore
    public TestRunReport getDomain() {
        return domain;
    }
}
