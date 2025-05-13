package net.ihe.gazelle.testreport;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.testreport.validator.TestRunReportValidator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestRunReportBuilder extends AbstractBuilder<TestRunReport> {

    private String runId;
    private Date dateTime;
    private TestBuilder test;
    private List<ItemBuilder> inputs;
    private List<ItemBuilder> outputs;
    private List<StepRunReportBuilder> stepRunReports;
    private String urlToTestRun;
    private List<UnexpectedErrorBuilder> unexpectedErrors;

    public TestRunReportBuilder() {
        super();
        inputs = new ArrayList<>();
        outputs = new ArrayList<>();
        stepRunReports = new ArrayList<>();
        unexpectedErrors = new ArrayList<>();
    }

    public TestRunReportBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
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
        this.dateTime = dateTime;
        return this;
    }

    public TestRunReportBuilder setTest(TestBuilder test) {
        this.test = test;
        return this;
    }

    public TestRunReportBuilder setInputs(List<ItemBuilder> inputs) {
        this.inputs = inputs;
        return this;
    }

    public TestRunReportBuilder addInput(ItemBuilder input) {
        inputs.add(input);
        return this;
    }

    public TestRunReportBuilder addInputs(List<ItemBuilder> inputs) {
        this.inputs.addAll(inputs);
        return this;
    }

    public TestRunReportBuilder setOutputs(List<ItemBuilder> outputs) {
        this.outputs = outputs;
        return this;
    }

    public TestRunReportBuilder addOutput(ItemBuilder output) {
        outputs.add(output);
        return this;
    }

    public TestRunReportBuilder addOutputs(List<ItemBuilder> outputs) {
        this.outputs.addAll(outputs);
        return this;
    }

    public TestRunReportBuilder setStepRunReports(List<StepRunReportBuilder> stepRunReports) {
        this.stepRunReports = stepRunReports;
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
        this.unexpectedErrors = unexpectedErrors;
        return this;
    }

    public TestRunReportBuilder addUnexpectedError(UnexpectedErrorBuilder unexpectedError) {
        this.unexpectedErrors.add(unexpectedError);
        return this;
    }

    public TestRunReportBuilder addUnexpectedErrors(List<UnexpectedErrorBuilder> unexpectedErrors) {
        this.unexpectedErrors.addAll(unexpectedErrors);
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
