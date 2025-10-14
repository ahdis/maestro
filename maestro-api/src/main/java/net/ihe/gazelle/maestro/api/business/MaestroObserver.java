package net.ihe.gazelle.maestro.api.business;

import net.ihe.gazelle.maestro.api.business.message.StepRunFinished;
import net.ihe.gazelle.maestro.api.business.message.StepRunStarted;
import net.ihe.gazelle.maestro.api.business.message.TestRunFinished;
import net.ihe.gazelle.maestro.api.business.message.TestRunStarted;
import net.ihe.gazelle.maestro.api.business.message.TestSuiteRunFinished;

public interface MaestroObserver {

   default void onTestRunStarted(TestRunStarted testRunStarted) {
      // Override only if needed
   }

   default void onStepRunStarted(StepRunStarted stepRunStarted) {
      // Override only if needed
   }

   default void onStepRunFinished(StepRunFinished stepRunFinished) {
      // Override only if needed
   }

   default void onTestRunFinished(TestRunFinished testRunFinished) {
      // Override only if needed
   }

   void onTestSuiteFinished(TestSuiteRunFinished testSuiteRunFinished);

}
