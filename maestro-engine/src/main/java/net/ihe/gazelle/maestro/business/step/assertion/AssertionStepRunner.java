package net.ihe.gazelle.maestro.business.step.assertion;

import net.ihe.gazelle.maestro.api.business.testreport.StepResult;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRun;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRunner;
import net.ihe.gazelle.maestro.api.business.testrun.runner.SyncStepRunner;

/**
 * Assertion Step is not implemented. It exists just for test purpose
 */
public class AssertionStepRunner extends SyncStepRunner {

    private final AssertionHandler handler;

    protected AssertionStepRunner(AssertionHandler assertionHandler) {
        handler = assertionHandler;
    }

    @Override
    public StepRunReportBuilder run(StepRun stepRun) {
        handler.assertion((AssertionStep) stepRun.step());
        return new StepRunReportBuilder()
                .setStepId(stepRun.step().getName())
                .setType(AssertionStep.TYPE)
                .setResult(StepResult.UNDEFINED);
    }

    @Override
    public String getSupportedStep() {
        return AssertionStep.TYPE;
    }

}
