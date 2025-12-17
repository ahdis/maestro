package net.ihe.gazelle.maestro.engine.business;

import net.ihe.gazelle.maestro.api.business.Maestro;
import net.ihe.gazelle.maestro.api.business.MaestroObserver;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;

public class StandaloneMaestro implements Maestro {

   private final MaestroFacade maestroFacade;

   public StandaloneMaestro(MaestroFacade maestroFacade) {
      this.maestroFacade = maestroFacade;
   }

   @Override
   public void executeTestSuite(TestSuiteRun testSuiteRun, boolean persist, MaestroObserver observer) {
      if(persist) {
         throw new UnsupportedOperationException("Persisting test suite runs is not supported in StandaloneMaestro.");
      }
      maestroFacade.executeTestSuite(testSuiteRun, observer);
   }

   @Override
   public void executeTest(TestRun testRun, boolean persist, MaestroObserver observer) {
      if(persist) {
         throw new UnsupportedOperationException("Persisting test runs is not supported in StandaloneMaestro.");
      }
      maestroFacade.executeTest(testRun, observer);
   }

}
