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

package net.ihe.gazelle.maestro.api.business.testrun.context;

import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testrun.StepCursor;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;

import java.util.ArrayList;
import java.util.List;

/**
 * TestRunSessionsStore is a structure used to store TestRun's context
 * and StepRunReportBuilder of executed step during TestRun execution.
 * It also contains a reference to the TestRun.
 */
public class TestRunSessionStore {

    private Context context;
    private TestRun testRun;
    private List<StepRunReportBuilder> stepReports;
    private StepCursor stepCursor;

    public TestRunSessionStore(Context context, TestRun testRun, List<StepRunReportBuilder> stepReports, StepCursor stepCursor) {
        this.context = context;
        this.testRun = testRun;
        this.stepReports = new ArrayList<>(stepReports);
        this.stepCursor = stepCursor;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public TestRun getTestRun() {
        return testRun;
    }

    public void setTestRun(TestRun testRun) {
        this.testRun = testRun;
    }

    public List<StepRunReportBuilder> getStepReports() {
        return new ArrayList<>(stepReports);
    }

    public void setStepReports(List<StepRunReportBuilder> stepReports) {
        this.stepReports = new ArrayList<>(stepReports);
    }

    public void addStepRunReport(StepRunReportBuilder stepReportBuilder) {
        stepReports.add(stepReportBuilder);
    }

    public StepCursor getStepCursor() {
        return stepCursor;
    }

    public void setStepCursor(StepCursor stepCursor) {
        this.stepCursor = stepCursor;
    }
}
