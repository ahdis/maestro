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
import net.ihe.gazelle.maestro.api.business.testrun.Maestro;
import net.ihe.gazelle.maestro.api.business.testrun.runner.FinishedStepEvent;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StartStepCommand;
import net.ihe.gazelle.maestro.api.technical.event.*;
import net.ihe.gazelle.maestro.quarkus.ws.async.AsynchronousMaestro;
import net.ihe.gazelle.maestro.quarkus.ws.async.tmintegration.TMTestRunMaestro;
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
        }else {
            synchronousMaestro.completeFuture(finishTestSetEvent);
        }
    }

    public void onStartTestRun(@Observes StartTestRunCommand startTestRunCommand) {
        engine.run(startTestRunCommand.getTestRun());
    }

    public void onFinishTestRun(@ObservesAsync FinishTestRunEvent finishTestRunEvent) {
        engine.finishTest(finishTestRunEvent.getTestRun(), finishTestRunEvent.getTestRunReportBuilder());
    }

    public void onStartStep(@ObservesAsync StartStepCommand startStepCommand) {
        stepRunnerJob.launchStepExecution(startStepCommand);
    }

    public void onFinishStep(@ObservesAsync FinishedStepEvent finishedStepEvent) {
        engine.finishStep(finishedStepEvent.getStepRun(), finishedStepEvent.getStepRunReport());
    }

    public void onErrorTestRunEvent(@ObservesAsync ErrorTestRunEvent errorTestRunEvent) {
        Exception exception = errorTestRunEvent.getException();
        LOG.error("Exception caught at EventListener {}", exception.getMessage(), exception);
    }
}
