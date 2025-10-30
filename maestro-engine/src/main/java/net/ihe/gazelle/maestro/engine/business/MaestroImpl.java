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

package net.ihe.gazelle.maestro.engine.business;

import net.ihe.gazelle.maestro.api.business.Maestro;
import net.ihe.gazelle.maestro.api.business.MaestroObserver;
import net.ihe.gazelle.maestro.api.business.message.StartStepRun;
import net.ihe.gazelle.maestro.api.business.message.StartTestRun;
import net.ihe.gazelle.maestro.api.business.message.StartTestSuiteRun;
import net.ihe.gazelle.maestro.api.business.message.StepRunFinished;
import net.ihe.gazelle.maestro.api.business.message.StepRunStarted;
import net.ihe.gazelle.maestro.api.business.message.TestRunFinished;
import net.ihe.gazelle.maestro.api.business.message.TestRunStarted;
import net.ihe.gazelle.maestro.api.business.message.TestSuiteRunFinished;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRunValidator;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class MaestroImpl implements Maestro {

   private static final Map<String, MaestroObserver> OBSERVERS = new ConcurrentHashMap<>();
   private final TestSuiteRunValidator testSuiteRunValidator = new TestSuiteRunValidator();
   private final MaestroEventProducer maestroEventProducer;

   public MaestroImpl(MaestroEventProducer maestroEventProducer) {
      this.maestroEventProducer = maestroEventProducer;
   }

   @Override
   public void executeTestSuite(TestSuiteRun testSuiteRun, MaestroObserver observer) {
      testSuiteRunValidator.assertValid(testSuiteRun);
      String sessionId = UUID.randomUUID().toString();
      OBSERVERS.put(sessionId, observer);
      maestroEventProducer.command(new StartTestSuiteRun(sessionId, testSuiteRun));
   }

   @Override
   public void executeTest(TestRun testRun, MaestroObserver observer) {
      // TODO to implement
   }

   public void listen(StartTestRun startTestRun) {
      Optional.ofNullable(OBSERVERS.get(startTestRun.getSessionId()))
            .ifPresent(observer -> observer.onTestRunStarted(
                  new TestRunStarted(startTestRun.getTestRun().getTest().getId())
            ));
   }

   public void listen(StartStepRun startStepRun) {
      Optional.ofNullable(OBSERVERS.get(startStepRun.getSessionId()))
            .ifPresent(observer -> observer.onStepRunStarted(
                  new StepRunStarted(startStepRun.getTestId(), startStepRun.getStepRun().getId())
            ));
   }

   public void listen(StepRunFinished stepRunFinished) {
      Optional.ofNullable(OBSERVERS.get(stepRunFinished.getSessionId()))
            .ifPresent(observer -> observer.onStepRunFinished(stepRunFinished));
   }

   public void listen(TestRunFinished testRunFinished) {
      Optional.ofNullable(OBSERVERS.get(testRunFinished.getSessionId()))
            .ifPresent(observer -> observer.onTestRunFinished(testRunFinished));
   }

   public void listen(TestSuiteRunFinished testSuiteRunFinished) {
      Optional.ofNullable(OBSERVERS.get(testSuiteRunFinished.getSessionId()))
            .ifPresent(observer -> observer.onTestSuiteFinished(testSuiteRunFinished));
   }
}
