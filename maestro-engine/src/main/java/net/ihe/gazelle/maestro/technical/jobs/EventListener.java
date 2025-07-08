package net.ihe.gazelle.maestro.technical.jobs;

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;
import net.ihe.gazelle.maestro.api.business.testrun.Maestro;
import net.ihe.gazelle.maestro.api.business.testrun.runner.FinishedStepEvent;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StartStepCommand;
import net.ihe.gazelle.maestro.technical.jobs.testrunevents.*;
import net.ihe.gazelle.maestro.technical.websocket.SessionsStorageInMemory;
import net.ihe.gazelle.maestro.technical.websocket.client.TestRunSocketClient;
import net.ihe.gazelle.maestro.technical.ws.async.AsynchronousMaestro;
import net.ihe.gazelle.maestro.technical.ws.async.tmintegration.TMTestRunMaestro;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the event broker that listen to all kind of messages used in Maestro application
 * It will trigger application code depending on which event is fired.
 */
public class EventListener {

    private static final Logger LOG = LoggerFactory.getLogger(EventListener.class);

    private final Maestro engine;
    private final SynchronousMaestro synchronousMaestro;
    private final AsynchronousMaestro asynchronousMaestro;
    private final StepRunnerJob stepRunnerJob;
    private final TMTestRunMaestro tmTestRunMaestro;
    private final TestRunSocketClient testRunSocketClient = new TestRunSocketClient();

    @Inject
    public EventListener(Maestro engine, SynchronousMaestro synchronousMaestro, AsynchronousMaestro asynchronousMaestro, StepRunnerJob stepRunnerJob, TMTestRunMaestro tmTestRunMaestro) {
        this.engine = engine;
        this.synchronousMaestro = synchronousMaestro;
        this.asynchronousMaestro = asynchronousMaestro;
        this.stepRunnerJob = stepRunnerJob;
        this.tmTestRunMaestro = tmTestRunMaestro;
    }

    public void onStartTestSet(@ObservesAsync StartTestSetCommand startTestSetCommand) {
        engine.run(startTestSetCommand.getTestSetRun());
    }

    public void onFinishTestSet(@ObservesAsync FinishTestSetEvent finishTestSetEvent) {
        String testSetId = finishTestSetEvent.getTestSetRun().testSetId();
        if (asynchronousMaestro.isServiceRunning(testSetId)) {
            asynchronousMaestro.callback(finishTestSetEvent);
        } else if (tmTestRunMaestro.isServiceRunning(testSetId)) {
            tmTestRunMaestro.callbackTM(finishTestSetEvent);
        } else if (SessionsStorageInMemory.hasSession(testSetId)) {
            testRunSocketClient.sendTestRunCompleted(testSetId, finishTestSetEvent.getTestReport().getTestRunReports().getFirst());
        } else {
            synchronousMaestro.completeFuture(finishTestSetEvent);
        }
    }

    public void onStartTestRun(@Observes StartTestRunCommand startTestRunCommand) {
        engine.run(startTestRunCommand.getTestRun());
    }

    public void onFinishTestRun(@ObservesAsync FinishTestRunEvent finishTestRunEvent) {
//        testRunSocketClient.sendTestRunCompleted(finishTestRunEvent.getTestRun().getTestSetId(),finishTestRunEvent.getTestRunReportBuilder().build());
        engine.finishTest(finishTestRunEvent.getTestRun(), finishTestRunEvent.getTestRunReportBuilder());
    }

    public void onStartStep(@ObservesAsync StartStepCommand startStepCommand) {
        stepRunnerJob.launchStepExecution(startStepCommand);
    }

    public void onFinishStep(@ObservesAsync FinishedStepEvent finishedStepEvent) {
//        testRunSocketClient.sendTestStepCompleted(finishedStepEvent.getStepRun().testRunId(),finishedStepEvent.getStepRunReport().build());
        engine.finishStep(finishedStepEvent.getStepRun(), finishedStepEvent.getStepRunReport());
    }

    public void onErrorTestRunEvent(@ObservesAsync ErrorTestRunEvent errorTestRunEvent) {
        Exception exception = errorTestRunEvent.getException();
        LOG.error(exception.getMessage(), exception);
    }
}
