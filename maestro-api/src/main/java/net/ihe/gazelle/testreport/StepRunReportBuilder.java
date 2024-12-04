package net.ihe.gazelle.testreport;

import net.ihe.gazelle.framework.modelvalidator.domain.AbstractBuilder;
import net.ihe.gazelle.framework.modelvalidator.domain.BuilderVisitor;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.testreport.visitor.StepRunReportBuilderVisitor;

import java.util.ArrayList;
import java.util.List;

public class StepRunReportBuilder extends AbstractBuilder<StepRunReport> {

    private String stepId;
    private String type;
    private StepResult result;
    private List<ItemBuilder> outputs;
    private List<UnexpectedErrorBuilder> unexpectedErrors;

    public StepRunReportBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
        outputs = new ArrayList<>();
        unexpectedErrors = new ArrayList<>();
    }

    public StepRunReportBuilder setStepId(String stepId) {
        this.stepId = stepId;
        return this;
    }

    public StepRunReportBuilder setType(String type) {
        this.type = type;
        return this;
    }

    public StepRunReportBuilder setResult(StepResult result) {
        this.result = result;
        return this;
    }

    public StepRunReportBuilder setOutputs(List<ItemBuilder> outputs) {
        this.outputs = new ArrayList<>(outputs);
        return this;
    }

    public StepRunReportBuilder addOutput(ItemBuilder item) {
        this.outputs.add(item);
        return this;
    }

    public StepRunReportBuilder addOutputs(List<ItemBuilder> outputs) {
        this.outputs.addAll(outputs);
        return this;
    }

    public StepRunReportBuilder setUnexpectedErrors(List<UnexpectedErrorBuilder> unexpectedErrors) {
        this.unexpectedErrors = new ArrayList<>(unexpectedErrors);
        return this;
    }

    public StepRunReportBuilder addUnexpectedError(UnexpectedErrorBuilder unexpectedError) {
        unexpectedErrors.add(unexpectedError);
        return this;
    }

    @Override
    protected BuilderVisitor getBuilderVisitor() {
        return StepRunReportBuilderVisitor.INSTANCE;
    }

    @Override
    protected Class<? extends StepRunReport> getParameterClass() {
        return StepRunReport.class;
    }

    @Override
    protected void make() {
        this.getObjectToBuild()
                .setStepId(stepId)
                .setType(type)
                .setResult(result)
                .setOutputs(
                        !outputs.isEmpty() ? outputs.stream()
                                .map(AbstractBuilder::buildField)
                                .toList() : new ArrayList<>()
                )
                .setUnexpectedErrors(!unexpectedErrors.isEmpty() ?
                                unexpectedErrors.stream().map(AbstractBuilder::buildField).toList() :
                                new ArrayList<>()
                )
                .computeResult();
    }
}
