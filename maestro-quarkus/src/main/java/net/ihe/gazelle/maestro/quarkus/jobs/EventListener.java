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

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;
import net.ihe.gazelle.maestro.api.business.message.StartTestRun;
import net.ihe.gazelle.maestro.api.business.message.StartTestSuiteRun;
import net.ihe.gazelle.maestro.api.business.message.TestRunFinished;
import net.ihe.gazelle.maestro.api.business.message.TestSuiteRunFinished;
import net.ihe.gazelle.maestro.api.business.testrun.Maestro;
import net.ihe.gazelle.maestro.api.business.message.StepRunFinished;
import net.ihe.gazelle.maestro.api.business.message.StartStepRun;
import net.ihe.gazelle.maestro.api.technical.event.*;
import net.ihe.gazelle.maestro.business.StepRunnerJob;
import net.ihe.gazelle.maestro.business.MaestroFacade;
import net.ihe.gazelle.maestro.business.SynchronousMaestro;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the event broker that listen to all kind of messages used in Maestro application
 * It will trigger application code depending on which event is fired.
 */
public class EventListener {

    private static final Logger LOG = LoggerFactory.getLogger(EventListener.class);

    private final Maestro engine;
    private final MaestroFacade maestroFacade;
    private final StepRunnerJob stepRunnerJob;

    @Inject
    public EventListener(Maestro engine, SynchronousMaestro synchronousMaestro, MaestroFacade maestroFacade, StepRunnerJob stepRunnerJob) {
        this.engine = engine;
        this.maestroFacade = maestroFacade;
        this.stepRunnerJob = stepRunnerJob;
    }

    public void onStartTestSet(@ObservesAsync StartTestSuiteRun startTestSuiteRun) {
        engine.run(startTestSuiteRun.testSuiteRun());
    }

    public void onFinishTestSet(@ObservesAsync TestSuiteRunFinished testSuiteRunFinished) {
       maestroFacade.onTestSuiteFinished(testSuiteRunFinished);
    }

    public void onStartTestRun(@Observes StartTestRun startTestRun) {
        engine.run(startTestRun.testRun());
    }

    public void onFinishTestRun(@ObservesAsync TestRunFinished testRunFinished) {
        engine.finishTest(testRunFinished.testRun(), testRunFinished.testRunReportBuilder());
    }

    public void onStartStep(@ObservesAsync StartStepRun startStepRun) {
        stepRunnerJob.launchStepExecution(startStepRun);
    }

    public void onFinishStep(@ObservesAsync StepRunFinished stepRunFinished) {
        engine.finishStep(stepRunFinished.stepRun(), stepRunFinished.stepRunReportBuilder());
    }

    public void onErrorTestRunEvent(@ObservesAsync ErrorTestRunEvent errorTestRunEvent) {
        Exception exception = errorTestRunEvent.getException();
        LOG.error("Exception caught at EventListener {}", exception.getMessage(), exception);
    }
}
