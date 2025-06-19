package net.ihe.gazelle.maestro.technical.step.simulation;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import net.ihe.gazelle.simulation.business.callback.SimulationCallback;
import net.ihe.gazelle.simulation.technical.jaxrs.SimulationCallbackController;

@Path("/")
public class SimulationCallbackControllerImpl extends SimulationCallbackController {

    @Inject
    public SimulationCallbackControllerImpl(SimulationCallback simulationCallback) {
        super(simulationCallback);
    }
}
