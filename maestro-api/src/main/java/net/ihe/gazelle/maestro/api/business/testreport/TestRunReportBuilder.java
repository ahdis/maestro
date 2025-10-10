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

import net.ihe.gazelle.errorhandling.business.UnexpectedErrorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.maestro.api.business.testreport.validator.TestRunReportValidator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestRunReportBuilder extends AbstractBuilder<TestRunReport> {

    private String runId;
    private Date dateTime;
    private TestBuilder test;
    private List<InputBuilder> inputs;
    private List<OutputBuilder> outputs;
    private List<StepRunReportBuilder> stepRunReports;
    private String urlToTestRun;
    private List<UnexpectedErrorBuilder> unexpectedErrors;

    public TestRunReportBuilder() {
        super();
        dateTime = new Date();
        inputs = new ArrayList<>();
        outputs = new ArrayList<>();
        stepRunReports = new ArrayList<>();
        unexpectedErrors = new ArrayList<>();
    }

    public TestRunReportBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
        dateTime = new Date();
        inputs = new ArrayList<>();
        outputs = new ArrayList<>();
        stepRunReports = new ArrayList<>();
        unexpectedErrors = new ArrayList<>();
    }

    public TestRunReportBuilder setRunId(String runId) {
        this.runId = runId;
        return this;
    }

    public TestRunReportBuilder setDateTime(Date dateTime) {
        this.dateTime = new Date(dateTime.getTime());
        return this;
    }

    public TestRunReportBuilder setTest(TestBuilder test) {
        this.test = test;
        return this;
    }

    public TestRunReportBuilder setInputs(List<InputBuilder> inputs) {
        this.inputs = new ArrayList<>(inputs);
        return this;
    }

    public TestRunReportBuilder addInput(InputBuilder input) {
        inputs.add(input);
        return this;
    }

    public TestRunReportBuilder setOutputs(List<OutputBuilder> outputs) {
        this.outputs = new ArrayList<>(outputs);
        return this;
    }

    public TestRunReportBuilder addOutput(OutputBuilder output) {
        outputs.add(output);
        return this;
    }

    public TestRunReportBuilder setStepRunReports(List<StepRunReportBuilder> stepRunReports) {
        this.stepRunReports = new ArrayList<>(stepRunReports);
        return this;
    }

    public TestRunReportBuilder addStepRunReport(StepRunReportBuilder stepRun) {
        this.stepRunReports.add(stepRun);
        return this;
    }

    public TestRunReportBuilder addStepRunReports(List<StepRunReportBuilder> stepRuns) {
        this.stepRunReports.addAll(stepRuns);
        return this;
    }

    public TestRunReportBuilder setUrlToTestRun(String urlToTestRun) {
        this.urlToTestRun = urlToTestRun;
        return this;
    }

    public TestRunReportBuilder setUnexpectedErrors(List<UnexpectedErrorBuilder> unexpectedErrors) {
        this.unexpectedErrors = new ArrayList<>(unexpectedErrors);
        return this;
    }

    public TestRunReportBuilder addUnexpectedError(UnexpectedErrorBuilder unexpectedError) {
        this.unexpectedErrors.add(unexpectedError);
        return this;
    }

    @Override
    protected AbstractValidator<TestRunReport> instantiateValidator(ValidatorBuilderFactory validatorBuilderFactory) {
        return new TestRunReportValidator(validatorBuilderFactory);
    }

    @Override
    protected TestRunReport instantiate() {
        return new TestRunReport();
    }

    @Override
    protected void make(TestRunReport testRunReport) {
        testRunReport.setRunId(runId)
                .setDateTime(dateTime)
                .setTest(AbstractBuilder.staticBuildWithoutValidation(test))
                .setInputs(
                        !inputs.isEmpty() ? inputs.stream()
                                .map(AbstractBuilder::staticBuildWithoutValidation)
                                .toList() : new ArrayList<>()
                )
                .setOutputs(
                        !outputs.isEmpty() ? outputs.stream()
                                .map(AbstractBuilder::staticBuildWithoutValidation)
                                .toList() : new ArrayList<>()
                )
                .setStepRunReports(
                        !stepRunReports.isEmpty() ? stepRunReports.stream()
                                .map(AbstractBuilder::staticBuildWithoutValidation)
                                .toList() : new ArrayList<>()
                )
                .setUrlToTestRun(urlToTestRun)
                .setUnexpectedErrors(
                        !unexpectedErrors.isEmpty() ? unexpectedErrors.stream()
                                .map(AbstractBuilder::staticBuildWithoutValidation)
                                .toList() : new ArrayList<>()
                ).computeResult();
    }
}
