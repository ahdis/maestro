/*
 * Copyright 2025 Kereval.
 */
package net.ihe.gazelle.maestro.quarkus.jobs;

import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testrun.MaestroEventProducer;
import net.ihe.gazelle.maestro.api.business.testrun.Step;
import net.ihe.gazelle.maestro.api.business.testrun.runner.*;
import net.ihe.gazelle.maestro.validation.step.business.ValidationStep;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StepRunnerJobTest {

    private static StepRun stepRunFor(Step step) {
        return new StepRun("ts", "tr", step);
    }

    @Test
    void launchStepExecution_happyPath_sendsReport() {
        StepRunner runner = mock(StepRunner.class);
        when(runner.run(any(StepRun.class))).thenReturn(CompletableFuture.completedFuture(new StepRunReportBuilder().setStepId("s").setType("t")));
        StepRunnerProvider provider = mock(StepRunnerProvider.class);
        when(provider.create(any(Step.class))).thenReturn(runner);
        MaestroEventProducer producer = mock(MaestroEventProducer.class);

        StepRunnerJob job = new StepRunnerJob(provider, producer);
        Step step = new ValidationStep();
        step.setName("s");
        StartStepCommand cmd = new StartStepCommand(stepRunFor(step));
        job.launchStepExecution(cmd);

        verify(producer, timeout(500)).returnStepReport(any(StepRun.class), any(StepRunReportBuilder.class));
    }

    @Test
    void launchStepExecution_runnerCreationThrows_returnsUnexpectedError() {
        StepRunnerProvider provider = mock(StepRunnerProvider.class);
        MaestroEventProducer producer = mock(MaestroEventProducer.class);
        when(provider.create(any(Step.class))).thenThrow(new RuntimeException("err"));

        StepRunnerJob job = new StepRunnerJob(provider, producer);
        Step step = new ValidationStep();
        step.setName("s");
        job.launchStepExecution(new StartStepCommand(stepRunFor(step)));

        verify(producer, timeout(500)).returnStepReport(any(StepRun.class), any(StepRunReportBuilder.class));
    }

    @Test
    void returnUnexpectedError_buildsUndefinedReport() {
        MaestroEventProducer producer = mock(MaestroEventProducer.class);
        StepRunnerJob job = new StepRunnerJob(mock(StepRunnerProvider.class), producer);
        Step step = new ValidationStep();
        step.setName("s");
        StepRun sr = stepRunFor(step);

        job.returnUnexpectedError(new RuntimeException("boom"), sr);

        verify(producer).returnStepReport(eq(sr), any(StepRunReportBuilder.class));
    }
}

