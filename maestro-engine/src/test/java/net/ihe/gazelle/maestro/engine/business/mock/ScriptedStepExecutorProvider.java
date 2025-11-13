package net.ihe.gazelle.maestro.engine.business.mock;

import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.engine.business.StepExecutorProvider;
import net.ihe.gazelle.maestro.engine.business.UnknownStepException;
import net.ihe.gazelle.maestro.spi.business.StepExecutor;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ScriptedStepExecutorProvider implements StepExecutorProvider {

   private final Map<String, StepExecutor> executorsByStepId = new ConcurrentHashMap<>();

   @Override
   public StepExecutor getExecutor(String sessionId, Step step) {
      StepExecutor executor = executorsByStepId.get(step.getId());
      if (executor == null) {
         throw new UnknownStepException("No executor registered for step " + step.getId());
      }
      return executor;
   }

   public ScriptedStepExecutorProvider returnsReport(String stepId, StepRunReport stepRunReport) {
      executorsByStepId.put(stepId, stepRun -> CompletableFuture.completedFuture(stepRunReport));
      return this;
   }

   public ScriptedStepExecutorProvider withExecutor(String stepId, StepExecutor executor) {
      executorsByStepId.put(stepId, executor);
      return this;
   }

   public ScriptedStepExecutorProvider failsWith(String stepId, RuntimeException exception) {
      executorsByStepId.put(stepId, stepRun -> {
         CompletableFuture<StepRunReport> failed = new CompletableFuture<>();
         failed.completeExceptionally(exception);
         return failed;
      });
      return this;
   }
}
