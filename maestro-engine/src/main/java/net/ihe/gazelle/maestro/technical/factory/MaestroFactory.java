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
