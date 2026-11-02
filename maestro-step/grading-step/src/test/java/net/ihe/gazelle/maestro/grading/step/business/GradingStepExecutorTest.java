/*
 * Copyright 2025-2026 IHE International.
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

package net.ihe.gazelle.maestro.grading.step.business;

import net.ihe.gazelle.maestro.api.business.message.GradingCompleted;
import net.ihe.gazelle.maestro.api.business.testreport.CriterionReport;
import net.ihe.gazelle.maestro.api.business.testreport.CriterionResult;
import net.ihe.gazelle.maestro.api.business.property.StringListProperty;
import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.business.testreport.StepResult;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.userinteract.stub.GradingHandlerStub;
import net.ihe.gazelle.maestro.spi.business.StepRun;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GradingStepExecutorTest {

    @Test
    void grading_step_executor_test() {
        GradingStepExecutor executor = new GradingStepExecutor(new GradingHandlerStub());
        StepRun stepRun = stepRun();

        StepRunReport report = executor.execute(stepRun);
        assertEquals(stepRun.getName(), report.getStepName());
        assertEquals(GradingStepDefinition.TYPE, report.getType());
        assertEquals(StepResult.PASSED, report.getResult());
    }

    @Test
    void exposesCriterionResultsWithReviewerAndAssessmentTime() {
        var assessmentTime = java.time.Instant.parse("2026-11-02T12:00:00Z");
        var executor = new GradingStepExecutor(new GradingHandlerStub() {
            @Override
            public GradingCompleted displayMessage(List<String> criteria, long timeout) {
                assertEquals(List.of("Check A", "Check B"), criteria);
                return new GradingCompleted().setResult(StepResult.FAILED).setCriteriaReport(List.of(
                        new CriterionReport().setDescription("Check A").setCriterionResult(CriterionResult.FAILED)
                                .setUserId("reviewer").setDateTime(assessmentTime)));
            }
        });
        var report = executor.execute(stepRun());
        assertEquals(StepResult.FAILED, report.getResult());
        assertEquals(1, report.getOutputs().size());
        var output = report.getOutput("Check A");
        assertEquals("FAILED", output.getValue());
        assertEquals("reviewer", output.getUserId());
        assertEquals(assessmentTime, output.getDateTime());
    }

    private Step step() {
        return new Step()
                .setName("Grading step")
                .setType(GradingStepDefinition.TYPE)
                .setProperties(List.of(
                        new StringListProperty(GradingStepDefinition.CRITERIA, List.of("Check A", "Check B"))
                ));
    }

    private StepRun stepRun() {
        return new StepRun(step(), List.of());
    }
}
