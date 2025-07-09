/*
 * Copyright 2025 IHE International.
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

package net.ihe.gazelle.maestro.technical.factory;

import net.ihe.gazelle.maestro.api.business.testrun.Maestro;
import net.ihe.gazelle.maestro.business.engine.MaestroImpl;
import net.ihe.gazelle.maestro.technical.SessionStoreContextImpl;
import net.ihe.gazelle.maestro.technical.StepRunnerProviderSPI;
import net.ihe.gazelle.maestro.technical.configuration.ServiceRegistryDAOImpl;
import net.ihe.gazelle.maestro.technical.configuration.ServiceRegistryImpl;
import net.ihe.gazelle.maestro.technical.jobs.EventMaestroProducer;
import net.ihe.gazelle.maestro.technical.jobs.StepMockEvent;
import net.ihe.gazelle.maestro.technical.jobs.StepRunnerProvider;
import net.ihe.gazelle.maestro.technical.jobs.TestRunMockEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MaestroFactoryTest {

    MaestroFactory maestroFactory;

    @BeforeEach
    public void setUp() {
        TestRunMockEvent testRunMockEvent = new TestRunMockEvent();
        StepMockEvent stepMockEvent = new StepMockEvent();
        maestroFactory = new MaestroFactory(new ServiceRegistryImpl(new ServiceRegistryDAOImpl()),
                new EventMaestroProducer(testRunMockEvent, stepMockEvent),
                new SessionStoreContextImpl(),
                null);
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
