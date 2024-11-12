package net.ihe.gazelle.report.builder;

import net.ihe.gazelle.framework.modelvalidator.domain.AbstractBuilder;
import net.ihe.gazelle.framework.modelvalidator.domain.BuilderVisitor;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.report.model.TestRun;
import net.ihe.gazelle.report.visitor.TestRunBuilderVisitor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestRunBuilder extends AbstractBuilder<TestRun> {

    private String runId;
    private Date dateTime;
    private TestBuilder test;
    private List<ItemBuilder> inputs;
    private List<ItemBuilder> outputs;
    private List<StepRunBuilder> stepRuns;
    private String urlToTestRun;
    private List<UnexpectedErrorBuilder> unexpectedErrors;

    public TestRunBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
        inputs = new ArrayList<>();
        outputs = new ArrayList<>();
        stepRuns = new ArrayList<>();
        unexpectedErrors = new ArrayList<>();
    }

    public TestRunBuilder setRunId(String runId) {
        this.runId = runId;
        return this;
    }

    public TestRunBuilder setDateTime(Date dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public TestRunBuilder setTest(TestBuilder test) {
        this.test = test;
        return this;
    }

    public TestRunBuilder setInputs(List<ItemBuilder> inputs) {
        this.inputs = inputs;
        return this;
    }

    public TestRunBuilder addInput(ItemBuilder input) {
        inputs.add(input);
        return this;
    }

    public TestRunBuilder addInputs(List<ItemBuilder> inputs) {
        this.inputs.addAll(inputs);
        return this;
    }

    public TestRunBuilder setOutputs(List<ItemBuilder> outputs) {
        this.outputs = outputs;
        return this;
    }

    public TestRunBuilder addOutput(ItemBuilder output) {
        outputs.add(output);
        return this;
    }

    public TestRunBuilder addOutputs(List<ItemBuilder> outputs) {
        this.outputs.addAll(outputs);
        return this;
    }

    public TestRunBuilder setStepRuns(List<StepRunBuilder> stepRuns) {
        this.stepRuns = stepRuns;
        return this;
    }

    public TestRunBuilder addStepRun(StepRunBuilder stepRun) {
        this.stepRuns.add(stepRun);
        return this;
    }

    public TestRunBuilder addStepRuns(List<StepRunBuilder> stepRuns) {
        this.stepRuns.addAll(stepRuns);
        return this;
    }

    public TestRunBuilder setUrlToTestRun(String urlToTestRun) {
        this.urlToTestRun = urlToTestRun;
        return this;
    }

    public TestRunBuilder setUnexpectedErrors(List<UnexpectedErrorBuilder> unexpectedErrors) {
        this.unexpectedErrors = unexpectedErrors;
        return this;
    }

    public TestRunBuilder addUnexpectedError(UnexpectedErrorBuilder unexpectedError) {
        this.unexpectedErrors.add(unexpectedError);
        return this;
    }

    public TestRunBuilder addUnexpectedErrors(List<UnexpectedErrorBuilder> unexpectedErrors) {
        this.unexpectedErrors.addAll(unexpectedErrors);
        return this;
    }

    @Override
    protected BuilderVisitor getBuilderVisitor() {
        return TestRunBuilderVisitor.INSTANCE;
    }

    @Override
    protected Class<? extends TestRun> getParameterClass() {
        return TestRun.class;
    }

    @Override
    protected void make() {
        this.getObjectToBuild()
                .setRunId(runId)
                .setDateTime(dateTime)
                .setTest(AbstractBuilder.buildField(test))
                .setInputs(
                        !inputs.isEmpty() ? inputs.stream()
                                .map(AbstractBuilder::buildField)
                                .toList() : new ArrayList<>()
                )
                .setOutputs(
                        !outputs.isEmpty() ? outputs.stream()
                                .map(AbstractBuilder::buildField)
                                .toList() : new ArrayList<>()
                )
                .setStepRuns(
                        !stepRuns.isEmpty() ? stepRuns.stream()
                                .map(AbstractBuilder::buildField)
                                .toList() : new ArrayList<>()
                )
                .setUrlToTestRun(urlToTestRun)
                .setUnexpectedErrors(
                        !unexpectedErrors.isEmpty() ? unexpectedErrors.stream()
                                .map(AbstractBuilder::buildField)
                                .toList() : new ArrayList<>()
                );
        this.getObjectToBuild().computeResult();
    }
}
