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

package net.ihe.gazelle.maestro.api.business.testrun;

import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testreport.TestRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRun;

/**
 * Maestro execution engine. It is used to manage iteration on step and different structure of an automated test.
 */
public interface Maestro {

    /**
     * Start execution of a TestRun.
     *
     * @param testRun the testRun containing automated steps to execute
     *
     * @throws InvalidTestRunException if testRun is null or not valid
     */
    void run(TestRun testRun);

    /**
     * Execution all tests of a test suite
     *
     * @param testSuiteRun the testSuiteRun containing a list of testRun and an id
     *
     * @throws InvalidTestSetException if testSuiteRun is null or not valid
     */
    void run(TestSuiteRun testSuiteRun);

    /**
     * Method called by MaestroListener when receiving a TestRunFinished message
     * @param testRun The testRun previously executed
     * @param testRunReportBuilder The Report Builder of previous TestRun execution
     */
    void finishTest(TestRun testRun, TestRunReportBuilder testRunReportBuilder);

    /**
     * Method called by MaestroListener when receiving a FinishedStepMessage
     * @param stepRun The StepRun previously executed
     * @param stepRunReportBuilder The report builder of previous executed step
     */
    void finishStep(StepRun stepRun, StepRunReportBuilder stepRunReportBuilder);
}
