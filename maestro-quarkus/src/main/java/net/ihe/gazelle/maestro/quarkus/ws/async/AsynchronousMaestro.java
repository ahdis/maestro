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

package net.ihe.gazelle.maestro.quarkus.ws.async;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.ihe.gazelle.maestro.api.business.testrun.MaestroEventProducer;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;
import net.ihe.gazelle.maestro.api.business.testrun.validator.TestSetRunValidator;
import net.ihe.gazelle.maestro.api.technical.event.TestSuiteFinished;
import net.ihe.gazelle.maestro.quarkus.client.MaestroCaller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class AsynchronousMaestro {

   private static final Map<String, MaestroCaller> RUNNING_SESSIONS = new ConcurrentHashMap<>();
   private final TestSetRunValidator testSetRunValidator = new TestSetRunValidator();
   private final MaestroEventProducer maestroEventProducer;

   @Inject
   public AsynchronousMaestro(MaestroEventProducer maestroEventProducer) {
      this.maestroEventProducer = maestroEventProducer;
   }

   public void executeTestSuite(TestSuiteRun testSuiteRun, MaestroCaller caller) {
      testSetRunValidator.assertValid(testSuiteRun);
      RUNNING_SESSIONS.put(testSuiteRun.getInternalId(), caller);
      maestroEventProducer.executeTestSet(testSuiteRun);
   }

   public void onTestSuiteFinished(TestSuiteFinished testSuiteFinished) {
      MaestroCaller caller = RUNNING_SESSIONS.get(testSuiteFinished.testSuiteRun().getInternalId());
      caller.onTestSuiteFinished(testSuiteFinished);
   }

}
