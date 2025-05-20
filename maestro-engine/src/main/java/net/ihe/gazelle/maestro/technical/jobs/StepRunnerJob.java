package net.ihe.gazelle.maestro.technical.jobs;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import net.ihe.gazelle.maestro.api.business.testreport.StepResult;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testreport.UnexpectedErrorBuilder;
import net.ihe.gazelle.maestro.api.business.testrun.runner.*;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.util.List;

/**
 * A stepRunnerJob receive message from producer to execute Step, launch execution and send result to Maestro Listener
 */
@Dependent
@Default
public class StepRunnerJob {

    private final StepRunnerProvider stepRunnerFactory;
    private final Emitter<StepMessage> stepMessageEmitter;

    @Inject
    public StepRunnerJob(StepRunnerProvider stepRunnerFactory,
                         @Channel("step-run") Emitter<StepMessage> stepMessageEmitter) {
        this.stepRunnerFactory = stepRunnerFactory;
        this.stepMessageEmitter = stepMessageEmitter;
    }

    /**
     * Execute Step with a stepRunner corresponding to the step
     * @param startStepCommand Message containing step to run
     */
    public void launchStepExecution(StartStepCommand startStepCommand) {
        StepRun stepRun = startStepCommand.getStepRun();
        try {
            StepRunner runner = stepRunnerFactory.create(stepRun.step());
            stepEnded(runner.run(stepRun), stepRun);
        } catch (Exception e) {
            returnUnexpectedError(e, stepRun);
        }
    }

    /**
     * Send a message to StepRunEventListener containing a step and a StepReportBuilder with one UnexpectedErrorBuilder containing data from an
     * exception
     * @see StepRunEventListener
     * @param e an exception that occurred during step execution
     * @param stepRun Previously executed step
     */
    private void returnUnexpectedError(Exception e, StepRun stepRun) {
        List<UnexpectedErrorBuilder> unexpectedErrorBuilders = List.of(
                new UnexpectedErrorBuilder()
                    .setMessage(e.getMessage())
                    .setName("StepExecutionError")
        );
        StepRunReportBuilder builder = new StepRunReportBuilder()
                .setStepId(stepRun.step().getName())
                .setType(stepRun.step().getType())
                .setResult(StepResult.UNDEFINED)
                .setUnexpectedErrors(unexpectedErrorBuilders);
        stepEnded(builder, stepRun);
    }

    /**
     * Emit a message containing StepRunReportBuilder and StepRun to StepRunEventListener
     * @see StepRunEventListener
     * @param stepRunReportBuilder A StepReportBuilder containing data of stepRun execution
     * @param stepRun StepRun previously executed
     */
    private void stepEnded(StepRunReportBuilder stepRunReportBuilder, StepRun stepRun) {
        stepMessageEmitter.send(new FinishedStepEvent(stepRunReportBuilder, stepRun));
    }

}
