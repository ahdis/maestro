/*
 * Copyright 2025 Kereval.
 */
package net.ihe.gazelle.maestro.simulation.step.business;

import net.ihe.gazelle.maestro.api.business.testreport.Output;
import net.ihe.gazelle.maestro.api.business.testreport.StepResult;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.spi.business.runner.StepRun;
import net.ihe.gazelle.maestro.userinteraction.step.business.UserInteractionHandler;
import net.ihe.gazelle.simulation.business.callback.Result;
import net.ihe.gazelle.simulation.business.callback.SimulationReport;
import net.ihe.gazelle.simulation.business.sequence.ServiceUnavailableException;
import net.ihe.gazelle.simulation.business.setup.AdditionalInstructions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SimulationStepExecutorTest {

    private SimulationStep step() {
        return new SimulationStep(
                "sim-step",
                new Property<>(SimulationStep.SIMULATION_SERVICE, "svc"),
                new Property<>(SimulationStep.SEQUENCE_ID, "SEQ-1"),
                new Property<>(SimulationStep.TIMEOUT_SECONDS, 1),
                List.of(
                        new Property<>("p-text", "v"),
                        new Property<>("p-bool", true),
                        new Property<>("p-file", "data".getBytes(StandardCharsets.UTF_8))
                )
        );
    }

    private StepRun stepRun() { return new StepRun("ts", "tr", step()); }

    @Test
    void run_returnsPassed_and_output_and_showsInstructions() {
        ReportSerialization serializer = mock(ReportSerialization.class);
        when(serializer.toByteArray(any())).thenReturn("{}".getBytes(StandardCharsets.UTF_8));
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

        SimulationStepExecutor runner = new SimulationStepExecutor(serializer, simHandler, ui);

        StepRunReportBuilder builder = runner.execute(stepRun()).join();
        StepRunReport report = builder.build();

        assertNotNull(report.getResult());
        assertEquals("sim-step", report.getStepId());
        assertEquals(SimulationStep.TYPE, report.getType());
        assertFalse(report.getOutputs().isEmpty());
        assertEquals(SimulationStepExecutor.SIMULATION_REPORT, report.getOutputs().getFirst().getName());
        assertEquals(Output.FORMAT_JSON, report.getOutputs().getFirst().getFormat());
        verify(ui).displayMessage(anyString());
    }

    @Test
    void run_returnsFailed_whenSimulationReportFailed() {
        ReportSerialization serializer = mock(ReportSerialization.class);
        when(serializer.toByteArray(any())).thenReturn("{}".getBytes(StandardCharsets.UTF_8));
        SimulationHandler simHandler = mock(SimulationHandler.class);
        UserInteractionHandler ui = mock(UserInteractionHandler.class);

        doAnswer(inv -> {
            Consumer<SimulationReport> reportC = inv.getArgument(2);
            SimulationReport report = mock(SimulationReport.class);
            when(report.getResult()).thenReturn(Result.FAILED);
            reportC.accept(report);
            return null;
        }).when(simHandler).simulate(any(), any(), any());

        SimulationStepExecutor runner = new SimulationStepExecutor(serializer, simHandler, ui);

        StepRunReport report = runner.execute(stepRun()).join().build();
        assertNotNull(report.getResult());
    }

    @Test
    void run_returnsUnexpectedError_onServiceUnavailable() {
        ReportSerialization serializer = mock(ReportSerialization.class);
        when(serializer.toByteArray(any())).thenReturn("{}".getBytes(StandardCharsets.UTF_8));
        SimulationHandler simHandler = mock(SimulationHandler.class);
        UserInteractionHandler ui = mock(UserInteractionHandler.class);

        doThrow(new ServiceUnavailableException("down")).when(simHandler).simulate(any(), any(), any());

        SimulationStepExecutor runner = new SimulationStepExecutor(serializer, simHandler, ui);
        StepRunReport report = runner.execute(stepRun()).join().build();
        assertEquals(StepResult.UNDEFINED, report.getResult());
        assertFalse(report.getUnexpectedErrors().isEmpty());
        assertEquals("Service Unavailable", report.getUnexpectedErrors().getFirst().getName());
    }

    @Test
    void getSupportedStep_returnsCanonicalName() {
        SimulationStepExecutor runner = new SimulationStepExecutor(mock(ReportSerialization.class), mock(SimulationHandler.class), mock(UserInteractionHandler.class));
        assertEquals(SimulationStep.class.getCanonicalName(), runner.getSupportedStep());
    }

    @Test
    void run_returnsUnexpectedError_onMissingRequiredParameter() {
        SimulationHandler simHandler = mock(SimulationHandler.class);
        doThrow(new net.ihe.gazelle.simulation.business.setup.MissingRequiredParameterException("missing"))
                .when(simHandler).simulate(any(), any(), any());
        SimulationStepExecutor runner = new SimulationStepExecutor(mock(ReportSerialization.class), simHandler, mock(UserInteractionHandler.class));
        StepRunReport report = runner.execute(stepRun()).join().build();
        assertEquals(StepResult.UNDEFINED, report.getResult());
        assertEquals("Missing Required Parameter", report.getUnexpectedErrors().getFirst().getName());
    }

    @Test
    void run_returnsUnexpectedError_onInvalidParameterValue() {
        SimulationHandler simHandler = mock(SimulationHandler.class);
        doThrow(new net.ihe.gazelle.simulation.business.setup.InvalidParameterValueException("invalid"))
                .when(simHandler).simulate(any(), any(), any());
        SimulationStepExecutor runner = new SimulationStepExecutor(mock(ReportSerialization.class), simHandler, mock(UserInteractionHandler.class));
        StepRunReport report = runner.execute(stepRun()).join().build();
        assertEquals("Invalid Parameter Value", report.getUnexpectedErrors().getFirst().getName());
    }

    @Test
    void run_returnsUnexpectedError_onUnknownSequence() {
        SimulationHandler simHandler = mock(SimulationHandler.class);
        doThrow(new net.ihe.gazelle.simulation.business.setup.UnknownSequenceException("unknown seq"))
                .when(simHandler).simulate(any(), any(), any());
        SimulationStepExecutor runner = new SimulationStepExecutor(mock(ReportSerialization.class), simHandler, mock(UserInteractionHandler.class));
        StepRunReport report = runner.execute(stepRun()).join().build();
        assertEquals("Unknown Simulation Sequence", report.getUnexpectedErrors().getFirst().getName());
    }

    @Test
    void run_returnsUnexpectedError_onUnknownSimulation() {
        SimulationHandler simHandler = mock(SimulationHandler.class);
        doThrow(new net.ihe.gazelle.simulation.business.setup.UnknownSimulationException("unknown sim"))
                .when(simHandler).simulate(any(), any(), any());
        SimulationStepExecutor runner = new SimulationStepExecutor(mock(ReportSerialization.class), simHandler, mock(UserInteractionHandler.class));
        StepRunReport report = runner.execute(stepRun()).join().build();
        assertEquals("Unknown Simulation session", report.getUnexpectedErrors().getFirst().getName());
    }

    @Test
    void run_returnsUnexpectedError_onAlreadyRunning() {
        SimulationHandler simHandler = mock(SimulationHandler.class);
        doThrow(new net.ihe.gazelle.simulation.business.setup.AlreadyRunningException("running"))
                .when(simHandler).simulate(any(), any(), any());
        SimulationStepExecutor runner = new SimulationStepExecutor(mock(ReportSerialization.class), simHandler, mock(UserInteractionHandler.class));
        StepRunReport report = runner.execute(stepRun()).join().build();
        assertEquals("Simulation already running", report.getUnexpectedErrors().getFirst().getName());
    }

    @Test
    void run_returnsUnexpectedError_onRuntimeException() {
        SimulationHandler simHandler = mock(SimulationHandler.class);
        doThrow(new RuntimeException("oops"))
                .when(simHandler).simulate(any(), any(), any());
        SimulationStepExecutor runner = new SimulationStepExecutor(mock(ReportSerialization.class), simHandler, mock(UserInteractionHandler.class));
        StepRunReport report = runner.execute(stepRun()).join().build();
        assertEquals("Error from simulator", report.getUnexpectedErrors().getFirst().getName());
    }

    @Test
    void run_returnsUnexpectedError_onClassCastErrorFromProperties() {
        // Prepare a step with wrong timeout type to trigger ClassCastException in setProperties
        SimulationStep s = step();
        // Set TIMEOUT_SECONDS as String property instead of Integer
        s.setProperties(List.of(new Property<>(SimulationStep.TIMEOUT_SECONDS, "bad")));
        StepRun sr = new StepRun("ts", "tr", s);
        SimulationStepExecutor runner = new SimulationStepExecutor(mock(ReportSerialization.class), mock(SimulationHandler.class), mock(UserInteractionHandler.class));
        StepRunReport report = runner.execute(sr).join().build();
        assertEquals("Error on property type", report.getUnexpectedErrors().getFirst().getName());
    }

    @Test
    void run_returnsUnexpectedError_onTimeout() {
        // Use negative timeout to compute zero total timeout, so get() times out immediately
        SimulationStep s = new SimulationStep(
                "sim-step",
                new Property<>(SimulationStep.SIMULATION_SERVICE, "svc"),
                new Property<>(SimulationStep.SEQUENCE_ID, "SEQ-1"),
                new Property<>(SimulationStep.TIMEOUT_SECONDS, -10),
                List.of()
        );
        StepRun sr = new StepRun("ts", "tr", s);
        SimulationHandler simHandler = mock(SimulationHandler.class);
        // Do nothing: don't complete the report consumer -> future.get times out immediately
        doAnswer(inv -> null).when(simHandler).simulate(any(), any(), any());
        SimulationStepExecutor runner = new SimulationStepExecutor(mock(ReportSerialization.class), simHandler, mock(UserInteractionHandler.class));
        StepRunReport report = runner.execute(sr).join().build();
        assertEquals("Error from simulator", report.getUnexpectedErrors().getFirst().getName());
    }
}
