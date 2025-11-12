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
import net.ihe.gazelle.maestro.api.business.message.*;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestRunValidator;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRunValidator;
import net.ihe.gazelle.maestro.engine.business.context.SessionStore;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class MaestroImpl implements Maestro {

   private final TestSuiteRunValidator testSuiteRunValidator = new TestSuiteRunValidator();
   private final TestRunValidator testRunValidator = new TestRunValidator();
    private final SessionStore<MaestroObserver> observers;
   private final MaestroEventProducer maestroEventProducer;
   private final TestSuiteEventProducer testSuiteEventProducer;

   public MaestroImpl(SessionStore<MaestroObserver> observers, MaestroEventProducer maestroEventProducer, TestSuiteEventProducer testSuiteEventProducer) {
       this.observers = observers;
       this.maestroEventProducer = maestroEventProducer;
      this.testSuiteEventProducer = testSuiteEventProducer;
   }

   @Override
   public void executeTestSuite(TestSuiteRun testSuiteRun, MaestroObserver observer) {
      testSuiteRunValidator.assertValid(testSuiteRun);
      String sessionId = UUID.randomUUID().toString();
      observers.addSession(sessionId, observer);
      maestroEventProducer.command(new StartTestSuiteRun(sessionId, testSuiteRun));
   }

   @Override
   public void executeTest(TestRun testRun, MaestroObserver observer) {
       testRunValidator.assertValid(testRun);
       String sessionId = UUID.randomUUID().toString();
       observers.addSession(sessionId, observer);
       maestroEventProducer.command(new StartTestRun(sessionId, testRun));
   }

   public void listen(StartTestRun startTestRun) {
      Optional.ofNullable(observers.getSession(startTestRun.getSessionId()))
            .ifPresent(observer -> observer.onTestRunStarted(
                  new TestRunStarted(startTestRun.getTestRun().getTest().getId())
            ));
   }

   public void listen(StartStepRun startStepRun) {
      Optional.ofNullable(observers.getSession(startStepRun.getSessionId()))
            .ifPresent(observer -> observer.onStepRunStarted(
                  new StepRunStarted(startStepRun.getTestId(), startStepRun.getStepRun().getId())
            ));
   }

   public void listen(StepRunFinished stepRunFinished) {
      Optional.ofNullable(observers.getSession(stepRunFinished.getSessionId()))
            .ifPresent(observer -> observer.onStepRunFinished(stepRunFinished));
   }

   public void listen(TestRunFinished testRunFinished) {
      Optional.ofNullable(observers.getSession(testRunFinished.getSessionId()))
            .ifPresent(observer -> observer.onTestRunFinished(testRunFinished));
   }

   public void listen(TestSuiteRunFinished testSuiteRunFinished) {
      Optional.ofNullable(observers.getSession(testSuiteRunFinished.getSessionId()))
            .ifPresent(observer -> {
                observer.onTestSuiteFinished(testSuiteRunFinished);
                observers.removeSession(testSuiteRunFinished.getSessionId());
            });
   }
}
