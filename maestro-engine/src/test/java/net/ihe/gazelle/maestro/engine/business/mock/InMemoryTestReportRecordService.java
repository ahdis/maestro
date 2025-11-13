package net.ihe.gazelle.maestro.engine.business.mock;

import net.ihe.gazelle.maestro.api.business.recording.ItemReference;
import net.ihe.gazelle.maestro.api.business.recording.TestReportRecordService;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.api.business.recording.TestReportRecordException;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class InMemoryTestReportRecordService implements TestReportRecordService {

   private final List<TestReport> recordedReports = new CopyOnWriteArrayList<>();
   private final AtomicInteger counter = new AtomicInteger();
   private final AtomicReference<TestReportRecordException> nextFailure = new AtomicReference<>();

   @Override
   public ItemReference recordTestReportWithReferences(TestReport report) {
      TestReportRecordException failure = nextFailure.getAndSet(null);
      if (failure != null) {
         throw failure;
      }
      recordedReports.add(report);
      return new ItemReference("report-" + counter.incrementAndGet(), null);
   }

   public List<TestReport> getRecordedReports() {
      return recordedReports;
   }

    public void failNextWith(TestReportRecordException exception) {
        nextFailure.set(exception);
    }
}
