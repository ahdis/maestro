/*
 * Copyright 2025 Kereval.
 */
package net.ihe.gazelle.maestro.simulation.step.business;

import net.ihe.gazelle.maestro.api.business.testreport.Output;
import net.ihe.gazelle.maestro.api.business.testreport.StepResult;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRun;
import net.ihe.gazelle.maestro.userinteraction.step.business.UserInteractionHandler;
import net.ihe.gazelle.simulation.business.callback.Result;
import net.ihe.gazelle.simulation.business.callback.SimulationReport;
import net.ihe.gazelle.simulation.business.sequence.ServiceUnavailableException;
import net.ihe.gazelle.simulation.business.setup.AdditionalInstructions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SimulationStepRunnerTest {

    private SimulationStep step() {
        return new SimulationStep(
                "sim-step",
                new Property<>(SimulationStep.SIMULATION_SERVICE, "svc"),
                new Property<>(SimulationStep.SEQUENCE_ID, "SEQ-1"),
                new Property<>(SimulationStep.TIMEOUT_SECONDS, 1),
                List.of(
                        new Property<>("p-text", "v"),
                        new Property<>("p-bool", true),
                        new Property<>("p-file", "data".getBytes())
                )
        );
    }

    private StepRun stepRun() { return new StepRun("ts", "tr", step()); }

    @Test
    void run_returnsPassed_and_output_and_showsInstructions() {
        ReportSerialization serializer = mock(ReportSerialization.class);
        when(serializer.toByteArray(any())).thenReturn("{}".getBytes());
        SimulationHandler simHandler = mock(SimulationHandler.class);
        UserInteractionHandler ui = mock(UserInteractionHandler.class);

        doAnswer(inv -> {
            Consumer<AdditionalInstructions> instr = inv.getArgument(1);
            Consumer<SimulationReport> reportC = inv.getArgument(2);
            AdditionalInstructions ai = mock(AdditionalInstructions.class);
            when(ai.getInstruction()).thenReturn("Do this");
            when(ai.getParameters()).thenReturn(List.of());
            instr.accept(ai);
            SimulationReport report = mock(SimulationReport.class);
            when(report.getResult()).thenReturn(Result.PASSED);
            reportC.accept(report);
            return null;
        }).when(simHandler).simulate(any(), any(), any());

        SimulationStepRunner runner = new SimulationStepRunner(serializer, simHandler, ui);

        StepRunReportBuilder builder = runner.run(stepRun()).join();
        StepRunReport report = builder.build();

        assertNotNull(report.getResult());
        assertEquals("sim-step", report.getStepId());
        assertEquals(SimulationStep.TYPE, report.getType());
        assertFalse(report.getOutputs().isEmpty());
        assertEquals(SimulationStepRunner.SIMULATION_REPORT, report.getOutputs().getFirst().getName());
        assertEquals(Output.FORMAT_JSON, report.getOutputs().getFirst().getFormat());
        verify(ui).displayMessage(anyString());
    }

    @Test
    void run_returnsFailed_whenSimulationReportFailed() {
        ReportSerialization serializer = mock(ReportSerialization.class);
        when(serializer.toByteArray(any())).thenReturn("{}".getBytes());
        SimulationHandler simHandler = mock(SimulationHandler.class);
        UserInteractionHandler ui = mock(UserInteractionHandler.class);

        doAnswer(inv -> {
            Consumer<SimulationReport> reportC = inv.getArgument(2);
            SimulationReport report = mock(SimulationReport.class);
            when(report.getResult()).thenReturn(Result.FAILED);
            reportC.accept(report);
            return null;
        }).when(simHandler).simulate(any(), any(), any());

        SimulationStepRunner runner = new SimulationStepRunner(serializer, simHandler, ui);

        StepRunReport report = runner.run(stepRun()).join().build();
        assertNotNull(report.getResult());
    }

    @Test
    void run_returnsUnexpectedError_onServiceUnavailable() {
        ReportSerialization serializer = mock(ReportSerialization.class);
        when(serializer.toByteArray(any())).thenReturn("{}".getBytes());
        SimulationHandler simHandler = mock(SimulationHandler.class);
        UserInteractionHandler ui = mock(UserInteractionHandler.class);

        doThrow(new ServiceUnavailableException("down")).when(simHandler).simulate(any(), any(), any());

        SimulationStepRunner runner = new SimulationStepRunner(serializer, simHandler, ui);
        StepRunReport report = runner.run(stepRun()).join().build();
        assertEquals(StepResult.UNDEFINED, report.getResult());
        assertFalse(report.getUnexpectedErrors().isEmpty());
        assertEquals("Service Unavailable", report.getUnexpectedErrors().getFirst().getName());
    }

    @Test
    void getSupportedStep_returnsCanonicalName() {
        SimulationStepRunner runner = new SimulationStepRunner(mock(ReportSerialization.class), mock(SimulationHandler.class), mock(UserInteractionHandler.class));
        assertEquals(SimulationStep.class.getCanonicalName(), runner.getSupportedStep());
    }
}
