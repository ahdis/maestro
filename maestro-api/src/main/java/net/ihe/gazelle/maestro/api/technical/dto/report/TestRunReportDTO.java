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

package net.ihe.gazelle.maestro.api.technical.dto.report;

import com.fasterxml.jackson.annotation.*;
import net.ihe.gazelle.errorhandling.technical.UnexpectedErrorDTO;
import net.ihe.gazelle.maestro.api.business.testreport.Result;
import net.ihe.gazelle.maestro.api.business.testreport.TestRunReport;
import net.ihe.gazelle.modelmarshaller.technical.dto.DTO;

import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "runId",
        "dateTime",
        "result",
        "test",
        "inputs",
        "outputs",
        "stepRunReports",
        "urlToTestRun",
        "unexpectedErrors"
})
public class TestRunReportDTO implements DTO<TestRunReport> {

    public static final String ISO_8601_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSX";

    @JsonIgnore
    private final TestRunReport testRunReport;

    public TestRunReportDTO() {
        this(new TestRunReport());
    }

    public TestRunReportDTO(TestRunReport testRunReport) {
        this.testRunReport = testRunReport;
    }

    @Override
    @JsonIgnore
    public TestRunReport getBusinessObject() {
        return testRunReport;
    }

    @JsonGetter(value = "runId")
    public String getRunId() {
        return getBusinessObject().getRunId();
    }

    @JsonSetter(value = "runId")
    public void setRunId(String runId) {
        getBusinessObject().setRunId(runId);
    }

    @JsonGetter(value = "dateTime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ISO_8601_DATETIME_PATTERN)
    public Date getDateTime() {
        return getBusinessObject().getDateTime();
    }

    @JsonSetter(value = "dateTime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ISO_8601_DATETIME_PATTERN)
    public void setDateTime(Date dateTime) {
        getBusinessObject().setDateTime(dateTime);
    }

    @JsonGetter(value = "result")
    public String result() {
        return getBusinessObject().getResult().name();
    }

    @JsonSetter(value = "result")
    public void setResult(String result) {
        getBusinessObject().setResult(Result.valueOf(result));
    }

    @JsonGetter(value = "test")
    public TestDTO test() {
        return new TestDTO(getBusinessObject().getTest());
    }

    @JsonSetter(value = "test")
    public void setTest(TestDTO testDTO) {
        getBusinessObject().setTest(testDTO.getBusinessObject());
    }

    @JsonGetter(value = "inputs")
    public List<InputDTO> getInputs() {
        return getBusinessObject()
                .getInputs()
                .stream()
                .map(InputDTO::new)
                .toList();
    }

    @JsonSetter(value = "inputs")
    public void setInputs(List<InputDTO> inputs) {
        getBusinessObject().setInputs(
                inputs.stream().map(InputDTO::getBusinessObject).toList()
        );
    }

    @JsonGetter(value = "outputs")
    public List<OutputDTO> getOutputs() {
        return getBusinessObject()
                .getOutputs()
                .stream()
                .map(OutputDTO::new)
                .toList();
    }

    @JsonSetter(value = "outputs")
    public void setOutputs(List<OutputDTO> outputs) {
        getBusinessObject().setOutputs(
                outputs.stream().map(OutputDTO::getBusinessObject).toList()
        );
    }

    @JsonGetter(value = "stepRunReports")
    public List<StepRunReportDTO> getStepRunReports() {
        return getBusinessObject()
                .getStepRunReports()
                .stream()
                .map(StepRunReportDTO::new)
                .toList();
    }

    @JsonSetter(value = "stepRunReports")
    public void setStepRunReports(List<StepRunReportDTO> stepRunReports) {
        getBusinessObject().setStepRunReports(
                stepRunReports.stream().map(StepRunReportDTO::getBusinessObject).toList()
        );
    }

    @JsonGetter(value = "urlToTestRun")
    public String getUrlToTestRun() {
        return getBusinessObject().getUrlToTestRun();
    }

    @JsonSetter(value = "urlToTestRun")
    public void setUrlToTestRun(String urlToTestRun) {
        getBusinessObject().setUrlToTestRun(urlToTestRun);
    }

    @JsonGetter(value = "unexpectedErrors")
    public List<UnexpectedErrorDTO> getUnexpectedErrors() {
        return getBusinessObject()
                .getUnexpectedErrors()
                .stream()
                .map(UnexpectedErrorDTO::new)
                .toList();
    }

    @JsonSetter(value = "unexpectedErrors")
    public void setUnexpectedErrors(List<UnexpectedErrorDTO> unexpectedErrors) {
        getBusinessObject().setUnexpectedErrors(
                unexpectedErrors.stream().map(UnexpectedErrorDTO::getBusinessObject).toList()
        );
    }
}
