package net.ihe.gazelle.maestro.technical.jobs;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestSetRun;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StartStepCommand;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepMessage;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRun;
import net.ihe.gazelle.maestro.business.engine.MaestroProducer;
import net.ihe.gazelle.maestro.technical.jobs.testrunevents.ErrorTestRunEvent;
import net.ihe.gazelle.maestro.technical.jobs.testrunevents.FinishTestRunEvent;
import net.ihe.gazelle.maestro.technical.jobs.testrunevents.FinishTestSetEvent;
import net.ihe.gazelle.maestro.technical.jobs.testrunevents.TestRunEvent;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

/**
 * MicroProfile Messaging is a specification that simplifies asynchronous messaging in Java microservices.
 * It provides a framework for building reactive systems using standardized annotations and APIs to
 * interact with message brokers, Smallrye in our case.
 * It supports concepts like event-driven architecture, message publishing/subscribing, and
 * processing with ease while integrating seamlessly into MicroProfile-based applications.
 */
@ApplicationScoped
@Default
public class MicroprofileMaestroProducer implements MaestroProducer {

    private final Emitter<TestRunEvent> testRunEventEmitter;
    private final Emitter <StepMessage> stepMessageEmitter;

    public MicroprofileMaestroProducer(@Channel("test-run") Emitter<TestRunEvent> testRunEventEmitter,
                                       @Channel("step-run") Emitter<StepMessage> stepMessageEmitter) {
        this.testRunEventEmitter = testRunEventEmitter;
        this.stepMessageEmitter = stepMessageEmitter;
    }

    @Override
    public void returnTestRunReport(TestRun testRun, TestRunReportBuilder report){
        testRunEventEmitter.send(new FinishTestRunEvent(testRun, report));
    }

    @Override
    public void executeStep(StepRun stepRun){
        stepMessageEmitter.send(new StartStepCommand(stepRun));
    }

    @Override
    public void returnTestSetReport(TestSetRun testSetRun, TestReport report){
        testRunEventEmitter.send(new FinishTestSetEvent(testSetRun, report));
    }

    @Override
    public void emitErrorTestRunEvent(Exception exception){
        testRunEventEmitter.send(new ErrorTestRunEvent(exception));
    }

}
