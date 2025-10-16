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

import net.ihe.gazelle.maestro.api.business.Maestro;
import net.ihe.gazelle.maestro.engine.business.TestRunner;
import net.ihe.gazelle.maestro.engine.business.context.SessionStoreImpl;
import net.ihe.gazelle.maestro.engine.business.TestReportRecordService.ItemReference;
import net.ihe.gazelle.maestro.quarkus.StepRunnerProviderSPI;
import net.ihe.gazelle.maestro.quarkus.configuration.ServiceRegistryDAOImpl;
import net.ihe.gazelle.maestro.quarkus.configuration.ServiceRegistryImpl;
import net.ihe.gazelle.maestro.engine.business.StepRunnerProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FactoryTest {

   MaestroFactory maestroFactory;
   ServicesFactory servicesFactory;

   @BeforeEach
   void setUp() {
      TestRunMockEvent testRunMockEvent = new TestRunMockEvent();

      servicesFactory = new ServicesFactory(
            new ServiceRegistryImpl(new ServiceRegistryDAOImpl()),
            null
      );

      maestroFactory = new MaestroFactory(
            new EventMaestroProducer(testRunMockEvent),
            new SessionStoreImpl(),
            testReport -> new ItemReference("test", null),
            servicesFactory.getStepRunnerProvider()
      );

   }

   @Test
   void getMaestroTest() {
      Maestro maestro = maestroFactory.getMaestro();
      assertNotNull(maestro);
      assertInstanceOf(TestRunner.class, maestro);
   }

   @Test
   void getStepRunnerProviderTest() {
      StepRunnerProvider provider = servicesFactory.getStepRunnerProvider();
      assertNotNull(provider);
      assertInstanceOf(StepRunnerProviderSPI.class, provider);
   }

}
