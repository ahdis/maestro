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

package net.ihe.gazelle.maestro.technical.jobs;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import net.ihe.gazelle.maestro.api.business.testreport.StepResult;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testreport.UnexpectedErrorBuilder;
import net.ihe.gazelle.maestro.api.business.testrun.runner.*;
import net.ihe.gazelle.maestro.business.MaestroEventProducer;

/**
 * A stepRunnerJob receive message from producer to execute Step, launch execution and send result to Maestro Listener
 */
@Dependent
@Default
public class StepRunnerJob {

    private final StepRunnerProvider stepRunnerFactory;
    private final MaestroEventProducer maestroEventProducer;

    @Inject
    public StepRunnerJob(StepRunnerProvider stepRunnerFactory, MaestroEventProducer maestroEventProducer) {
        this.stepRunnerFactory = stepRunnerFactory;
        this.maestroEventProducer = maestroEventProducer;
    }

    /**
     * Execute Step with a stepRunner corresponding to the step
     * @param startStepCommand Message containing step to run
     */
    public void launchStepExecution(StartStepCommand startStepCommand) {
        StepRun stepRun = startStepCommand.getStepRun();
        try {
            StepRunner runner = stepRunnerFactory.create(stepRun.step());
            if (runner instanceof AsyncStepRunner)
                runner.runAsync(stepRun);
            if (runner instanceof SyncStepRunner)
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
        UnexpectedErrorBuilder unexpectedErrorBuilder = new UnexpectedErrorBuilder()
                .fromThrowable(e);
        StepRunReportBuilder builder = new StepRunReportBuilder()
                .setStepId(stepRun.step().getName())
                .setType(stepRun.step().getType())
                .setResult(StepResult.UNDEFINED)
                .addUnexpectedError(unexpectedErrorBuilder);
        stepEnded(stepRun, builder);
    }

    /**
     * Fire an event containing StepRunReportBuilder and StepRun to EventListener
     * @see EventListener
     * @param stepRun StepRun previously executed
     * @param stepRunReportBuilder A StepReportBuilder containing data of stepRun execution
     */
    private void stepEnded(StepRun stepRun, StepRunReportBuilder stepRunReportBuilder) {
        maestroEventProducer.returnStepReport(stepRun, stepRunReportBuilder);
    }

}
