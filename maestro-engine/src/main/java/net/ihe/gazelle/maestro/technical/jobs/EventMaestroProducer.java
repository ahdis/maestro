package net.ihe.gazelle.maestro.technical.jobs;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
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

/**
 * CDI Event is a specification that simplifies asynchronous messaging in Java microservices.
 * It provides a framework for building reactive systems using standardized annotations and APIs to
 * interact with message brokers, Observers in our case.
 * It supports concepts like event-driven architecture, message publishing/subscribing, and
 * processing with ease while integrating seamlessly into CDI based applications.
 */
@ApplicationScoped
@Default
public class EventMaestroProducer implements MaestroProducer {

    private final Event<TestRunEvent> testRunEvent;
    private final Event<StepMessage> stepMessage;

    public EventMaestroProducer(Event<TestRunEvent> testRunEvent, Event<StepMessage> stepMessage) {
        this.testRunEvent = testRunEvent;
        this.stepMessage = stepMessage;
    }

    @Override
    public void returnTestRunReport(TestRun testRun, TestRunReportBuilder report){
        testRunEvent.fireAsync(new FinishTestRunEvent(testRun, report));
    }

    @Override
    public void executeStep(StepRun stepRun){
        stepMessage.fireAsync(new StartStepCommand(stepRun));
    }

    @Override
    public void returnTestSetReport(TestSetRun testSetRun, TestReport report){
        testRunEvent.fireAsync(new FinishTestSetEvent(testSetRun, report));
    }

    @Override
    public void emitErrorTestRunEvent(Exception exception){
        testRunEvent.fireAsync(new ErrorTestRunEvent(exception));
    }

}
