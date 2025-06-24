package net.ihe.gazelle.maestro.technical.step.simulation;

import net.ihe.gazelle.maestro.api.business.testrun.runner.ConfigProvider;
import net.ihe.gazelle.maestro.api.business.testrun.runner.Handler;
import net.ihe.gazelle.maestro.api.business.testrun.runner.HandlerFactory;
import net.ihe.gazelle.servicemetadata.api.business.ConsumedInterface;
import net.ihe.gazelle.servicemetadata.api.business.ConsumedInterfaceBuilder;
import net.ihe.gazelle.servicemetadata.api.business.HttpRestBinding;
import net.ihe.gazelle.servicemetadata.api.business.ProvidedInterface;
import net.ihe.gazelle.simulation.client.business.SimulationClientImpl;
import net.ihe.gazelle.simulation.client.technical.SimulationCallbackFactory;
import net.ihe.gazelle.simulation.client.technical.SimulationServiceClientImpl;
import net.ihe.gazelle.simulation.jaxrs.api.technical.ws.SimulationAPI;

import java.util.List;
import java.util.NoSuchElementException;

public class GazelleSimulationServiceHandlerFactory implements HandlerFactory {

    @Override
    public Class<? extends Handler> getHandlerType() {
        return GazelleSimulationServiceHandler.class;
    }

    @Override
    public Handler createHandler(ProvidedInterface providedInterface, ConfigProvider configProvider) throws IllegalArgumentException {
        try {
            HttpRestBinding binding = (HttpRestBinding) providedInterface.getBindings().getFirst();
            return new GazelleSimulationServiceHandler(
                    new SimulationClientImpl(
                            new SimulationServiceClientImpl(binding.getServiceUrl(), configProvider.getConfig("gzl.maestro.url")),
                            SimulationCallbackFactory.getSimulationSessionDAO()
                    )
            );
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Service " + providedInterface.getInterfaceName() + " has no bindings");
        }
    }

    @Override
    public ConsumedInterface getConsumedInterface() {
        return new ConsumedInterfaceBuilder()
                .setInterfaceName(SimulationAPI.INTERFACE_NAME)
                .setRequired(true)
                .setSupportedBindings(List.of(HttpRestBinding.TYPE))
                .setSupportedVersions(List.of("1.0.0"))
                .build();
    }
}
