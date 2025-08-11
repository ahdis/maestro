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

import net.ihe.gazelle.maestro.api.business.testrun.Maestro;
import net.ihe.gazelle.maestro.business.MaestroImpl;
import net.ihe.gazelle.maestro.quarkus.mock.StepMockEvent;
import net.ihe.gazelle.maestro.quarkus.SessionStoreContextImpl;
import net.ihe.gazelle.maestro.quarkus.StepRunnerProviderSPI;
import net.ihe.gazelle.maestro.quarkus.configuration.ServiceRegistryDAOImpl;
import net.ihe.gazelle.maestro.quarkus.configuration.ServiceRegistryImpl;
import net.ihe.gazelle.maestro.quarkus.jobs.EventMaestroProducer;
import net.ihe.gazelle.maestro.quarkus.jobs.StepRunnerProvider;
import net.ihe.gazelle.maestro.quarkus.record.DatahouseTestReportRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MaestroFactoryTest {

    MaestroFactory maestroFactory;

    @BeforeEach
    void setUp() {
        TestRunMockEvent testRunMockEvent = new TestRunMockEvent();
        StepMockEvent stepMockEvent = new StepMockEvent();
        maestroFactory = new MaestroFactory(new ServiceRegistryImpl(new ServiceRegistryDAOImpl()),
                new EventMaestroProducer(testRunMockEvent, stepMockEvent),
                new SessionStoreContextImpl(),
                null, testReport -> "test");
    }

    @Test
    void getMaestroTest(){
        Maestro maestro = maestroFactory.getMaestro();
        assertNotNull(maestro);
        assertInstanceOf(MaestroImpl.class, maestro);
    }

    @Test
    void getStepRunnerProviderTest(){
        StepRunnerProvider provider = maestroFactory.getStepRunnerProvider();
        assertNotNull(provider);
        assertInstanceOf(StepRunnerProviderSPI.class, provider);
    }

}
