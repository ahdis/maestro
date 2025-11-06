package net.ihe.gazelle.maestro.api.business;

import net.ihe.gazelle.maestro.api.business.message.TestSuiteRunFinished;
import net.ihe.gazelle.maestro.api.business.testreport.Result;
import net.ihe.gazelle.maestro.api.business.testreport.TestReportSummary;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SynchronousMaestroTest {

   @Test
   void shouldReturnReportSummaryWhenExecutionCompletes() throws Exception {
      TestSuiteRun testSuiteRun = new TestSuiteRun();
      TestReportSummary expectedSummary = new TestReportSummary()
            .setId("summary-1")
            .setDateTime(Instant.parse("2025-01-01T00:00:00Z"))
            .setResult(Result.PASSED);

      CapturingMaestro maestro = new CapturingMaestro(expectedSummary);
      SynchronousMaestro synchronousMaestro = new SynchronousMaestro(5, maestro);

      TestReportSummary summary = synchronousMaestro.executeTestSuite(testSuiteRun);

      assertSame(expectedSummary, summary);
      assertSame(testSuiteRun, maestro.lastTestSuiteRun);
   }

   @Test
   void shouldTimeoutWhenExecutionDoesNotComplete() {
      BlockingMaestro maestro = new BlockingMaestro();
      SynchronousMaestro synchronousMaestro = new SynchronousMaestro(1, maestro);

      assertThrows(TimeoutException.class, () -> synchronousMaestro.executeTestSuite(new TestSuiteRun()));
   }

   private static final class CapturingMaestro implements Maestro {

      private final TestReportSummary reportSummary;
      private TestSuiteRun lastTestSuiteRun;

      private CapturingMaestro(TestReportSummary reportSummary) {
         this.reportSummary = reportSummary;
      }

      @Override
      public void executeTestSuite(TestSuiteRun testSuiteRun, MaestroObserver observer) {
         this.lastTestSuiteRun = testSuiteRun;
         observer.onTestSuiteFinished(new TestSuiteRunFinished()
               .setTestSuiteRun(testSuiteRun)
               .setReportSummary(reportSummary));
      }

      @Override
      public void executeTest(TestRun testRun, MaestroObserver observer) {
         // Not needed for this test
      }
   }

   private static final class BlockingMaestro implements Maestro {

      @Override
      public void executeTestSuite(TestSuiteRun testSuiteRun, MaestroObserver observer) {
         // Intentionally left blank to trigger timeout
      }

      @Override
      public void executeTest(TestRun testRun, MaestroObserver observer) {
         // Not needed for this test
      }
   }
}
