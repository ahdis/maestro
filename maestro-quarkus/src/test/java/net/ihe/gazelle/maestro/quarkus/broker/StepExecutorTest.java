/*
 * Copyright 2025 Kereval.
 */
package net.ihe.gazelle.maestro.quarkus.broker;

import net.ihe.gazelle.maestro.api.business.message.StartStepRun;
import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.engine.business.MaestroEventProducer;
import net.ihe.gazelle.maestro.engine.business.StepExecutorProvider;
import net.ihe.gazelle.maestro.engine.business.StepRun;
import net.ihe.gazelle.maestro.engine.business.StepRunner;
import net.ihe.gazelle.maestro.spi.business.runner.StepExecutor;
import net.ihe.gazelle.maestro.validation.step.business.ValidationStep;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StepExecutorTest {

    private static StepRun stepRunFor(Step step) {
        return new StepRun("ts", "tr", step);
    }

    @Test
    void launchStepExecution_happyPath_sendsReport() {
        StepExecutor runner = mock(
              StepExecutor.class);
        when(runner.execute(any(StepRun.class))).thenReturn(CompletableFuture.completedFuture(new StepRunReportBuilder().setStepId("s").setType("t")));
        StepExecutorProvider provider = mock(StepExecutorProvider.class);
        when(provider.getExecutor(any(Step.class))).thenReturn(runner);
        MaestroEventProducer producer = mock(MaestroEventProducer.class);

        StepRunner job = new StepRunner(provider, producer);
        Step step = new ValidationStep();
        step.setName("s");
        StartStepRun cmd = new StartStepRun(stepRunFor(step));
        job.run(cmd);

        verify(producer, timeout(500)).returnStepReport(any(StepRun.class), any(StepRunReportBuilder.class));
    }

    @Test
    void launchStepExecution_runnerCreationThrows_returnsUnexpectedError() {
        StepExecutorProvider provider = mock(StepExecutorProvider.class);
        MaestroEventProducer producer = mock(MaestroEventProducer.class);
        when(provider.getExecutor(any(Step.class))).thenThrow(new RuntimeException("err"));

        StepRunner job = new StepRunner(provider, producer);
        Step step = new ValidationStep();
        step.setName("s");
        job.run(new StartStepRun(stepRunFor(step)));

        verify(producer, timeout(500)).returnStepReport(any(StepRun.class), any(StepRunReportBuilder.class));
    }

    @Test
    void returnUnexpectedError_buildsUndefinedReport() {
        MaestroEventProducer producer = mock(MaestroEventProducer.class);
        StepRunner job = new StepRunner(mock(StepExecutorProvider.class), producer);
        Step step = new ValidationStep();
        step.setName("s");
        StepRun sr = stepRunFor(step);

        job.returnUnexpectedError(new RuntimeException("boom"), sr);

        verify(producer).returnStepReport(eq(sr), any(StepRunReportBuilder.class));
    }
}

