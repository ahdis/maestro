package net.ihe.gazelle.maestro.api.business;

import net.ihe.gazelle.maestro.api.business.message.ExecutionFinished;
import net.ihe.gazelle.maestro.api.business.test.TestSuite;
import net.ihe.gazelle.maestro.api.business.testreport.Result;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SynchronousMaestroTest {

   @Test
   void shouldReturnReportSummaryWhenExecutionCompletes() throws Exception {
      TestSuiteRun testSuiteRun = new TestSuiteRun()
              .setTestSuite(new TestSuite());
      TestReport expectedReport = new TestReport()
            .setUuid("summary-1")
            .setDateTime(Instant.parse("2025-01-01T00:00:00Z"))
            .setResult(Result.PASSED);

      CapturingMaestro maestro = new CapturingMaestro(expectedReport);
      SynchronousMaestro synchronousMaestro = new SynchronousMaestro(maestro);

      TestReport report = synchronousMaestro.executeTestSuite(testSuiteRun, false);

      assertSame(expectedReport, report);
      assertSame(testSuiteRun, maestro.lastTestSuiteRun);
   }

   @Test
   void shouldTimeoutWhenExecutionDoesNotComplete() {
      BlockingMaestro maestro = new BlockingMaestro();
      SynchronousMaestro synchronousMaestro = new SynchronousMaestro(maestro);
      TestSuiteRun testSuiteRun = new TestSuiteRun()
              .setTestSuite(new TestSuite());

      assertThrows(TimeoutException.class, () -> synchronousMaestro.executeTestSuite(testSuiteRun, false));
   }

   private static final class CapturingMaestro implements Maestro {

      private final TestReport report;
      private TestSuiteRun lastTestSuiteRun;

      private CapturingMaestro(TestReport report) {
         this.report = report;
      }

      @Override
      public void executeTestSuite(TestSuiteRun testSuiteRun, boolean persist, MaestroObserver observer) {
         this.lastTestSuiteRun = testSuiteRun;
         observer.onExecutionFinished(new ExecutionFinished()
               .setRun(testSuiteRun)
               .setReport(report));
      }

      @Override
      public void executeTest(TestRun testRun, boolean persist, MaestroObserver observer) {
         // Not needed for this test
      }
   }

   private static final class BlockingMaestro implements Maestro {

      @Override
      public void executeTestSuite(TestSuiteRun testSuiteRun, boolean persist, MaestroObserver observer) {
         // Intentionally left blank to trigger timeout
      }

      @Override
      public void executeTest(TestRun testRun, boolean persist, MaestroObserver observer) {
         // Not needed for this test
      }
   }
}
