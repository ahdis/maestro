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

   private final Map<String, StepExecutor> executorsByStepName = new ConcurrentHashMap<>();

   @Override
   public StepExecutor getExecutor(String sessionId, Step step) {
      StepExecutor executor = executorsByStepName.get(step.getName());
      if (executor == null) {
         throw new UnknownStepException("No executor registered for step " + step.getName());
      }
      return executor;
   }

   public ScriptedStepExecutorProvider returnsReport(String stepName, StepRunReport stepRunReport) {
      executorsByStepName.put(stepName, stepRun -> CompletableFuture.completedFuture(stepRunReport));
      return this;
   }

   public ScriptedStepExecutorProvider withExecutor(String stepName, StepExecutor executor) {
      executorsByStepName.put(stepName, executor);
      return this;
   }

   public ScriptedStepExecutorProvider failsWith(String stepName, RuntimeException exception) {
      executorsByStepName.put(stepName, stepRun -> {
         CompletableFuture<StepRunReport> failed = new CompletableFuture<>();
         failed.completeExceptionally(exception);
         return failed;
      });
      return this;
   }
}
