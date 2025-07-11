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

package net.ihe.gazelle.maestro.technical.handler;

import net.ihe.gazelle.maestro.simulation.step.technical.handler.GazelleSimulationServiceHandler;
import net.ihe.gazelle.simulation.business.callback.*;
import net.ihe.gazelle.simulation.business.sequence.SimulationSequence;
import net.ihe.gazelle.simulation.business.setup.AdditionalInstructions;
import net.ihe.gazelle.simulation.business.setup.ParameterBuilder;
import net.ihe.gazelle.simulation.business.setup.ParameterType;
import net.ihe.gazelle.simulation.business.setup.SimulationRequest;
import net.ihe.gazelle.simulation.client.business.SimulationClient;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

public class SimulationHandlerMock extends GazelleSimulationServiceHandler {

    public SimulationHandlerMock(SimulationClient simulationClient) {
        super(simulationClient);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public List<SimulationSequence> getSimulationSequences() {
        return List.of();
    }

    @Override
    public void simulate(SimulationRequest simulationRequest, Consumer<AdditionalInstructions> instructionHandler, Consumer<SimulationReport> reportConsumer) {
        SimulationReport report = new SimulationReportBuilder()
                .setDateTime(new Date())
                .setSequenceId("Simple_CHXDS_ITI-41-42")
                .setServiceName("Mock_XDS_Simulator")
                .setServiceVersion("1.0.0")
                .addTransaction(new TransactionReportBuilder()
                        .setResult(Result.PASSED)
                        .setTransaction("ITI-41")
                        .addStandard("ebRIM")
                        .addMessage(new MessageBuilder()
                                .setName("ITI-41 request")
                                .setDateTime(new Date())
                                .setSender("XDS Document Repository")
                                .setReceiver("Document Registry")
                                .setReference("ref"))
                        .setInitiator(new RoleBuilder()
                                .setSimulated(true)
                                .setName("XDS Document Repository")
                                .addConfig(new ParameterBuilder()
                                        .setName("IP")
                                        .setValue("91.121.54.71")
                                        .setType(ParameterType.TEXT)))
                        .setResponder(new RoleBuilder()
                                .setSimulated(false)
                                .setName("Document Registry"))
                        .addValidationReport(new ValidationReportBuilder()
                                .setValidationResult(ValidationResult.PASSED)
                                .setValidationProfileId("ITI-41")
                                .setReference("ref")))
                .build();
        report.computeResult();
        reportConsumer.accept(report);
    }
}
