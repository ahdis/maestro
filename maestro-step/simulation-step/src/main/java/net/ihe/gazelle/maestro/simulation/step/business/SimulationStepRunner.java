/*
 * Copyright 2025 IHE International.
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
import net.ihe.gazelle.maestro.api.business.testreport.ItemBuilder;
import net.ihe.gazelle.maestro.api.business.testreport.StepResult;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testreport.UnexpectedErrorBuilder;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRun;
import net.ihe.gazelle.maestro.api.business.testrun.runner.SyncStepRunner;
import net.ihe.gazelle.simulation.business.callback.Result;
import net.ihe.gazelle.simulation.business.callback.SimulationReport;
import net.ihe.gazelle.simulation.business.callback.SimulationReportValidator;
import net.ihe.gazelle.simulation.business.setup.*;
import net.ihe.gazelle.simulation.client.business.ServiceUnavailableException;
import net.ihe.gazelle.userinteraction.step.business.UserInteractionHandler;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SimulationStepRunner extends SyncStepRunner {

    private final ReportSerialization serializer;
    private final SimulationHandler simulationHandler;
    private final UserInteractionHandler userInteractionHandler;

    public SimulationStepRunner(ReportSerialization serializer, SimulationHandler simulationHandler, UserInteractionHandler userInteractionHandler) {
        this.serializer = serializer;
        this.simulationHandler = simulationHandler;
        this.userInteractionHandler = userInteractionHandler;
    }

    @Override
    public StepRunReportBuilder run(StepRun stepRun) {
        try {
            SimulationRequest simulationRequest = SimulationRequestMapper
                    .createSimulationRequest((SimulationStep) stepRun.step());
            CompletableFuture<SimulationReport> futureReport = new CompletableFuture<>();
            simulationHandler.simulate(simulationRequest, this::showInstructions, futureReport::complete);
            SimulationReport simulationReport = futureReport.get();
            ItemBuilder itemBuilder = new ItemBuilder()
                    .setContent(serializer.toByteArray(simulationReport))
                    .setName("simulationReport");
            StepRunReportBuilder stepRunReportBuilder = prepareStepReport(stepRun)
                    .setOutputs(List.of(itemBuilder));
            computeResult(simulationReport, stepRunReportBuilder);
            return stepRunReportBuilder;
        } catch (MissingRequiredParameterException e) {
            return buildReportWithError(stepRun, e, "Missing Required Parameter");
        } catch (InvalidParameterValueException e) {
            return buildReportWithError(stepRun, e, "Invalid Parameter Value");
        } catch (UnknownSequenceException e) {
            return buildReportWithError(stepRun, e, "Unknown Simulation Sequence");
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

    private StepRunReportBuilder prepareStepReport(StepRun stepRun) {
        return new StepRunReportBuilder()
                .setStepId(stepRun.step().getName())
                .setType(SimulationStep.TYPE);
    }

    private StepRunReportBuilder buildReportWithError(StepRun stepRun, Exception e, String name) {
        return prepareStepReport(stepRun)
                .addUnexpectedError(
                        new UnexpectedErrorBuilder()
                                .setName(name)
                                .setMessage(e.getMessage())
                );
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
