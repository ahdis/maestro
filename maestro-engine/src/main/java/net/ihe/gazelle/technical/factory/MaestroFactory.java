package net.ihe.gazelle.technical.factory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.ws.rs.Produces;
import net.ihe.gazelle.business.engine.EngineImpl;
import net.ihe.gazelle.business.engine.runner.StepRunnerProvider;
import net.ihe.gazelle.engine.Engine;
import net.ihe.gazelle.handler.HandlerProviderSPI;
import net.ihe.gazelle.technical.StepRunnerProviderSPI;

@ApplicationScoped
public class MaestroFactory {

    @Produces
    StepRunnerProvider getStepRunnerProvider() {
        return new StepRunnerProviderSPI(new HandlerProviderSPI());
    }

    @Produces
    @Default
    Engine getEngine() {
        return new EngineImpl(getStepRunnerProvider());
    }

}
