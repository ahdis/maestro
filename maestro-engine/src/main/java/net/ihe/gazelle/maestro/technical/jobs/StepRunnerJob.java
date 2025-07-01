package net.ihe.gazelle.maestro.technical.jobs;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import net.ihe.gazelle.maestro.api.business.testreport.StepResult;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testreport.UnexpectedErrorBuilder;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StartStepCommand;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRun;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRunner;
import net.ihe.gazelle.maestro.business.engine.MaestroProducer;

import java.util.List;

/**
 * A stepRunnerJob receive message from producer to execute Step, launch execution and send result to Maestro Listener
 */
@Dependent
@Default
public class StepRunnerJob {

    private final StepRunnerProvider stepRunnerFactory;
    private final MaestroProducer maestroProducer;

    @Inject
    public StepRunnerJob(StepRunnerProvider stepRunnerFactory, MaestroProducer maestroProducer) {
        this.stepRunnerFactory = stepRunnerFactory;
        this.maestroProducer = maestroProducer;
    }

    /**
     * Execute Step with a stepRunner corresponding to the step
     * @param startStepCommand Message containing step to run
     */
    public void launchStepExecution(StartStepCommand startStepCommand) {
        StepRun stepRun = startStepCommand.getStepRun();
        try {
            StepRunner runner = stepRunnerFactory.create(stepRun.step());
            stepEnded(stepRun, runner.run(stepRun));
        } catch (Exception e) {
            returnUnexpectedError(e, stepRun);
        }
    }

    /**
     * Fire an event to EventListener containing a step and a StepReportBuilder with one UnexpectedErrorBuilder containing data from an
     * exception
     * @see EventListener
     * @param e an exception that occurred during step execution
     * @param stepRun Previously executed step
     */
    public void returnUnexpectedError(Exception e, StepRun stepRun) {
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
        stepEnded(stepRun, builder);
    }

    /**
     * Fire an event containing StepRunReportBuilder and StepRun to EventListener
     * @see EventListener
     * @param stepRun StepRun previously executed
     * @param stepRunReportBuilder A StepReportBuilder containing data of stepRun execution
     */
    private void stepEnded(StepRun stepRun, StepRunReportBuilder stepRunReportBuilder) {
        maestroProducer.returnStepReport(stepRun, stepRunReportBuilder);
    }

}
