package net.ihe.gazelle.maestro.engine.business;

import net.ihe.gazelle.maestro.api.business.message.StartTestRun;
import net.ihe.gazelle.maestro.api.business.message.TestSuiteRunFinished;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;

/**
 * Produces events related to the execution of {@link TestSuiteRun} instances,
 * including commands to start test runs and notifications when a test suite run has finished.
 */
public interface TestSuiteEventProducer {

   /**
    * Sends a command to start a {@link TestRun} within a test suite run.
    *
    * @param startTestRun the event representing the start of a test run
    */
   void command(StartTestRun startTestRun);

   /**
    * Notifies that a {@link TestSuiteRun} has finished execution.
    *
    * @param testSuiteRunFinished the finished test suite run event to notify
    */
   void notify(TestSuiteRunFinished testSuiteRunFinished);

}
