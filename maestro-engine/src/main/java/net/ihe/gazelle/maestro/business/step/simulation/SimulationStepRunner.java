package net.ihe.gazelle.maestro.business.step.simulation;

import net.ihe.gazelle.lang.ExecutionRuntimeException;
import net.ihe.gazelle.maestro.api.business.testreport.ItemBuilder;
import net.ihe.gazelle.maestro.api.business.testreport.StepResult;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testreport.UnexpectedErrorBuilder;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRun;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRunner;
import net.ihe.gazelle.maestro.business.step.spi.ReportSerialization;
import net.ihe.gazelle.maestro.business.step.userinteract.UserInteractionHandler;
import net.ihe.gazelle.simulation.business.callback.Result;
import net.ihe.gazelle.simulation.business.callback.SimulationReport;
import net.ihe.gazelle.simulation.business.callback.SimulationReportValidator;
import net.ihe.gazelle.simulation.business.setup.AdditionalInstructions;
import net.ihe.gazelle.simulation.business.setup.Parameter;
import net.ihe.gazelle.simulation.business.setup.SimulationRequest;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SimulationStepRunner implements StepRunner {

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
        SimulationRequest simulationRequest = SimulationRequestMapper.
                createSimulationRequest((SimulationStep) stepRun.step());
        CompletableFuture<SimulationReport> futureReport = new CompletableFuture<>();
        simulationHandler.simulate(simulationRequest, this::showInstructions, futureReport::complete);
        try {
            SimulationReport simulationReport = futureReport.get();
            ItemBuilder itemBuilder = new ItemBuilder()
                    .setContent(serializer.simulationReportToByteArray(simulationReport))
                    .setName("simulationReport");
            StepRunReportBuilder stepRunReportBuilder = new StepRunReportBuilder()
                    .setOutputs(List.of(itemBuilder))
                    .setStepId(stepRun.step().getName())
                    .setType(SimulationStep.TYPE);
            computeResult(simulationReport, stepRunReportBuilder);
            return stepRunReportBuilder;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (ExecutionException e) {
            throw new ExecutionRuntimeException(e);
        }
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
