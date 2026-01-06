package net.ihe.gazelle.maestro.api.business;

import net.ihe.gazelle.maestro.api.business.message.*;

import java.util.concurrent.CompletableFuture;

/**
 * Observer interface for receiving updates during test execution.
 */
public interface MaestroObserver {

   /**
    * Called when a test run starts.
    *
    * @param testRunStarted information about the started test run
    */
   default void onTestRunStarted(TestRunStarted testRunStarted) {
      // Override only if needed
   }

   /**
    * Called when a step run starts.
    *
    * @param stepRunStarted information about the started step run
    */
   default void onStepRunStarted(StepRunStarted stepRunStarted) {
      // Override only if needed
   }

   /**
    * Interacts with the user during test execution.
    *
    * @param interactWithUser the user interaction request
    * @return a CompletableFuture that completes when the interaction is finished
    */
   CompletableFuture<UserInteractionCompleted> interactWithUser(InteractWithUser interactWithUser);

   /**
    * Called when a step run finishes.
    *
    * @param stepRunFinished information about the finished step run
    */
   default void onStepRunFinished(StepRunFinished stepRunFinished) {
      // Override only if needed
   }

   /**
    * Called when a test run finishes.
    *
    * @param testRunFinished information about the finished test run
    */
   default void onTestRunFinished(TestRunFinished testRunFinished) {
      // Override only if needed
   }

   /**
    * Called when the entire execution finishes.
    *
    * @param executionFinished information about the finished execution
    */
   void onExecutionFinished(ExecutionFinished executionFinished);
}
