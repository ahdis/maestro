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
import net.ihe.gazelle.maestro.engine.business.MaestroEventProducer;
import net.ihe.gazelle.maestro.engine.business.MaestroImpl;
import net.ihe.gazelle.maestro.engine.business.TestRunner;
import net.ihe.gazelle.maestro.engine.business.context.SessionStore;
import net.ihe.gazelle.maestro.engine.business.TestReportRecordService;
import net.ihe.gazelle.maestro.engine.business.StepRunner;
import net.ihe.gazelle.maestro.engine.business.StepRunnerProvider;
import net.ihe.gazelle.maestro.api.business.SynchronousMaestro;
import org.eclipse.microprofile.config.inject.ConfigProperty;


@ApplicationScoped
public class MaestroFactory {


   @ConfigProperty(name = "quarkus.application.version")
   String applicationVersion;

   @ConfigProperty(name = "gzl.maestro.timeout", defaultValue = "60")
   int timeoutSeconds;

   private final MaestroEventProducer maestroEventProducer;
   private final SessionStore sessionStore;
   private final TestReportRecordService testReportRecordService;
   private final StepRunnerProvider stepRunnerProvider;

   @Inject
   public MaestroFactory(MaestroEventProducer maestroEventProducer,
                         SessionStore sessionStore,
                         TestReportRecordService testReportRecordService,
                         StepRunnerProvider stepRunnerProvider) {
      this.maestroEventProducer = maestroEventProducer;
      this.sessionStore = sessionStore;
      this.testReportRecordService = testReportRecordService;
      this.stepRunnerProvider = stepRunnerProvider;
   }

   @Produces
   @Default
   public SynchronousMaestro getSynchronousMaestro() {
      return new SynchronousMaestro(timeoutSeconds, getMaestroFacade());
   }

   @Produces
   @Default
   public MaestroImpl getMaestroFacade() {
      return new MaestroImpl(maestroEventProducer);
   }

   @Produces
   @Default
   public Maestro getMaestro() {
      return new TestRunner(maestroEventProducer, sessionStore, testReportRecordService, applicationVersion);
   }

   @Produces
   @Default
   public StepRunner getStepRunnerJob() {
      return new StepRunner(stepRunnerProvider, maestroEventProducer);
   }

}
