package net.ihe.gazelle.maestro.api.business;

import net.ihe.gazelle.maestro.api.business.message.*;

import java.util.concurrent.CompletableFuture;

public interface MaestroObserver {

   default void onTestRunStarted(TestRunStarted testRunStarted) {
       // Override only if needed
   }

   default void onStepRunStarted(StepRunStarted stepRunStarted) {
       // Override only if needed
   }

   CompletableFuture<UserInteractionCompleted> interactWithUser(InteractWithUser interactWithUser);

   default void onStepRunFinished(StepRunFinished stepRunFinished) {
       // Override only if needed
   }

   default void onTestRunFinished(TestRunFinished testRunFinished) {
       // Override only if needed
   }

   void onTestSuiteFinished(TestSuiteRunFinished testSuiteRunFinished);

}
