package net.ihe.gazelle.maestro.technical.step.itb;

import net.ihe.gazelle.maestro.api.business.testrun.runner.ConfigProvider;
import net.ihe.gazelle.maestro.api.business.testrun.runner.Handler;
import net.ihe.gazelle.maestro.api.business.testrun.runner.HandlerFactory;
import net.ihe.gazelle.maestro.business.step.itb.ItbHandler;
import net.ihe.gazelle.maestro.technical.step.simulation.GazelleSimulationServiceHandler;
import net.ihe.gazelle.servicemetadata.api.business.*;
import net.ihe.gazelle.simulation.client.business.SimulationClientImpl;
import net.ihe.gazelle.simulation.client.technical.SimulationCallbackFactory;
import net.ihe.gazelle.simulation.client.technical.SimulationServiceClientImpl;
import net.ihe.gazelle.simulation.jaxrs.api.technical.ws.SimulationAPI;

import java.util.List;

public class ItbHandlerFactory implements HandlerFactory {

    @Override
    public Class<? extends Handler> getHandlerType() {
        return ItbHandlerImpl.class;
    }

    @Override
    public Handler createHandler(ProvidedInterface providedInterface, ConfigProvider configProvider) throws IllegalArgumentException {
        return new ItbHandlerImpl();
    }

    @Override
    public ConsumedInterface getConsumedInterface() {
        return new ConsumedInterfaceBuilder()
                .setInterfaceName("ITB API")
                .setRequired(true)
                .setSupportedBindings(List.of(HttpRestBinding.TYPE))
                .setSupportedVersions(List.of("1.0.0"))
                .build();
    }
}

