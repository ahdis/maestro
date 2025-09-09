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
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRun;
import net.ihe.gazelle.maestro.assertion.step.technical.handler.AssertionHandlerImpl;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class AssertionStepRunnerTest {

    private static AssertionStep buildStep(String name) {
        Property<byte[]> assertion = new Property<>(AssertionStep.ASSERTION, new byte[]{1});
        Property<String> assertionService = new Property<>(AssertionStep.ASSERTION_SERVICE, "dummy");
        return new AssertionStep(name, assertion, assertionService);
    }

    @Test
    void run_returnsPassedReport_whenHandlerReturnsTrue() throws Exception {
        AssertionHandler handler = new AssertionHandler() {
            @Override
            public boolean assertion(AssertionStep assertionStep) {
                return true;
            }

            @Override
            public boolean isAvailable() {
                return true;
            }
        };

        AssertionStepRunner runner = new AssertionStepRunner(handler);
        AssertionStep step = buildStep("assert-step-pass");
        StepRun stepRun = new StepRun("ts", "tr", step);

        CompletableFuture<StepRunReportBuilder> future = runner.run(stepRun);
        StepRunReportBuilder builder = future.get(1, TimeUnit.SECONDS);
        StepRunReport report = builder.build();

        assertEquals(step.getName(), report.getStepId());
        assertEquals(AssertionStep.TYPE, report.getType());
        assertEquals(StepResult.PASSED, report.getResult());
        assertTrue(report.isValid());
    }

    @Test
    void run_returnsFailedReport_whenHandlerReturnsFalse() throws Exception {
        AssertionHandler handler = new AssertionHandler() {
            @Override
            public boolean assertion(AssertionStep assertionStep) {
                return false;
            }

            @Override
            public boolean isAvailable() {
                return true;
            }
        };

        AssertionStepRunner runner = new AssertionStepRunner(handler);
        AssertionStep step = buildStep("assert-step-fail");
        StepRun stepRun = new StepRun("ts", "tr", step);

        CompletableFuture<StepRunReportBuilder> future = runner.run(stepRun);
        StepRunReportBuilder builder = future.get(1, TimeUnit.SECONDS);
        StepRunReport report = builder.build();

        assertEquals(step.getName(), report.getStepId());
        assertEquals(AssertionStep.TYPE, report.getType());
        assertEquals(StepResult.FAILED, report.getResult());
        assertTrue(report.isValid());
    }

    @Test
    void getSupportedStep_returnsAssertionType() {
        AssertionStepRunner runner = new AssertionStepRunner(new AssertionHandlerImpl());
        assertEquals(AssertionStep.TYPE, runner.getSupportedStep());
    }
}
