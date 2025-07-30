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

package net.ihe.gazelle.maestro.quarkus.jobs.testrunevents;

import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.api.business.testrun.TestSetRun;

/**
 * A message to notify that a TestSet Execution Ended and to send a TestReport
 */
public class FinishTestSetEvent implements TestRunEvent {

    private final TestReport testReport;
    private final TestSetRun testSetRun;

    public FinishTestSetEvent(TestSetRun testRuns, TestReport testReport) {
        this.testSetRun = testRuns;
        this.testReport = testReport;
    }

    public TestReport getTestReport() {
        return testReport;
    }

    public TestSetRun getTestSetRun() {
        return testSetRun;
    }
}
