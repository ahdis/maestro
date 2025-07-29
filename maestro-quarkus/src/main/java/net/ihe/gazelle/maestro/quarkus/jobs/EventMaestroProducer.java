/*
 * Copyright 2025 Kereval.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ihe.gazelle.maestro.quarkus.jobs;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestSetRun;
import net.ihe.gazelle.maestro.api.business.testrun.runner.FinishedStepEvent;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StartStepCommand;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepMessage;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRun;
import net.ihe.gazelle.maestro.business.MaestroEventProducer;
import net.ihe.gazelle.maestro.quarkus.jobs.testrunevents.*;

/**
 * CDI Event is a specification that simplifies asynchronous messaging in Java microservices.
 * It provides a framework for building reactive systems using standardized annotations and APIs to
 * interact with message brokers, Observers in our case.
 * It supports concepts like event-driven architecture, message publishing/subscribing, and
 * processing with ease while integrating seamlessly into CDI based applications.
 */
@ApplicationScoped
@Default
public class EventMaestroProducer implements MaestroEventProducer {

    private final Event<TestRunEvent> testRunEvent;
    private final Event<StepMessage> stepMessageEvent;

    @Inject
    public EventMaestroProducer(Event<TestRunEvent> testRunEvent, Event<StepMessage> stepMessageEvent) {
        this.testRunEvent = testRunEvent;
        this.stepMessageEvent = stepMessageEvent;
    }

    @Override
    public void executeTestSet(TestSetRun testSetRun) {
        testRunEvent.fireAsync(new StartTestSetCommand(testSetRun));
    }

    @Override
    public void returnTestSetReport(TestSetRun testSetRun, TestReport report) {
        testRunEvent.fireAsync(new FinishTestSetEvent(testSetRun, report));
    }

    @Override
    public void returnTestRunReport(TestRun testRun, TestRunReportBuilder report) {
        testRunEvent.fireAsync(new FinishTestRunEvent(testRun, report));
    }

    @Override
    public void executeStep(StepRun stepRun) {
        stepMessageEvent.fireAsync(new StartStepCommand(stepRun));
    }

    @Override
    public void returnStepReport(StepRun stepRun, StepRunReportBuilder report) {
        stepMessageEvent.fireAsync(new FinishedStepEvent(report, stepRun));
    }

    @Override
    public void emitErrorTestRunEvent(Exception exception) {
        testRunEvent.fireAsync(new ErrorTestRunEvent(exception));
    }

}
