package net.ihe.gazelle.maestro.quarkus.client;

import net.ihe.gazelle.maestro.api.technical.event.StepRunFinished;
import net.ihe.gazelle.maestro.api.technical.event.StepRunStarted;
import net.ihe.gazelle.maestro.api.technical.event.TestRunFinished;
import net.ihe.gazelle.maestro.api.technical.event.TestRunStarted;
import net.ihe.gazelle.maestro.api.technical.event.TestSuiteFinished;

public interface MaestroCaller {

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

   void onTestSuiteFinished(TestSuiteFinished testSuiteFinished);

}
