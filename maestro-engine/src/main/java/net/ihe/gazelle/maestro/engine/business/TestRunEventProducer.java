package net.ihe.gazelle.maestro.engine.business;

import net.ihe.gazelle.maestro.api.business.message.StartStepRun;
import net.ihe.gazelle.maestro.api.business.message.TestRunFinished;
import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;

/**
 * Produces events related to the execution of {@link TestRun} instances.
 */
public interface TestRunEventProducer {

   /**
    * Sends a command to start a {@link Step} execution within a test run.
    *
    * @param startStepRun the event representing the start of a step run
    */
   void command(StartStepRun startStepRun);

   /**
    * Notifies that a {@link TestRun} has finished execution.
    *
    * @param testRunFinished the finished test run event to notify
    */
   void notify(TestRunFinished testRunFinished);

}
