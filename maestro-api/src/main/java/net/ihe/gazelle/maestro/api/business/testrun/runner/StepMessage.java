package net.ihe.gazelle.maestro.api.business.testrun.runner;

/**
 * <p>
 * A step message is a structure used to send step's info via producer/listener.
 * It contains a StepRun field.
 * It's an abstract class. Each concrete class extending StepMessage correspond to a type of message relative to step.
 * </p>
 * <p>
 * This abstract class should not be used directly to send message. Use inheritors class instead :
 *  <p>
 * {@link FinishedStepEvent}
 *  </p>
 *  <p>
 * {@link StartStepCommand}
 *  </p>
 * </p>
 */
public abstract class StepMessage {
    private final StepRun stepRun;

    protected StepMessage(StepRun stepRun) {
        this.stepRun = stepRun;
    }

    public StepRun getStepRun() {
        return stepRun;
    }
}
