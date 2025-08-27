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

import net.ihe.gazelle.lang.ExecutionRuntimeException;
import net.ihe.gazelle.maestro.api.business.testreport.*;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRun;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRunner;
import net.ihe.gazelle.simulation.business.callback.Result;
import net.ihe.gazelle.simulation.business.callback.SimulationReport;
import net.ihe.gazelle.simulation.business.callback.SimulationReportValidator;
import net.ihe.gazelle.simulation.business.setup.*;
import net.ihe.gazelle.simulation.client.business.ServiceUnavailableException;
import net.ihe.gazelle.maestro.userinteraction.step.business.UserInteractionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;

public class SimulationStepRunner implements StepRunner {

    private static final Logger LOG = LoggerFactory.getLogger(SimulationStepRunner.class);
    public static final String SIMULATION_REPORT = "SIMULATION_REPORT";

    private final ReportSerialization serializer;
    private final SimulationHandler simulationHandler;
    private final UserInteractionHandler userInteractionHandler;

    public SimulationStepRunner(ReportSerialization serializer, SimulationHandler simulationHandler, UserInteractionHandler userInteractionHandler) {
        this.serializer = serializer;
        this.simulationHandler = simulationHandler;
        this.userInteractionHandler = userInteractionHandler;
    }

    @Override
    public CompletableFuture<StepRunReportBuilder> run(StepRun stepRun) {
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
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (ExecutionException e) {
            throw new ExecutionRuntimeException(e);
        }
    }

    private SimulationReport getSimulationReport(SimulationRequest simulationRequest, CompletableFuture<SimulationReport> futureReport) throws ExecutionException, InterruptedException {
        SimulationReport simulationReport = futureReport
                .orTimeout(simulationRequest.getTimeoutSeconds() + 10L, TimeUnit.SECONDS)
                .exceptionally(ex ->
                {
                    if (ex instanceof TimeoutException) {
                        LOG.error("Simulation has timed out after {} seconds.", simulationRequest.getTimeoutSeconds() + 10, ex);
                        return null;
                    }
                    throw new CompletionException(ex);
                })
                .get();
        if (simulationReport == null) {
            throw new TimeoutRuntimeException("Simulation has timed out after " + simulationRequest.getTimeoutSeconds() + 10 + " seconds.");
        }
        return simulationReport;
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
            instructions.append("name : ").append(parameter.getName()).append("\n")
                    .append("value : ").append(parameter.getValue());
        }
        userInteractionHandler.displayMessage(instructions.toString());
    }

    @Override
    public String getSupportedStep() {
        return SimulationStep.class.getCanonicalName();
    }
}
