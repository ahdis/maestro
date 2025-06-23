package net.ihe.gazelle.maestro.technical.jobs;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import net.ihe.gazelle.maestro.api.business.testrun.Maestro;
import net.ihe.gazelle.maestro.api.business.testrun.runner.FinishedStepEvent;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StartStepCommand;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepMessage;
import net.ihe.gazelle.maestro.technical.jobs.testrunevents.*;
import net.ihe.gazelle.maestro.technical.websocket.client.TestRunSocketClient;
import net.ihe.gazelle.maestro.technical.ws.async.AsynchronousMaestro;
import net.ihe.gazelle.maestro.technical.ws.async.tmintegration.TMTestRunMaestro;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Default
public class MicroprofileListener implements TestRunEventListener, StepRunEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(MicroprofileListener.class);

    private final Maestro engine;
    private final SynchronousMaestro synchronousMaestro;
    private final AsynchronousMaestro asynchronousMaestro;
    private final StepRunnerJob stepRunnerJob;
    private final TMTestRunMaestro tmTestRunMaestro;
    private final TestRunSocketClient testRunSocketClient = new TestRunSocketClient();

    @Inject
    public MicroprofileListener(Maestro engine, SynchronousMaestro synchronousMaestro, AsynchronousMaestro asynchronousMaestro, StepRunnerJob stepRunnerJob, TMTestRunMaestro tmTestRunMaestro) {
        this.engine = engine;
        this.synchronousMaestro = synchronousMaestro;
        this.asynchronousMaestro = asynchronousMaestro;
        this.stepRunnerJob = stepRunnerJob;
        this.tmTestRunMaestro = tmTestRunMaestro;
    }

    /**
     * Listener method for testRun messages send by different producers : <br>
     *  - SynchronousMaestro {@link SynchronousMaestro} <br>
     *  - MicroprofileListener {@link MicroprofileListener} <br>
     *  - TMTestRunMaestro {@link TMTestRunMaestro} <br>
     *
     * <p>
     *     Treatment will depend on event type :
     *     <p>
     *         <b>StartTestRunCommand</b> : Launch execution of one TestRun
     *     </p>
     *     <p>
     *         <b>StartTestSetCommand</b> : Launch execution of a TestSetRun
     *     </p>
     *     <p>
     *         <b>FinishTestRunEvent</b> : Return executed TestRun and TestRunReportBuilder to engine to continue
     *         iteration
     *     </p>
     *     <p>
     *         <b>FinishTestSetEvent</b> : If There is a TM service running with testSet id, forward message to TM callback,
     *         else forward message to SynchronousMaestro
     *     </p>
     *     <p>
     *         <b>ErrorTestRunEvent</b> : Print exception in Logs
     *     </p>
     * </p>
     *
     * @param event the message received
     */
    @Incoming("test-run")
    @Acknowledgment(Acknowledgment.Strategy.PRE_PROCESSING)
    @Override
    public void receiveTestRun(TestRunEvent event) {
        if (event instanceof StartTestRunCommand startEvent) {
            engine.run(startEvent.getTestRun());
        }
        if (event instanceof StartTestSetCommand testSetEvent) {
            engine.run(testSetEvent.getTestSetRun());
        }
        if (event instanceof FinishTestRunEvent finishTestRunEvent) {
            testRunSocketClient.sendTestRunCompleted(finishTestRunEvent.getTestRun().getTestSetId(),finishTestRunEvent.getTestRunReportBuilder().build());
            engine.finishTest(finishTestRunEvent.getTestRun(), finishTestRunEvent.getTestRunReportBuilder());
        }
        if (event instanceof FinishTestSetEvent finishTestSetEvent) {
            if (asynchronousMaestro.isServiceRunning(finishTestSetEvent.getTestSetRun().testSetId())) {
                asynchronousMaestro.callback(finishTestSetEvent);
            } else if (tmTestRunMaestro.isServiceRunning(finishTestSetEvent.getTestSetRun().testSetId())) {
                tmTestRunMaestro.callbackTM(finishTestSetEvent);
            } else {
                synchronousMaestro.completeFuture(finishTestSetEvent);
            }
        }
        if (event instanceof ErrorTestRunEvent errorTestRunEvent) {
            Exception exception = errorTestRunEvent.getException();
            LOG.error(exception.getMessage(), exception);
        }
    }

    /**
     * Listener method for StepRun messages send by different producers : <br>
     * - MaestroProducer {@link net.ihe.gazelle.maestro.business.engine.MaestroProducer} <br>
     * - StepRunnerJob {@link StepRunnerJob} <br>
     *
     * <p>
     *     Treatment will depend on event type :
     *     <p>
     *         <b>StartStepCommand</b> : Forward message to StepRunnerJob to execute Step
     *     </p>
     *     <p>
     *         <b>FinishedStepEvent</b> : Forward message to Maestro to continue iteration
     *     </p>
     * </p>
     *
     * @param stepMessage message received
     */
    @Incoming("step-run")
    @Acknowledgment(Acknowledgment.Strategy.PRE_PROCESSING)
    @Override
    public void receiveStepEvent(StepMessage stepMessage){
        if (stepMessage instanceof StartStepCommand startStepCommand) {
            stepRunnerJob.launchStepExecution(startStepCommand);
        }
        if (stepMessage instanceof FinishedStepEvent finishedStepEvent) {
            testRunSocketClient.sendTestStepCompleted(finishedStepEvent.getStepRun().testRunId(),finishedStepEvent.getStepRunReport().build());
            engine.finishStep(finishedStepEvent.getStepRun(), finishedStepEvent.getStepRunReport());
        }
    }
}
