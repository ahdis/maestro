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

package net.ihe.gazelle.maestro.quarkus.factory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import jakarta.ws.rs.Produces;
import net.ihe.gazelle.maestro.api.business.Maestro;
import net.ihe.gazelle.maestro.api.business.MaestroObserver;
import net.ihe.gazelle.maestro.api.business.SynchronousMaestro;
import net.ihe.gazelle.maestro.engine.business.TestReportRecordingService;
import net.ihe.gazelle.maestro.engine.business.*;
import net.ihe.gazelle.maestro.engine.business.context.SessionStore;
import net.ihe.gazelle.maestro.engine.business.context.TestRunSession;
import net.ihe.gazelle.maestro.engine.business.context.TestSuiteSession;
import net.ihe.gazelle.servicemetadata.api.business.MetadataService;
import org.eclipse.microprofile.config.inject.ConfigProperty;


@ApplicationScoped
public class MaestroFactory {

   @ConfigProperty(name = "gzl.maestro.timeout", defaultValue = "60")
   int timeoutSeconds;

   private final SessionStore<TestSuiteSession> suiteSessionStore;
   private final SessionStore<TestRunSession> testSessionStore;
    private final SessionStore<MaestroObserver> testRunObserverStore;
    private final SessionStore<MaestroObserver> testSuiteRunObserverStore;
   private final MaestroEventProducer maestroEventProducer;
   private final TestSuiteEventProducer testSuiteEventProducer;
   private final TestRunEventProducer testRunEventProducer;
   private final StepRunEventProducer stepRunEventProducer;
   private final StepExecutorProvider stepExecutorProvider;
   private final TestReportRecordingService testReportRecordingService;
   private final MetadataService metadataService;

   @Inject
   public MaestroFactory(@TestSuiteStore SessionStore<TestSuiteSession> suiteSessionStore,
                         @TestRunStore SessionStore<TestRunSession> testSessionStore,
                         @TestRunObserverStore SessionStore<MaestroObserver> testRunObserverStore,
                         @TestSuiteRunObserverStore SessionStore<MaestroObserver> testSuiteRunObserverStore,
                         MaestroEventProducer maestroEventProducer,
                         TestSuiteEventProducer testSuiteTestReportRecordService,
                         TestRunEventProducer testRunEventProducer,
                         StepRunEventProducer stepRunEventProducer,
                         TestReportRecordingService testReportRecordingService,
                         StepExecutorProvider stepExecutorProvider,
                         MetadataService metadataService) {
      this.suiteSessionStore = suiteSessionStore;
      this.testSessionStore = testSessionStore;
      this.testRunObserverStore = testRunObserverStore;
      this.testSuiteRunObserverStore = testSuiteRunObserverStore;
      this.maestroEventProducer = maestroEventProducer;
      this.testSuiteEventProducer = testSuiteTestReportRecordService;
      this.testRunEventProducer = testRunEventProducer;
      this.stepRunEventProducer = stepRunEventProducer;
      this.testReportRecordingService = testReportRecordingService;
      this.stepExecutorProvider = stepExecutorProvider;
      this.metadataService = metadataService;
   }

   @Produces
   @Default
   public SynchronousMaestro getSynchronousMaestro() {
      return new SynchronousMaestro(timeoutSeconds, getRecordingMaestro());
   }

   @Produces
   @Default
   public MaestroImpl getMaestro() {
       return new MaestroImpl(
               testRunObserverStore,
               testSuiteRunObserverStore,
               maestroEventProducer,
               metadataService
       );
   }

   @Produces
   @Default
   public Maestro getRecordingMaestro() {
       return new RecordingMaestroImpl(
               getMaestro(),
             testReportRecordingService);
   }

   @Produces
   @Default
   public TestSuiteRunner getTestSuiteRunner() {
      return new TestSuiteRunner(suiteSessionStore, testSuiteEventProducer);
   }

   @Produces
   @Default
   public TestRunner getTestRunner() {
      return new TestRunner(testSessionStore, testRunEventProducer);
   }

   @Produces
   @Default
   public StepRunner getStepRunnerJob() {
      return new StepRunner(stepExecutorProvider, stepRunEventProducer);
   }

}
