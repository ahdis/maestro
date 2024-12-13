package net.ihe.gazelle.maestro.runner;

/**
 * Step message used by producer to notify a subscriber to launch step execution
 */
public class StartStepCommand extends StepMessage {

    public StartStepCommand(StepRun stepRun) {
        super(stepRun);
    }
}
