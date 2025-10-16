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

package net.ihe.gazelle.maestro.engine.business;

import net.ihe.gazelle.errorhandling.business.UnexpectedErrorBuilder;
import net.ihe.gazelle.maestro.api.business.message.StepRunFinished;
import net.ihe.gazelle.maestro.api.business.testreport.StepResult;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRun;
import net.ihe.gazelle.maestro.api.business.message.StartStepRun;
import org.slf4j.Logger;

/**
 * A stepRunnerJob receive message from producer to execute Step, launch execution and send result to Maestro Listener
 */
public class StepRunner {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(StepRunner.class);

    private final StepRunnerProvider stepRunnerProvider;
    private final StepRunEventProducer stepRunEventProducer;

    public StepRunner(StepRunnerProvider stepRunnerProvider, StepRunEventProducer stepRunEventProducer) {
        this.stepRunnerProvider = stepRunnerProvider;
       this.stepRunEventProducer = stepRunEventProducer;
    }

    /**
     * Execute Step with a stepRunner corresponding to the step
     * @param startStepRun Message containing step to run
     */
    public void execute(StartStepRun startStepRun) {
        StepRun stepRun = startStepRun.getStepRun();
        try {
            net.ihe.gazelle.maestro.api.business.testrun.runner.StepRunner runner = stepRunnerProvider.create(stepRun.step());
            runner.run(stepRun)
                    .thenAccept(
                          stepRunReportBuilder -> stepRunEventProducer.notify(new StepRunFinished(startStepRun.getTestRunId(), stepRun, stepRunReportBuilder))
                    )
                    .exceptionally(e -> {
                        returnUnexpectedError(e, startStepRun.getTestRunId(), stepRun);
                        return null;
                    });
        } catch (Exception e) {
            returnUnexpectedError(e, startStepRun.getTestRunId(), stepRun);
        }
    }

    private void returnUnexpectedError(Throwable e, String testRunId, StepRun stepRun) {
        logger.error("Unexpected error during step execution", e);
        UnexpectedErrorBuilder unexpectedErrorBuilder = new UnexpectedErrorBuilder().fromThrowable(e);
        StepRunReportBuilder builder = new StepRunReportBuilder()
                .setStepId(stepRun.step().getName())
                .setType(stepRun.step().getType())
                .setResult(StepResult.UNDEFINED)
                .addUnexpectedError(unexpectedErrorBuilder);
       stepRunEventProducer.notify(new StepRunFinished(testRunId, stepRun, builder));
    }

}
