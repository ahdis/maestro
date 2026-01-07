package net.ihe.gazelle.maestro.assertion.step.business.equals;

import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.spi.business.StepExecutor;
import net.ihe.gazelle.maestro.spi.business.StepRun;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.ihe.gazelle.maestro.api.business.testreport.StepResult.FAILED;
import static net.ihe.gazelle.maestro.api.business.testreport.StepResult.PASSED;
import static net.ihe.gazelle.maestro.assertion.step.business.equals.AssertEqualsStepDefinition.*;

/**
 * Executor for the assert equals step. Verifies that the actual value equals the expected value.
 */
public class AssertEqualsStepExecutor implements StepExecutor {

   /**
    * Default constructor.
    */
   public AssertEqualsStepExecutor() { /* Default constructor */ }

   @Override
   public StepRunReport execute(StepRun stepRun) {
      StepRunReportBuilder reportBuilder = new StepRunReportBuilder()
            .setStepName(stepRun.getName())
            .setType(stepRun.getType());

      String expected = stepRun.getPropertyValue(EXPECTED);
      String actual = stepRun.getPropertyValue(ACTUAL);
      if(expected != null && expected.equals(actual)) {
         return reportBuilder.setResult(PASSED).build();
      } else {
         StringProperty failureOutput = new StringProperty(
               FAILURE_MESSAGE,
               String.format("Values are not equal. Expected: '%s', Actual: '%s'", expected, actual)
         );
         return reportBuilder.setOutputs(List.of(failureOutput)).setResult(FAILED).build();
      }
   }
}
