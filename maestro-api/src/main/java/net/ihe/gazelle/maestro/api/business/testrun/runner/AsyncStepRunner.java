package net.ihe.gazelle.maestro.api.business.testrun.runner;

import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;

public abstract class AsyncStepRunner implements StepRunner {

    @Override
    public StepRunReportBuilder run(StepRun stepRun) {
        throw new UnsupportedOperationException("This step runner does not support synchronous execution. Use an asynchronous step runner instead.");
    }

}
