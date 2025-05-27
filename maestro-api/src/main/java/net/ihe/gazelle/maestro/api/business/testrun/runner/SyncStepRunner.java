package net.ihe.gazelle.maestro.api.business.testrun.runner;


public abstract class SyncStepRunner implements StepRunner {

    @Override
    public void runAsync(StepRun stepRun) {
        throw new UnsupportedOperationException("This step runner does not support asynchronous execution. Use an synchronous step runner instead.");
    }
}
