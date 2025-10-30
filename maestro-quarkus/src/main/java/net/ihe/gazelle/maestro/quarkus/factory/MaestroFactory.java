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
import net.ihe.gazelle.maestro.api.business.SynchronousMaestro;
import net.ihe.gazelle.maestro.engine.business.MaestroEventProducer;
import net.ihe.gazelle.maestro.engine.business.MaestroImpl;
import net.ihe.gazelle.maestro.engine.business.StepExecutorProvider;
import net.ihe.gazelle.maestro.engine.business.StepRunEventProducer;
import net.ihe.gazelle.maestro.engine.business.StepRunner;
import net.ihe.gazelle.maestro.engine.business.TestReportRecordService;
import net.ihe.gazelle.maestro.engine.business.TestRunEventProducer;
import net.ihe.gazelle.maestro.engine.business.TestRunner;
import net.ihe.gazelle.maestro.engine.business.TestSuiteEventProducer;
import net.ihe.gazelle.maestro.engine.business.TestSuiteRunner;
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
   private final MaestroEventProducer maestroEventProducer;
   private final TestSuiteEventProducer testSuiteEventProducer;
   private final TestRunEventProducer testRunEventProducer;
   private final StepRunEventProducer stepRunEventProducer;
   private final StepExecutorProvider stepExecutorProvider;
   private final TestReportRecordService testReportRecordService;
   private final MetadataService metadataService;

   @Inject
   public MaestroFactory(@TestSuiteStore SessionStore<TestSuiteSession> suiteSessionStore,
                         @TestRunStore SessionStore<TestRunSession> testSessionStore,
                         MaestroEventProducer maestroEventProducer,
                         TestSuiteEventProducer testSuitetestReportRecordService,
                         TestRunEventProducer testRunEventProducer,
                         StepRunEventProducer stepRunEventProducer,
                         TestReportRecordService testReportRecordService,
                         StepExecutorProvider stepExecutorProvider,
                         MetadataService metadataService) {
      this.suiteSessionStore = suiteSessionStore;
      this.testSessionStore = testSessionStore;
      this.maestroEventProducer = maestroEventProducer;
      this.testSuiteEventProducer = testSuitetestReportRecordService;
      this.testRunEventProducer = testRunEventProducer;
      this.stepRunEventProducer = stepRunEventProducer;
      this.testReportRecordService = testReportRecordService;
      this.stepExecutorProvider = stepExecutorProvider;
      this.metadataService = metadataService;
   }

   @Produces
   @Default
   public SynchronousMaestro getSynchronousMaestro() {
      return new SynchronousMaestro(timeoutSeconds, getMaestro());
   }

   @Produces
   @Default
   public Maestro getMaestro() {
      return new MaestroImpl(maestroEventProducer);
   }

   @Produces
   @Default
   public TestSuiteRunner getTestSuiteRunner() {
      return new TestSuiteRunner(suiteSessionStore, testSuiteEventProducer, testReportRecordService, metadataService);
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
