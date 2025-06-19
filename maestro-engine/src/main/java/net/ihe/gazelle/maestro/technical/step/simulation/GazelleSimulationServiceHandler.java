package net.ihe.gazelle.maestro.technical.step.simulation;

import io.smallrye.common.annotation.Blocking;
import net.ihe.gazelle.maestro.business.step.simulation.SimulationHandler;
import net.ihe.gazelle.simulation.business.callback.SimulationReport;
import net.ihe.gazelle.simulation.business.sequence.SimulationSequence;
import net.ihe.gazelle.simulation.business.setup.AdditionalInstructions;
import net.ihe.gazelle.simulation.business.setup.SimulationRequest;
import net.ihe.gazelle.simulation.client.business.SimulationClient;

import java.util.List;
import java.util.function.Consumer;

public class GazelleSimulationServiceHandler implements SimulationHandler {

    private final SimulationClient simulationClient;

    public GazelleSimulationServiceHandler(SimulationClient simulationClient) {
        this.simulationClient = simulationClient;
    }

    @Override
    public boolean isAvailable() {
        try {
            return getSimulationSequences() != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<SimulationSequence> getSimulationSequences() {
        return simulationClient.getSimulationSequences();
    }

    @Override
    public void simulate(SimulationRequest simulationRequest, Consumer<AdditionalInstructions> instructionHandler, Consumer<SimulationReport> reportConsumer) {
        simulationClient.simulate(simulationRequest, instructionHandler, reportConsumer);
    }

    @Override
    public void notifySimulation(String sessionId, SimulationReport report) {
        simulationClient.notifySimulation(sessionId, report);
    }
}
