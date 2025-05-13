package net.ihe.gazelle.testreport;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.testreport.validator.StepRunReportValidator;

import java.util.ArrayList;
import java.util.List;

public class StepRunReportBuilder extends AbstractBuilder<StepRunReport> {

    private String stepId;
    private String type;
    private StepResult result;
    private List<ItemBuilder> outputs;
    private List<UnexpectedErrorBuilder> unexpectedErrors;

    public StepRunReportBuilder() {
        super();
        outputs = new ArrayList<>();
        unexpectedErrors = new ArrayList<>();
    }

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
    protected AbstractValidator<StepRunReport> instantiateValidator(ValidatorBuilderFactory validatorBuilderFactory) {
        return new StepRunReportValidator(validatorBuilderFactory);
    }

    @Override
    protected StepRunReport instantiate() {
        return new StepRunReport();
    }

    @Override
    protected void make(StepRunReport stepRunReport) {
        stepRunReport.setStepId(stepId)
                .setType(type)
                .setResult(result)
                .setOutputs(
                        !outputs.isEmpty() ? outputs.stream()
                                .map(AbstractBuilder::staticBuildWithoutValidation)
                                .toList() : new ArrayList<>()
                )
                .setUnexpectedErrors(
                        !unexpectedErrors.isEmpty() ? unexpectedErrors.stream()
                                .map(AbstractBuilder::staticBuildWithoutValidation)
                                .toList() : new ArrayList<>()
                ).computeResult();
    }
}
