/*
 * Copyright 2026 IHE International.
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

package net.ihe.gazelle.maestro.engine.business;

import net.ihe.gazelle.maestro.api.business.Maestro;
import net.ihe.gazelle.maestro.api.business.MaestroObserver;
import net.ihe.gazelle.maestro.api.business.message.StartTestRun;
import net.ihe.gazelle.maestro.api.business.message.StartTestSuiteRun;

/**
 * Standalone implementation of {@link Maestro} that executes tests without persisting results.
 * Persisting test runs or test suite runs is not supported in this implementation.
 */
public class StandaloneMaestro implements Maestro {

   private final MaestroFacade maestroFacade;

   /**
    * Creates a new {@code StandaloneMaestro} using the provided {@link MaestroFacade}.
    *
    * @param maestroFacade the facade used to execute tests and test suites
    */
   public StandaloneMaestro(MaestroFacade maestroFacade) {
      this.maestroFacade = maestroFacade;
   }

   @Override
   public void executeTestSuite(StartTestSuiteRun startTestSuiteRun, MaestroObserver observer) {
      if (startTestSuiteRun.isPersist()) {
         throw new UnsupportedOperationException("Persisting test suite runs is not supported in StandaloneMaestro.");
      }
      maestroFacade.executeTestSuite(startTestSuiteRun, observer);
   }

   @Override
   public void executeTest(StartTestRun startTestRun, MaestroObserver observer) {
      if (startTestRun.isPersist()) {
         throw new UnsupportedOperationException("Persisting test runs is not supported in StandaloneMaestro.");
      }
      maestroFacade.executeTest(startTestRun, observer);
   }

}
