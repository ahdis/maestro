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

package net.ihe.gazelle.maestro.simulation.step.business;

import net.ihe.gazelle.errorhandling.business.UnexpectedErrorBuilder;
import net.ihe.gazelle.lang.ExecutionRuntimeException;
import net.ihe.gazelle.maestro.api.business.testreport.Output;
import net.ihe.gazelle.maestro.api.business.testreport.OutputBuilder;
import net.ihe.gazelle.maestro.api.business.testreport.StepResult;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.spi.business.runner.StepExecutor;
import net.ihe.gazelle.maestro.spi.business.runner.StepRun;
import net.ihe.gazelle.maestro.userinteraction.step.business.UserInteractionHandler;
import net.ihe.gazelle.simulation.business.callback.Result;
import net.ihe.gazelle.simulation.business.callback.SimulationReport;
import net.ihe.gazelle.simulation.business.callback.SimulationReportValidator;
import net.ihe.gazelle.simulation.business.sequence.ServiceUnavailableException;
import net.ihe.gazelle.simulation.business.setup.AdditionalInstructions;
import net.ihe.gazelle.simulation.business.setup.AlreadyRunningException;
import net.ihe.gazelle.simulation.business.setup.InvalidParameterValueException;
import net.ihe.gazelle.simulation.business.setup.MissingRequiredParameterException;
import net.ihe.gazelle.simulation.business.setup.Parameter;
import net.ihe.gazelle.simulation.business.setup.SimulationRequest;
import net.ihe.gazelle.simulation.business.setup.UnknownSequenceException;
import net.ihe.gazelle.simulation.business.setup.UnknownSimulationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SimulationStepExecutor implements StepExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(SimulationStepExecutor.class);
    public static final String SIMULATION_REPORT = "SIMULATION_REPORT";

    private final ReportSerialization serializer;
    private final SimulationHandler simulationHandler;
    private final UserInteractionHandler userInteractionHandler;

    public SimulationStepExecutor(ReportSerialization serializer, SimulationHandler simulationHandler, UserInteractionHandler userInteractionHandler) {
        this.serializer = serializer;
        this.simulationHandler = simulationHandler;
        this.userInteractionHandler = userInteractionHandler;
    }

    @Override
    public CompletableFuture<StepRunReportBuilder> execute(StepRun stepRun) {
        try {
            SimulationRequest simulationRequest = SimulationRequestMapper
                    .createSimulationRequest((SimulationStep) stepRun.step());
            CompletableFuture<SimulationReport> futureReport = new CompletableFuture<>();
            simulationHandler.simulate(simulationRequest, this::showInstructions, futureReport::complete);
            SimulationReport simulationReport = getSimulationReport(simulationRequest, futureReport);
            OutputBuilder outputBuilder = new OutputBuilder()
                    .setContent(serializer.toByteArray(simulationReport))
                    .setName(SIMULATION_REPORT)
                    .setFormat(Output.FORMAT_JSON)
                    ;
            StepRunReportBuilder stepRunReportBuilder = prepareStepReport(stepRun)
                    .setOutputs(List.of(outputBuilder));
            computeResult(simulationReport, stepRunReportBuilder);
            return CompletableFuture.completedFuture(stepRunReportBuilder);
        } catch (MissingRequiredParameterException e) {
            return buildReportWithError(stepRun, e, "Missing Required Parameter");
        } catch (InvalidParameterValueException e) {
            return buildReportWithError(stepRun, e, "Invalid Parameter Value");
        } catch (UnknownSequenceException e) {
            return buildReportWithError(stepRun, e, "Unknown Simulation Sequence");
        } catch (UnknownSimulationException e) {
            return buildReportWithError(stepRun, e, "Unknown Simulation session");
        } catch (AlreadyRunningException e) {
            return buildReportWithError(stepRun, e, "Simulation already running");
        } catch (ServiceUnavailableException e) {
            return buildReportWithError(stepRun, e, "Service Unavailable");
        } catch (ClassCastException e) {
            return buildReportWithError(stepRun, e, "Error on property type");
        } catch (RuntimeException e) {
            return buildReportWithError(stepRun, e, "Error from simulator");
        }
    }

    private SimulationReport getSimulationReport(SimulationRequest simulationRequest, CompletableFuture<SimulationReport> futureReport) {
        long timeout = simulationRequest.getTimeoutSeconds() + 10L;
        try {
            return futureReport.get(timeout, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            LOG.error("Simulation timed out after {} seconds", timeout);
            throw new TimeoutRuntimeException("Simulation timed out after " + timeout + " seconds");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (ExecutionException e) {
            throw new ExecutionRuntimeException(e);
        }
    }

    private StepRunReportBuilder prepareStepReport(StepRun stepRun) {
        return new StepRunReportBuilder()
                .setStepId(stepRun.step().getName())
                .setType(SimulationStep.TYPE);
    }

    private CompletableFuture<StepRunReportBuilder> buildReportWithError(StepRun stepRun, Exception e, String name) {
        return CompletableFuture.completedFuture(prepareStepReport(stepRun)
                .addUnexpectedError(
                        new UnexpectedErrorBuilder()
                                .setName(name)
                                .setMessage(name)
                                .setCause(new UnexpectedErrorBuilder().fromThrowable(e))
                ));
    }

    /**
     * Compute the simulation step result from the received simulation report from external simulation tool
     * @param simulationReport The received simulation report
     * @param stepRunReportBuilder An object to build the Step run report
     */
    private void computeResult(SimulationReport simulationReport, StepRunReportBuilder stepRunReportBuilder) {
        SimulationReportValidator validator = new SimulationReportValidator();
        validator.validate(simulationReport).getAllInvalidRules().forEach(
                ruleResult -> stepRunReportBuilder.addUnexpectedError(
                        new UnexpectedErrorBuilder()
                                .setName(ruleResult.getId())
                                .setMessage(ruleResult.getDescription())
                )
        );
        if (Result.PASSED.equals(simulationReport.getResult())) {
            stepRunReportBuilder.setResult(StepResult.PASSED);
        } else if (Result.FAILED.equals(simulationReport.getResult())) {
            stepRunReportBuilder.setResult(StepResult.FAILED);
        } else {
            stepRunReportBuilder.setResult(StepResult.UNDEFINED);
        }
    }

    private void showInstructions(AdditionalInstructions additionalInstructions) {
        StringBuilder instructions = new StringBuilder()
                .append(additionalInstructions.getInstruction())
                .append("\n");
        for (Parameter parameter : additionalInstructions.getParameters()) {
            instructions
                    .append("name : ").append(parameter.getName()).append("\n")
                    .append("value : ").append(parameter.getValue()).append("\n");
        }
        userInteractionHandler.displayMessage(instructions.toString());
    }

    @Override
    public String getSupportedStep() {
        return SimulationStep.class.getCanonicalName();
    }
}
