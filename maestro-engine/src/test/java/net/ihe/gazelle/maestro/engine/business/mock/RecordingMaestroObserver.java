package net.ihe.gazelle.maestro.engine.business.mock;

import net.ihe.gazelle.maestro.api.business.MaestroObserver;
import net.ihe.gazelle.maestro.api.business.message.*;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class RecordingMaestroObserver implements MaestroObserver {

   private final List<TestRunStarted> testRunStartedEvents = new CopyOnWriteArrayList<>();
   private final List<StepRunStarted> stepRunStartedEvents = new CopyOnWriteArrayList<>();
   private final List<StepRunFinished> stepRunFinishedEvents = new CopyOnWriteArrayList<>();
   private final List<TestRunFinished> testRunFinishedEvents = new CopyOnWriteArrayList<>();
   private final CompletableFuture<ExecutionFinished> future = new CompletableFuture<>();

   @Override
   public void onTestRunStarted(TestRunStarted testRunStarted) {
      testRunStartedEvents.add(testRunStarted);
   }

   @Override
   public void onStepRunStarted(StepRunStarted stepRunStarted) {
      stepRunStartedEvents.add(stepRunStarted);
   }

   @Override
   public CompletableFuture<UserInteractionCompleted> interactWithUser(InteractWithUser interactWithUser) {
      return CompletableFuture.completedFuture(new UserInteractionCompleted("observer"));
   }

   @Override
   public void onStepRunFinished(StepRunFinished stepRunFinished) {
      stepRunFinishedEvents.add(stepRunFinished);
   }

   @Override
   public void onTestRunFinished(TestRunFinished testRunFinished) {
      testRunFinishedEvents.add(testRunFinished);
   }

    @Override
    public void onExecutionFinished(ExecutionFinished executionFinished) {
        future.complete(executionFinished);
    }

   public ExecutionFinished awaitSuiteFinished(Duration timeout) {
      try {
         return future.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
      } catch (Exception e) {
         throw new AssertionError("Test suite did not finish in time", e);
      }
   }

   public List<StepRunFinished> getStepRunFinishedEvents() {
      return stepRunFinishedEvents;
   }

   public List<TestRunFinished> getTestRunFinishedEvents() {
      return testRunFinishedEvents;
   }

   public List<TestRunStarted> getTestRunStartedEvents() {
      return testRunStartedEvents;
   }

   public List<StepRunStarted> getStepRunStartedEvents() {
      return stepRunStartedEvents;
   }
}
