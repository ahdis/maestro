package net.ihe.gazelle.maestro.technical.step.simulation;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;
import net.ihe.gazelle.simulation.business.callback.SimulationCallback;
import net.ihe.gazelle.simulation.client.business.SimulationClientImpl;
import net.ihe.gazelle.simulation.client.technical.SimulationServiceClientImpl;

public class SimulationCallbackFactory {

    @Produces
    @Default
    @RequestScoped
    public SimulationCallback createSimulationCallback() {
        return new SimulationClientImpl(new SimulationServiceClientImpl("", ""));
    }
}
