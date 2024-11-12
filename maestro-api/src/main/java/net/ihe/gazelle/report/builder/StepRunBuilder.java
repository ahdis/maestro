package net.ihe.gazelle.report.builder;

import net.ihe.gazelle.framework.modelvalidator.domain.AbstractBuilder;
import net.ihe.gazelle.framework.modelvalidator.domain.BuilderVisitor;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.report.model.StepResult;
import net.ihe.gazelle.report.model.StepRun;
import net.ihe.gazelle.report.visitor.StepRunBuilderVisitor;

import java.util.ArrayList;
import java.util.List;

public class StepRunBuilder extends AbstractBuilder<StepRun> {

    private String stepId;
    private String type;
    private StepResult result;
    private List<ItemBuilder> outputs;

    public StepRunBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
        outputs = new ArrayList<>();
    }

    public StepRunBuilder setStepId(String stepId) {
        this.stepId = stepId;
        return this;
    }

    public StepRunBuilder setType(String type) {
        this.type = type;
        return this;
    }

    public StepRunBuilder setResult(StepResult result) {
        this.result = result;
        return this;
    }

    public StepRunBuilder setOutputs(List<ItemBuilder> outputs) {
        this.outputs = new ArrayList<>(outputs);
        return this;
    }

    public StepRunBuilder addOutput(ItemBuilder item) {
        this.outputs.add(item);
        return this;
    }

    public StepRunBuilder addOutputs(List<ItemBuilder> outputs) {
        this.outputs.addAll(outputs);
        return this;
    }

    @Override
    protected BuilderVisitor getBuilderVisitor() {
        return StepRunBuilderVisitor.INSTANCE;
    }

    @Override
    protected Class<? extends StepRun> getParameterClass() {
        return StepRun.class;
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
                );
    }
}
