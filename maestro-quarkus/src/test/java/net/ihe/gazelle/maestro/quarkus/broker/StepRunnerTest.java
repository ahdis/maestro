/*
 * Copyright 2025 Kereval.
 */
package net.ihe.gazelle.maestro.quarkus.broker;

import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.engine.business.MaestroEventProducer;
import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.business.message.StartStepRun;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRun;
import net.ihe.gazelle.maestro.engine.business.StepRunner;
import net.ihe.gazelle.maestro.engine.business.StepRunnerProvider;
import net.ihe.gazelle.maestro.validation.step.business.ValidationStep;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StepRunnerTest {

    private static StepRun stepRunFor(Step step) {
        return new StepRun("ts", "tr", step);
    }

    @Test
    void launchStepExecution_happyPath_sendsReport() {
        net.ihe.gazelle.maestro.api.business.testrun.runner.StepRunner runner = mock(
              net.ihe.gazelle.maestro.api.business.testrun.runner.StepRunner.class);
        when(runner.run(any(StepRun.class))).thenReturn(CompletableFuture.completedFuture(new StepRunReportBuilder().setStepId("s").setType("t")));
        StepRunnerProvider provider = mock(StepRunnerProvider.class);
        when(provider.create(any(Step.class))).thenReturn(runner);
        MaestroEventProducer producer = mock(MaestroEventProducer.class);

        StepRunner job = new StepRunner(provider, producer);
        Step step = new ValidationStep();
        step.setName("s");
        StartStepRun cmd = new StartStepRun(stepRunFor(step));
        job.execute(cmd);

        verify(producer, timeout(500)).returnStepReport(any(StepRun.class), any(StepRunReportBuilder.class));
    }

    @Test
    void launchStepExecution_runnerCreationThrows_returnsUnexpectedError() {
        StepRunnerProvider provider = mock(StepRunnerProvider.class);
        MaestroEventProducer producer = mock(MaestroEventProducer.class);
        when(provider.create(any(Step.class))).thenThrow(new RuntimeException("err"));

        StepRunner job = new StepRunner(provider, producer);
        Step step = new ValidationStep();
        step.setName("s");
        job.execute(new StartStepRun(stepRunFor(step)));

        verify(producer, timeout(500)).returnStepReport(any(StepRun.class), any(StepRunReportBuilder.class));
    }

    @Test
    void returnUnexpectedError_buildsUndefinedReport() {
        MaestroEventProducer producer = mock(MaestroEventProducer.class);
        StepRunner job = new StepRunner(mock(StepRunnerProvider.class), producer);
        Step step = new ValidationStep();
        step.setName("s");
        StepRun sr = stepRunFor(step);

        job.returnUnexpectedError(new RuntimeException("boom"), sr);

        verify(producer).returnStepReport(eq(sr), any(StepRunReportBuilder.class));
    }
}

