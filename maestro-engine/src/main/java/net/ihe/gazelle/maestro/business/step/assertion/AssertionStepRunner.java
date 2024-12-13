package net.ihe.gazelle.maestro.business.step.assertion;

import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.maestro.runner.StepRun;
import net.ihe.gazelle.maestro.runner.StepRunner;
import net.ihe.gazelle.testreport.StepResult;
import net.ihe.gazelle.testreport.StepRunReportBuilder;

/**
 * Assertion Step is not implemented. It exists just for test purpose
 */
public class AssertionStepRunner implements StepRunner {

    private final ValidatorBuilderFactory validatorBuilderFactory;
    private final AssertionHandler handler;

    protected AssertionStepRunner(ValidatorBuilderFactory validatorBuilderFactory, AssertionHandler assertionHandler) {
        this.validatorBuilderFactory = validatorBuilderFactory;
        handler = assertionHandler;
    }

    @Override
    public StepRunReportBuilder run(StepRun stepRun) {
        handler.assertion((AssertionStep) stepRun.step());
        return new StepRunReportBuilder(validatorBuilderFactory)
                .setStepId(stepRun.step().getName())
                .setType(AssertionStep.TYPE)
                .setResult(StepResult.UNDEFINED);
    }

    @Override
    public String getSupportedStep() {
        return AssertionStep.TYPE;
    }

}
