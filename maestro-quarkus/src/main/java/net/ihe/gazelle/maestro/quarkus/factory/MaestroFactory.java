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
import net.ihe.gazelle.maestro.api.business.testrun.Maestro;
import net.ihe.gazelle.maestro.api.business.testrun.MaestroEventProducer;
import net.ihe.gazelle.maestro.api.business.testrun.runner.ConfigProvider;
import net.ihe.gazelle.maestro.business.MaestroImpl;
import net.ihe.gazelle.maestro.business.SessionStoreContext;
import net.ihe.gazelle.maestro.business.TestReportRecordService;
import net.ihe.gazelle.maestro.quarkus.ServiceRegistry;
import net.ihe.gazelle.maestro.business.SessionStoreContextImpl;
import net.ihe.gazelle.maestro.quarkus.StepRunnerProviderSPI;
import net.ihe.gazelle.maestro.quarkus.handler.HandlerProviderSPI;
import net.ihe.gazelle.maestro.quarkus.jobs.StepRunnerProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;


@ApplicationScoped
public class MaestroFactory {


    @ConfigProperty(name = "quarkus.application.version")
    String applicationVersion;

    private final ServiceRegistry serviceRegistry;
    private final MaestroEventProducer maestroEventProducer;
    private final SessionStoreContext sessionStoreContext;
    private final TestReportRecordService testReportRecordService;
    private final ConfigProvider configProvider;

    @Inject
    public MaestroFactory(ServiceRegistry serviceRegistry, MaestroEventProducer maestroEventProducer,
                          SessionStoreContext sessionStoreContext, ConfigProvider configProvider,
                          TestReportRecordService testReportRecordService) {
        this.testReportRecordService = testReportRecordService;
        this.serviceRegistry = serviceRegistry;
        this.maestroEventProducer = maestroEventProducer;
        this.sessionStoreContext = sessionStoreContext;
        this.configProvider = configProvider;
    }

    @Produces
    @Default
    public StepRunnerProvider getStepRunnerProvider() {
        return new StepRunnerProviderSPI(new HandlerProviderSPI(serviceRegistry, configProvider));
    }

    @Produces
    @Default
    public Maestro getMaestro() {
        return new MaestroImpl(maestroEventProducer, sessionStoreContext, testReportRecordService, applicationVersion);
    }

}
