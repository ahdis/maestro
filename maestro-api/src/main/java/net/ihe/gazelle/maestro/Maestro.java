package net.ihe.gazelle.maestro;

import net.ihe.gazelle.maestro.runner.StepRun;
import net.ihe.gazelle.testreport.StepRunReportBuilder;
import net.ihe.gazelle.testreport.TestRunReportBuilder;

/**
 * Maestro execution engine. It is used to manage iteration on step and different structure of an automated test.
 */
public interface Maestro {

    /**
     * Start execution of a TestRun by initializing a testRun session.
     * {@link net.ihe.gazelle.maestro.context.TestRunSessionStore}
     * <br>
     * Create a new context containing TestRun's inputs and run first test step of testRun if it's valid.
     * {@link net.ihe.gazelle.maestro.context.Context}
     * {@link TestRun}
     * <p>
     * If testRun is null, it emits an exception.
     * If testRun is not null, but is not valid, it sends a report with undefined result containing an unexpectedError
     * caused by <b>MissingArgumentException</b> or <b>DuplicatedStepsException</b>.
     *</p>
     * @param testRun the testRun containing automated steps to execute
     */
    void run(TestRun testRun);

    /**
     * Start execution of a test set by initializing a test set session.
     * {@link TestSetRun}
     * <br>
     * Instance a new TestSetSessionStore in SessionsStore.
     * {@link net.ihe.gazelle.maestro.context.TestSetSessionStore}
     * <br>
     * Run first testRun if it's valid.
     * <p>
     * If test set's id is null, it emits an InvalidTestSetException.
     * If test set's id is not null, but testSet is not valid, it sends a report with undefined result containing an unexpectedError
     * caused by an <b>IllegalArgumentException</b>
     * </p>
     * @param testSetRun the testSetRun containing a list of testRun and an id
     */
    void run(TestSetRun testSetRun);

    /**
     * Method called by MaestroListener when receiving a FinishTestRunEvent message
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
