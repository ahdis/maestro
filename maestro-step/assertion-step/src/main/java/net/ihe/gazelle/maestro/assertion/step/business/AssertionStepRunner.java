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

package net.ihe.gazelle.maestro.assertion.step.business;

import net.ihe.gazelle.maestro.api.business.testreport.StepResult;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRun;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRunner;

import java.util.concurrent.CompletableFuture;

/**
 * Assertion Step is not implemented. It exists just for test purpose
 */
public class AssertionStepRunner implements StepRunner {

    private final AssertionHandler handler;

    public AssertionStepRunner(AssertionHandler assertionHandler) {
        handler = assertionHandler;
    }

    @Override
    public CompletableFuture<StepRunReportBuilder> run(StepRun stepRun) {
        boolean result = handler.assertion((AssertionStep) stepRun.step());
        return CompletableFuture.completedFuture(new StepRunReportBuilder()
                .setStepId(stepRun.step().getName())
                .setType(AssertionStep.TYPE)
                .setResult(result ? StepResult.PASSED : StepResult.FAILED)
        );
    }

    @Override
    public String getSupportedStep() {
        return AssertionStep.TYPE;
    }

}
