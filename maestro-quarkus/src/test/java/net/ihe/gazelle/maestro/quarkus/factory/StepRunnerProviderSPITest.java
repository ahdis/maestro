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

import net.ihe.gazelle.maestro.api.business.testrun.Step;
import net.ihe.gazelle.maestro.quarkus.StepRunnerProviderSPI;
import net.ihe.gazelle.maestro.quarkus.configuration.ServiceRegistryDAOImpl;
import net.ihe.gazelle.maestro.quarkus.configuration.ServiceRegistryImpl;
import net.ihe.gazelle.maestro.quarkus.handler.HandlerProviderSPI;
import net.ihe.gazelle.maestro.business.StepRunnerProvider;
import net.ihe.gazelle.maestro.quarkus.mock.DummyStep;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class StepRunnerProviderSPITest {

    @Test
    void createNotSupportedStep(){
        Step step = new DummyStep();
        StepRunnerProvider provider = new StepRunnerProviderSPI(new HandlerProviderSPI(new ServiceRegistryImpl(new ServiceRegistryDAOImpl()), null));
        assertThrows(IllegalArgumentException.class, () -> provider.create(step));
    }

}
