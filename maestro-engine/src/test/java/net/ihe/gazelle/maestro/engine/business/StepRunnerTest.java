/*
 * Copyright 2026 IHE International.
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
import java.time.Instant;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

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
      step = new Step().setName("Step 1").setType("SIMULATION");
   }

   @Test
   void runDelegatesToExecutorAndEmitsReport() {
      StepRunReport report = new StepRunReport()
            .setStepName("Step 1")
            .setType("SIMULATION")
            .setResult(StepResult.PASSED);

      executorProvider.withExecutor("Step 1", stepRun -> {
         assertThat(stepRun.getTimeout(), allOf(greaterThan(0L), lessThanOrEqualTo(60_000L)));
         return report;
      });

      stepRunner.run(new StartStepRun(
              "123456", "abc123", 4,
              new StepRun(step, step.getProperties()))
              .setExpirationTimestamp(Instant.now().plusSeconds(60)));

      StepRunFinished finished = eventProducer.awaitFinishedEvent(Duration.ofSeconds(2));
      assertEquals("Step 1", finished.getStepName());
      assertEquals("123456", finished.getSessionId());
      assertEquals("abc123", finished.getTestId());
      assertThat(finished.getStepRunReport(), sameInstance(report));
   }

   @Test
   void runHonorsTimeoutProperty() {
      step.setTimeout(50L);
      executorProvider.withExecutor("Step 1", stepRun -> {
         assertEquals(50L, stepRun.getTimeout());
         return new StepRunReport().setStepName("Step 1").setType("SIMULATION").setResult(StepResult.PASSED);
      });

      stepRunner.run(new StartStepRun("session-timeout", "test", 0, new StepRun(step, step.getProperties())));

      StepRunFinished finished = eventProducer.awaitFinishedEvent(Duration.ofSeconds(2));
      assertEquals(StepResult.PASSED, finished.getStepRunReport().getResult());
   }

   @Test
   void runSkipsExecutionWhenSessionHasExpired() {
      executorProvider.withExecutor("Step 1", stepRun -> fail("Expired steps must not execute"));

      stepRunner.run(new StartStepRun("expired-session", "test", 0, new StepRun(step, step.getProperties()))
              .setExpirationTimestamp(Instant.now().minusSeconds(1)));

      StepRunFinished finished = eventProducer.awaitFinishedEvent(Duration.ofSeconds(2));
      assertEquals(StepResult.UNDEFINED, finished.getStepRunReport().getResult());
      assertThat(finished.getStepRunReport().getUnexpectedErrors(), hasSize(1));
   }

   @Test
   void runHandlesUnknownStepException() {
      stepRunner.run(new StartStepRun("session-unknown", "test", 0, new StepRun(step, step.getProperties())));

      StepRunFinished finished = eventProducer.awaitFinishedEvent(Duration.ofSeconds(2));
      assertThat(finished.getStepRunReport().getResult(), is(StepResult.UNDEFINED));
      assertThat(finished.getStepRunReport().getUnexpectedErrors(), is(not(empty())));
   }

   @Test
   void runPropagatesExecutorExceptionsAsUndefinedReports() {
      executorProvider.withExecutor("Step 1", stepRun -> {
         throw new RuntimeException("boom");
      });

      stepRunner.run(new StartStepRun("session-exception", "test", 0, new StepRun(step, step.getProperties())));

      StepRunFinished finished = eventProducer.awaitFinishedEvent(Duration.ofSeconds(2));
      assertThat(finished.getStepRunReport().getResult(), is(StepResult.UNDEFINED));
      assertEquals("Step 1", finished.getStepName());
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
