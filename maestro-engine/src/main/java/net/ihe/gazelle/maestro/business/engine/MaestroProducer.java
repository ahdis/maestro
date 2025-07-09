/*
 * Copyright 2025 IHE International.
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

package net.ihe.gazelle.maestro.business.engine;

import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestSetRun;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRun;

/**
 * Producer is used to send message via a chanel to be caught by a listener of this chanel.
 * This tool allow different parts of Maestro to execute asynchronously.
 * Main interactions are between service calling for an automated test execution and Maestro engine
 * and between Maestro engine and StepRunners.
 */
public interface MaestroProducer {

    /**
     * Method called when starting a new test set execution
     * @param testSetRun The set of tests to be run
     */
    void executeTestSet(TestSetRun testSetRun);

    /**
     * Method called when a testSet execution ended to return result
     * @param testSetRun Previously executed TestSet
     * @param report The global execution return of testSetRun
     */
    void returnTestSetReport(TestSetRun testSetRun, TestReport report);

    /**
     * Method called when a TestRun execution ended to continue iteration on TestRuns
     * @param testRun Previously executed TestRun
     * @param report A reportBuilder of testRun execution
     */
    void returnTestRunReport(TestRun testRun, TestRunReportBuilder report);

    /**
     * Method called to launch a step execution
     * @param stepRun structure containing step to run
     */
    void executeStep(StepRun stepRun);

    /**
     * Method called to finish a step execution and add its report the global test report
     * @param stepRun The step that has been run
     * @param report The report builder to add to global test report
     */
    void returnStepReport(StepRun stepRun, StepRunReportBuilder report);

    /**
     * Method called when a blocking error occurred during execution
     * @param exception An exception describing the error
     */
    void emitErrorTestRunEvent(Exception exception);
}
