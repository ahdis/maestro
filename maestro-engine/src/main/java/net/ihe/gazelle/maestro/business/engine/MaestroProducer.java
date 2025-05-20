package net.ihe.gazelle.maestro.business.engine;

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
     * Method called when a TestRun execution ended to continue iteration on TestRuns
     * @param testRun Previously executed TestRun
     * @param report A reportBuilder of testRun execution
     */
    void returnTestRunReport(TestRun testRun, TestRunReportBuilder report);

    /**
     * Method call to launch a step execution
     * @param stepRun structure containing step to run
     */
    void executeStep(StepRun stepRun);

    /**
     * Method called when a testSet execution ended to return result
     * @param testSetRun Previously executed TestSet
     * @param report The global execution return of testSetRun
     */
    void returnTestSetReport(TestSetRun testSetRun, TestReport report);

    /**
     * Method called when a blocking error occurred during execution
     * @param exception An exception describing the error
     */
    void emitErrorTestRunEvent(Exception exception);
}
