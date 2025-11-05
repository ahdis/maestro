package net.ihe.gazelle.maestro.assertion.step.business.contains;

import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.spi.business.StepExecutor;
import net.ihe.gazelle.maestro.spi.business.StepRun;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.ihe.gazelle.maestro.api.business.testreport.StepResult.FAILED;
import static net.ihe.gazelle.maestro.api.business.testreport.StepResult.PASSED;
import static net.ihe.gazelle.maestro.assertion.step.business.equals.AssertEqualsStepDefinition.ACTUAL;
import static net.ihe.gazelle.maestro.assertion.step.business.equals.AssertEqualsStepDefinition.EXPECTED;
import static net.ihe.gazelle.maestro.assertion.step.business.equals.AssertEqualsStepDefinition.FAILURE_MESSAGE;

public class AssertContainsStepExecutor implements StepExecutor {

   @Override
   public CompletableFuture<StepRunReport> execute(StepRun stepRun) {
      StepRunReportBuilder reportBuilder = new StepRunReportBuilder()
            .setStepId(stepRun.getId())
            .setStepName(stepRun.getName())
            .setType(stepRun.getType());

      String expected = stepRun.getPropertyValue(EXPECTED);
      String actual = stepRun.getPropertyValue(ACTUAL);

      if(actual != null && actual.contains(expected)) {
         return CompletableFuture.completedFuture(reportBuilder.setResult(PASSED).build());
      } else {
         StringProperty failureOutput = new StringProperty(
               FAILURE_MESSAGE,
               String.format("Expected value cannot be found. Expected: '%s', Actual: '%s'", expected, actual)
         );
         return CompletableFuture.completedFuture(
               reportBuilder.setOutputs(List.of(failureOutput)).setResult(FAILED).build()
         );
      }
   }
}
