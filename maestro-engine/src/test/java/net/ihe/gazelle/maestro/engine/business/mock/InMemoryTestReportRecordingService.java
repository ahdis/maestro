package net.ihe.gazelle.maestro.engine.business.mock;

import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.engine.business.TestReportRecordingException;
import net.ihe.gazelle.maestro.engine.business.TestReportRecordingService;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class InMemoryTestReportRecordingService implements TestReportRecordingService {

   private final List<TestReport> recordedReports = new CopyOnWriteArrayList<>();
   private final AtomicReference<TestReportRecordingException> nextFailure = new AtomicReference<>();

    @Override
    public String recordTestReport(TestReport report) {
        return recordTestReportWithReferences(report);
    }

   private synchronized String recordTestReportWithReferences(TestReport report) {
      TestReportRecordingException failure = nextFailure.getAndSet(null);
      if (failure != null) {
         throw failure;
      }
      recordedReports.add(report);
      return "report-" + recordedReports.size();
   }

   public List<TestReport> getRecordedReports() {
      return new CopyOnWriteArrayList<>(recordedReports);
   }

   public void failNextWith(TestReportRecordingException exception) {
        nextFailure.set(exception);
    }

   public void clear() {
        recordedReports.clear();
    }
}
