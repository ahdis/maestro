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
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.userinteract.GradingHandler;
import net.ihe.gazelle.maestro.spi.business.StepExecutor;
import net.ihe.gazelle.maestro.spi.business.StepRun;

import java.util.List;

/**
 * Executor for grading step.
 * This executor displays messages to users and waits for their acknowledgment.
 */
public class GradingStepExecutor implements StepExecutor {

    private final GradingHandler gradingHandler;

    /**
     * Constructor for GradingStepExecutor.
     *
     * @param gradingHandler the handler to manage grading
     */
    public GradingStepExecutor(GradingHandler gradingHandler) {
        this.gradingHandler = gradingHandler;
    }

    @Override
    public StepRunReport execute(StepRun stepRun) {
        List<String> criteria = stepRun.getPropertyValue(GradingStepDefinition.CRITERIA);

        GradingCompleted gradingCompleted = gradingHandler.displayMessage(criteria, stepRun.getTimeout());
        return buildStepRunReport(stepRun, gradingCompleted);
    }

    private StepRunReport buildStepRunReport(StepRun stepRun, GradingCompleted gradingCompleted) {
        StepRunReportBuilder report = new StepRunReportBuilder()
                .setStepName(stepRun.getName())
                .setType(GradingStepDefinition.TYPE)
                .setOutputs(gradingCompleted.getGradingCriteria().stream()
                        .map(criterion -> new StringProperty()
                                .setName(criterion.getDescription())
                                .setValue(criterion.getCriterionResult().name())
                                .setUserId(criterion.getUserId())
                                .setDateTime(criterion.getDateTime()))
                        .toList())
                .setResult(gradingCompleted.getResult().toStepResult());
        if (gradingCompleted.getNote() != null && !gradingCompleted.getNote().isBlank()) {
            report.addOutput(new StringProperty(GradingStepDefinition.NOTE, gradingCompleted.getNote())
                    .setUserId(gradingCompleted.getUserId())
                    .setDateTime(gradingCompleted.getDateTime()));
        }
        return report.build();
    }
}
