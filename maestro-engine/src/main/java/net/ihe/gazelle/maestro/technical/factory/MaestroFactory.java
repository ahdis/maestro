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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import jakarta.ws.rs.Produces;
import net.ihe.gazelle.maestro.api.business.testrun.Maestro;
import net.ihe.gazelle.maestro.api.business.testrun.runner.ConfigProvider;
import net.ihe.gazelle.maestro.business.engine.MaestroImpl;
import net.ihe.gazelle.maestro.business.engine.MaestroProducer;
import net.ihe.gazelle.maestro.technical.ServiceRegistry;
import net.ihe.gazelle.maestro.technical.SessionStoreContextImpl;
import net.ihe.gazelle.maestro.technical.StepRunnerProviderSPI;
import net.ihe.gazelle.maestro.technical.handler.HandlerProviderSPI;
import net.ihe.gazelle.maestro.technical.jobs.StepRunnerProvider;


@ApplicationScoped
public class MaestroFactory {

    ServiceRegistry serviceRegistry;

    MaestroProducer maestroProducer;

    SessionStoreContextImpl sessionStoreContext;

    ConfigProvider configProvider;

    @Inject
    public MaestroFactory(ServiceRegistry serviceRegistry, MaestroProducer maestroProducer, SessionStoreContextImpl sessionStoreContext, ConfigProvider configProvider) {
        this.serviceRegistry = serviceRegistry;
        this.maestroProducer = maestroProducer;
        this.sessionStoreContext = sessionStoreContext;
        this.configProvider = configProvider;
    }

    @Produces
    @Default
    StepRunnerProvider getStepRunnerProvider() {
        return new StepRunnerProviderSPI(new HandlerProviderSPI(serviceRegistry, configProvider));
    }

    @Produces
    @Default
    Maestro getMaestro() {
        return new MaestroImpl(maestroProducer, sessionStoreContext);
    }

}
