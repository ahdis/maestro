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

package net.ihe.gazelle.maestro.simulation.step.technical.handler;

import net.ihe.gazelle.maestro.simulation.step.business.SimulationHandler;
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
}
