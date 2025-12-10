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

package net.ihe.gazelle.maestro.api.business.testreport;

import net.ihe.gazelle.errorhandling.business.UnexpectedError;
import net.ihe.gazelle.maestro.api.business.property.Property;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Business rules <br>
 * mandatory fields : dateTime, result, test
 *
 */
public class TestRunReport implements Serializable {

    @Serial
    private static final long serialVersionUID = 6739258918175686572L;

    private String runId;
    private Instant dateTime;
    private Result result;
    private Test test;
    private List<Property> inputs;
    private List<Property> outputs;
    private List<StepRunReport> stepRunReports;
    private String urlToTestRun;
    private List<UnexpectedError> unexpectedErrors;

    public TestRunReport() {
        dateTime = Instant.now();
        result = Result.UNDEFINED;
        inputs = new ArrayList<>();
        outputs = new ArrayList<>();
        stepRunReports = new ArrayList<>();
        unexpectedErrors = new ArrayList<>();
    }

    public TestRunReport(TestRunReport copy) {
        this();
        this.runId = copy.getRunId();
        this.dateTime = copy.getDateTime();
        this.result = copy.getResult();
        this.test = copy.getTest();
        this.inputs = new ArrayList<>(copy.getInputs());
        this.outputs = new ArrayList<>(copy.getOutputs());
        this.stepRunReports = new ArrayList<>(copy.getStepRunReports());
        this.urlToTestRun = copy.getUrlToTestRun();
        this.unexpectedErrors = new ArrayList<>(copy.getUnexpectedErrors());
    }

    public String getRunId() {
        return runId;
    }

    public TestRunReport setRunId(String runId) {
        this.runId = runId;
        return this;
    }

    public Instant getDateTime() {
        return dateTime;
    }

    public TestRunReport setDateTime(Instant dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public Test getTest() {
        return test;
    }

    public TestRunReport setTest(Test test) {
        this.test = test;
        return this;
    }

    public List<Property> getInputs() {
        return new ArrayList<>(inputs);
    }

    public TestRunReport setInputs(List<Property> inputs) {
        this.inputs = inputs != null ? new ArrayList<>(inputs) : new ArrayList<>();
        return this;
    }

    public TestRunReport addInput(Property input) {
        inputs.add(input);
        return this;
    }

    public TestRunReport addInputs(List<Property> inputs) {
        this.inputs.addAll(inputs);
        return this;
    }

    public List<Property> getOutputs() {
        return new ArrayList<>(outputs);
    }

    public TestRunReport setOutputs(List<Property> outputs) {
        this.outputs = outputs != null ? new ArrayList<>(outputs) : new ArrayList<>();
        return this;
    }

    public TestRunReport addOutput(Property output) {
        outputs.add(output);
        return this;
    }

    public TestRunReport addOutputs(List<Property> outputs) {
        this.outputs.addAll(outputs);
        return this;
    }

    public Result getResult() {
        return result;
    }

    public TestRunReport setResult(Result result) {
        this.result = result;
        return this;
    }

    public List<StepRunReport> getStepRunReports() {
        return new ArrayList<>(stepRunReports);
    }

    public TestRunReport setStepRunReports(List<StepRunReport> stepRunReports) {
        this.stepRunReports =  stepRunReports != null ? new ArrayList<>(stepRunReports) : new ArrayList<>();
        return this;
    }

    public TestRunReport addStepRunReport(StepRunReport stepRunReport) {
        stepRunReports.add(stepRunReport);
        return this;
    }

    public TestRunReport addStepRunReports(List<StepRunReport> stepRunReports) {
        this.stepRunReports.addAll(stepRunReports);
        return this;
    }

    public String getUrlToTestRun() {
        return urlToTestRun;
    }

    public TestRunReport setUrlToTestRun(String urlToTestRun) {
        this.urlToTestRun = urlToTestRun;
        return this;
    }

    public List<UnexpectedError> getUnexpectedErrors() {
        return new ArrayList<>(unexpectedErrors);
    }

    public TestRunReport setUnexpectedErrors(List<UnexpectedError> unexpectedErrors) {
        this.unexpectedErrors = unexpectedErrors!= null ? new ArrayList<>(unexpectedErrors) : new ArrayList<>();
        return this;
    }

    public TestRunReport addUnexpectedError(UnexpectedError unexpectedError) {
        unexpectedErrors.add(unexpectedError);
        return this;
    }

    public TestRunReport addUnexpectedErrors(List<UnexpectedError> unexpectedErrors) {
        this.unexpectedErrors.addAll(unexpectedErrors);
        return this;
    }

    public Result computeResult() {
        if (!unexpectedErrors.isEmpty()) {
            result = Result.UNDEFINED;
        } else {
            Optional<StepResult> globalStepResult = stepRunReports.stream()
                    .map(StepRunReport::getResult)
                    .filter(Objects::nonNull)
                    .max(StepResult::compareTo);

            if (globalStepResult.isPresent()) {
                switch (globalStepResult.get()) {
                    case PASSED -> result = Result.PASSED;
                    case FAILED -> result = Result.FAILED;
                    case DONE, UNDEFINED -> result = Result.UNDEFINED;
                }
            }
        }
        return result;
    }

    public boolean isDateTimeNotInTheFuture() {
        return dateTime != null && !dateTime.isAfter(Instant.now());
    }

    public boolean resultMatchesComputedResult() {
        TestRunReport tmp = new TestRunReport(this);
        tmp.computeResult();
        return tmp.getResult().equals(result);
    }

    public boolean isTestDefined() {
        return test != null;
    }

    public boolean isUrlToTestRunDefinedIfPresent() {
        return urlToTestRun == null || !urlToTestRun.isBlank();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestRunReport testRunReport)) return false;
        return Objects.equals(runId, testRunReport.runId)
                && Objects.equals(dateTime, testRunReport.dateTime)
                && result == testRunReport.result
                && Objects.equals(test, testRunReport.test)
                && Objects.equals(inputs, testRunReport.inputs)
                && Objects.equals(outputs, testRunReport.outputs)
                && Objects.equals(stepRunReports, testRunReport.stepRunReports)
                && Objects.equals(urlToTestRun, testRunReport.urlToTestRun)
                && Objects.equals(unexpectedErrors, testRunReport.unexpectedErrors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(runId, dateTime, result, test, inputs, outputs, stepRunReports, urlToTestRun, unexpectedErrors);
    }
}
