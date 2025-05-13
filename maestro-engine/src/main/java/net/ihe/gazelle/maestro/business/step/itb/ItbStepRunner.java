package net.ihe.gazelle.maestro.business.step.itb;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.maestro.runner.StepRun;
import net.ihe.gazelle.maestro.runner.StepRunner;
import net.ihe.gazelle.testreport.StepResult;
import net.ihe.gazelle.testreport.StepRunReportBuilder;

public class ItbStepRunner implements StepRunner {

    private final ValidatorBuilderFactory validatorBuilderFactory;
    private final ItbHandler handler;

    public ItbStepRunner(ValidatorBuilderFactory validatorBuilderFactory, ItbHandler itbHandler) {
        this.validatorBuilderFactory = validatorBuilderFactory;
        this.handler = itbHandler;
    }

    @Override
    public StepRunReportBuilder run(StepRun stepRun) {
        handler.handle((ItbStep) stepRun.step());
        return new StepRunReportBuilder(validatorBuilderFactory)
                .setStepId(stepRun.step().getName())
                .setType(ItbStep.TYPE)
                .setResult(StepResult.PASSED);
    }

    @Override
    public final String getSupportedStep() {
        return ItbStep.TYPE;
    }

}
