/*
 * Copyright 2025 Kereval.
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

package net.ihe.gazelle.maestro.business;

import net.ihe.gazelle.maestro.api.business.MaestroObserver;
import net.ihe.gazelle.maestro.api.business.testrun.MaestroEventProducer;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;
import net.ihe.gazelle.maestro.api.business.testrun.validator.TestSetRunValidator;
import net.ihe.gazelle.maestro.api.business.message.TestSuiteRunFinished;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class MaestroFacade {

   private static final Map<String, MaestroObserver> OBSERVERS = new ConcurrentHashMap<>();
   private final TestSetRunValidator testSetRunValidator = new TestSetRunValidator();
   private final MaestroEventProducer maestroEventProducer;

   public MaestroFacade(MaestroEventProducer maestroEventProducer) {
      this.maestroEventProducer = maestroEventProducer;
   }

   public void executeTestSuite(TestSuiteRun testSuiteRun, MaestroObserver observer) {
      testSetRunValidator.assertValid(testSuiteRun);
      OBSERVERS.put(testSuiteRun.getInternalId(), observer);
      maestroEventProducer.executeTestSet(testSuiteRun);
   }

   public void onTestSuiteFinished(TestSuiteRunFinished testSuiteRunFinished) {
      MaestroObserver observer = OBSERVERS.get(testSuiteRunFinished.testSuiteRun().getInternalId());
      observer.onTestSuiteFinished(testSuiteRunFinished);
   }

}
