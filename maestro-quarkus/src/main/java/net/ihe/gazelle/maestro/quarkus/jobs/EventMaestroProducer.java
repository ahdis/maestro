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
import jakarta.inject.Inject;
import net.ihe.gazelle.maestro.api.business.message.Message;
import net.ihe.gazelle.maestro.api.business.message.StartTestSuiteRun;
import net.ihe.gazelle.maestro.api.business.message.TestRunFinished;
import net.ihe.gazelle.maestro.api.business.message.TestSuiteRunFinished;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testreport.TestReportSummary;
import net.ihe.gazelle.maestro.api.business.testreport.TestRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testrun.MaestroEventProducer;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;
import net.ihe.gazelle.maestro.api.business.message.StepRunFinished;
import net.ihe.gazelle.maestro.api.business.message.StartStepRun;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRun;
import net.ihe.gazelle.maestro.api.technical.event.*;

/**
 * CDI Message is a specification that simplifies asynchronous messaging in Java microservices.
 * It provides a framework for building reactive systems using standardized annotations and APIs to
 * interact with message brokers, Observers in our case.
 * It supports concepts like event-driven architecture, message publishing/subscribing, and
 * processing with ease while integrating seamlessly into CDI based applications.
 */
@ApplicationScoped
public class EventMaestroProducer implements MaestroEventProducer {

    private final Event<Message> messageEvent;

    @Inject
    public EventMaestroProducer(Event<Message> messageEvent) {
        this.messageEvent = messageEvent;
    }

    @Override
    public void executeTestSet(TestSuiteRun testSuiteRun) {
        messageEvent.fireAsync(new StartTestSuiteRun(testSuiteRun));
    }

    @Override
    public void returnTestSetReport(TestSuiteRun testSuiteRun, TestReportSummary reportSummary) {
        messageEvent.fireAsync(new TestSuiteRunFinished(testSuiteRun, reportSummary));
    }

    @Override
    public void returnTestRunReport(TestRun testRun, TestRunReportBuilder report) {
        messageEvent.fireAsync(new TestRunFinished(testRun, report));
    }

    @Override
    public void executeStep(StepRun stepRun) {
       messageEvent.fireAsync(new StartStepRun(stepRun));
    }

    @Override
    public void returnStepReport(StepRun stepRun, StepRunReportBuilder report) {
       messageEvent.fireAsync(new StepRunFinished(stepRun, report));
    }

    @Override
    public void emitErrorTestRunEvent(Exception exception) {
        messageEvent.fireAsync(new ErrorTestRunEvent(exception));
    }

}
