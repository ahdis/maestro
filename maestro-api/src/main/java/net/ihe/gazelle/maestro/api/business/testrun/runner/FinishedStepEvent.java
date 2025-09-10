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

package net.ihe.gazelle.maestro.api.business.testrun.runner;

import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;

/**
 * Step message used by a producer to notify listener that a step execution ended.
 * It contains a report builder loaded with infos to build executed step's report
 */
public class FinishedStepEvent extends StepMessage {

    private final StepRunReportBuilder stepRunReport;

    public FinishedStepEvent(StepRunReportBuilder stepRunReport, StepRun stepRun) {
        super(stepRun);
        this.stepRunReport = stepRunReport;
    }

    public StepRunReportBuilder getStepRunReport() {
        return stepRunReport;
    }

}
