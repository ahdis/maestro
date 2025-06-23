package net.ihe.gazelle.maestro.technical.step.simulation;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import net.ihe.gazelle.simulation.business.callback.SimulationCallbackService;
import net.ihe.gazelle.simulation.callback.jaxrs.server.technical.SimulationCallbackController;


@Path("/")
public class SimulationCallbackControllerImpl extends SimulationCallbackController {

    @Inject
    public SimulationCallbackControllerImpl(SimulationCallbackService simulationCallback) {
        super(simulationCallback);
    }
}
