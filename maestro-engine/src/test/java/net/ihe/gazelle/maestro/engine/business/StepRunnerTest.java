package net.ihe.gazelle.maestro.engine.business;

import net.ihe.gazelle.maestro.api.business.message.StartStepRun;
import net.ihe.gazelle.maestro.api.business.message.StepRunFinished;
import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.business.testreport.StepResult;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.engine.business.mock.ScriptedStepExecutorProvider;
import net.ihe.gazelle.maestro.spi.business.StepRun;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class StepRunnerTest {

   private ScriptedStepExecutorProvider executorProvider;
   private RecordingStepRunEventProducer eventProducer;
   private StepRunner stepRunner;
   private Step step;

   @BeforeEach
   void setUp() {
      executorProvider = new ScriptedStepExecutorProvider();
      eventProducer = new RecordingStepRunEventProducer();
      stepRunner = new StepRunner(executorProvider, eventProducer);
      step = new Step().setId("step-1").setName("Step").setType("SIMULATION");
   }

   @Test
   void runDelegatesToExecutorAndEmitsReport() {
      StepRunReport report = new StepRunReport()
            .setStepId("step-1")
            .setStepName("Step")
            .setType("SIMULATION")
            .setResult(StepResult.PASSED);

      executorProvider.withExecutor("step-1", stepRun -> CompletableFuture.completedFuture(report));

      stepRunner.run(new StartStepRun("session", "test", new StepRun(step, step.getProperties())));

      StepRunFinished finished = eventProducer.awaitFinishedEvent(Duration.ofSeconds(2));
      assertThat(finished.getStepRunReport(), sameInstance(report));
   }

   @Test
   void runHonorsTimeoutProperty() {
      step.setProperties(List.of(new net.ihe.gazelle.maestro.api.business.property.IntegerProperty("timeout", 50)));
      executorProvider.withExecutor("step-1", stepRun -> new CompletableFuture<>());

      stepRunner.run(new StartStepRun("session-timeout", "test", new StepRun(step, step.getProperties())));

      StepRunFinished finished = eventProducer.awaitFinishedEvent(Duration.ofSeconds(2));
      assertThat(finished.getStepRunReport().getResult(), is(StepResult.UNDEFINED));
      assertThat(finished.getStepRunReport().getUnexpectedErrors(), is(not(empty())));
   }

   @Test
   void runHandlesUnknownStepException() {
      stepRunner.run(new StartStepRun("session-unknown", "test", new StepRun(step, step.getProperties())));

      StepRunFinished finished = eventProducer.awaitFinishedEvent(Duration.ofSeconds(2));
      assertThat(finished.getStepRunReport().getResult(), is(StepResult.UNDEFINED));
      assertThat(finished.getStepRunReport().getUnexpectedErrors(), is(not(empty())));
   }

   @Test
   void runPropagatesExecutorExceptionsAsUndefinedReports() {
      executorProvider.withExecutor("step-1", stepRun -> {
         CompletableFuture<StepRunReport> future = new CompletableFuture<>();
         future.completeExceptionally(new RuntimeException("boom"));
         return future;
      });

      stepRunner.run(new StartStepRun("session-exception", "test", new StepRun(step, step.getProperties())));

      StepRunFinished finished = eventProducer.awaitFinishedEvent(Duration.ofSeconds(2));
      assertThat(finished.getStepRunReport().getResult(), is(StepResult.UNDEFINED));
      assertThat(finished.getStepRunReport().getUnexpectedErrors(), is(not(empty())));
   }

   private static final class RecordingStepRunEventProducer implements StepRunEventProducer {
      private final BlockingQueue<StepRunFinished> queue = new LinkedBlockingQueue<>();

      @Override
      public void notify(StepRunFinished stepRunFinished) {
         queue.add(stepRunFinished);
      }


      StepRunFinished awaitFinishedEvent(Duration timeout) {
         try {
            StepRunFinished event = queue.poll(timeout.toMillis(), TimeUnit.MILLISECONDS);
            if (event == null) {
               throw new AssertionError("No StepRunFinished received within " + timeout);
            }
            return event;
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AssertionError("Interrupted while waiting for StepRunFinished", e);
         }
      }
   }

}
