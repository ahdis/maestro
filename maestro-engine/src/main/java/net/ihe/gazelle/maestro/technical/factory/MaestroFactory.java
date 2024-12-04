package net.ihe.gazelle.maestro.technical.factory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import jakarta.ws.rs.Produces;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.interlay.adapter.BValidatorBuilderFactory;
import net.ihe.gazelle.maestro.Maestro;
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

    private final ValidatorBuilderFactory validatorBuilderFactory = new BValidatorBuilderFactory();

    @Inject
    public MaestroFactory(ServiceRegistry serviceRegistry, MaestroProducer maestroProducer, SessionStoreContextImpl sessionStoreContext) {
        this.serviceRegistry = serviceRegistry;
        this.maestroProducer = maestroProducer;
        this.sessionStoreContext = sessionStoreContext;
    }

    @Produces
    @Default
    StepRunnerProvider getStepRunnerProvider() {
        return new StepRunnerProviderSPI(new HandlerProviderSPI(serviceRegistry));
    }

    @Produces
    @Default
    Maestro getMaestro() {
        return new MaestroImpl(validatorBuilderFactory, maestroProducer, sessionStoreContext);
    }

}
