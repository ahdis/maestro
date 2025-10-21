package net.ihe.gazelle.maestro.engine.business;

import net.ihe.gazelle.errorhandling.business.UnexpectedError;
import net.ihe.gazelle.errorhandling.business.UnexpectedErrorBuilder;
import net.ihe.gazelle.maestro.api.business.message.StartTestRun;
import net.ihe.gazelle.maestro.api.business.message.StartTestSuiteRun;
import net.ihe.gazelle.maestro.api.business.message.TestRunFinished;
import net.ihe.gazelle.maestro.api.business.message.TestSuiteRunFinished;
import net.ihe.gazelle.maestro.api.business.testreport.EntityIdentificationBuilder;
import net.ihe.gazelle.maestro.api.business.testreport.SystemUnderTestBuilder;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestReportBuilder;
import net.ihe.gazelle.maestro.api.business.testreport.TestReportSummary;
import net.ihe.gazelle.maestro.api.business.testreport.TestServiceBuilder;
import net.ihe.gazelle.maestro.api.business.InvalidTestSuiteException;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;
import net.ihe.gazelle.maestro.engine.business.context.SessionStore;
import net.ihe.gazelle.maestro.engine.business.context.TestRunCursor;
import net.ihe.gazelle.maestro.engine.business.context.TestSuiteSession;
import net.ihe.gazelle.security.business.acl.AccessControlListBuilder;
import net.ihe.gazelle.servicemetadata.api.business.MetadataService;
import net.ihe.gazelle.servicemetadata.api.business.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestSuiteRunner {

   private static final Logger LOG = LoggerFactory.getLogger(TestSuiteRunner.class);

   private final SessionStore sessionStore;
   private final TestSuiteEventProducer testSuiteEventProducer;
   private final TestReportRecordService testReportRecordService;
   private final Service maestroService;

   public TestSuiteRunner(SessionStore sessionStore,
                          TestSuiteEventProducer testSuiteEventProducer,
                          TestReportRecordService testReportRecordService, MetadataService metadataService) {
      this.sessionStore = sessionStore;
      this.testSuiteEventProducer = testSuiteEventProducer;
      this.testReportRecordService = testReportRecordService;
      this.maestroService = metadataService.getMetadata();
   }

   public void execute(StartTestSuiteRun startTestSuiteRun) {
      TestSuiteRun testSuiteRun = startTestSuiteRun.getTestSuiteRun();
      if (testSuiteRun == null) {
         throw new InvalidTestSuiteException("TestSuiteRun is null");
      }

      try {
         TestSuiteSession testSuiteSession = new TestSuiteSession(testSuiteRun);
         sessionStore.addTestSuite(testSuiteRun.getInternalId(), testSuiteSession);
         TestRunCursor testRunCursor = testSuiteSession.getTestRunCursor();
         if (testRunCursor == null || !testRunCursor.hasNext()) {
            throw new InvalidTestSuiteException("TestSuiteRun does not contain any TestRun");
         }
         testSuiteEventProducer.command(new StartTestRun(testSuiteRun.getInternalId(), testRunCursor.next()));
      } catch (Exception e) {
         LOG.error("Error running test set {}", testSuiteRun.getInternalId(), e);
         TestReportBuilder testReportBuilder = buildGenericTestReport(testSuiteRun)
               .setDateTime(Date.from(Instant.now()))
               .addUnexpectedError(new UnexpectedErrorBuilder().fromThrowable(e));
         TestReport testReport = testReportBuilder.build();
         String sessionId = testSuiteRun.getInternalId();
         if (sessionId != null) {
            sessionStore.removeTestSuite(sessionId);
         }
         recordAndReturnTestReport(testReport, testSuiteRun);
      }
   }

   public void listen(TestRunFinished testRunFinished) {
      String testSuiteId = testRunFinished.getTestSuiteRunId();
      TestSuiteSession testSuiteSession = sessionStore.getTestSuite(testSuiteId);
      if(testSuiteSession != null) {
         testSuiteSession.addTestRunReportBuilder(testRunFinished.getTestRunReportBuilder());
         TestSuiteRun testSuiteRun = testSuiteSession.getTestSuiteRun();
         TestRunCursor testRunCursor = testSuiteSession.getTestRunCursor();
         if (!testRunCursor.hasNext()) {
            finishTestSuite(testSuiteSession.getTestSuiteRun(), testSuiteSession);
         } else {
            try {
               testSuiteEventProducer.command(new StartTestRun(testSuiteId, testRunCursor.next()));
               //fixme: remove this error handling from here
            } catch (Exception e) {
               TestRun testRun = testRunFinished.getTestRun();
               LOG.error("Error running test {}", (testRun.getTest() != null ? testRun.getTest().getName() : ""), e);
               TestReportBuilder testReportBuilder = buildGenericTestReport(testSuiteRun);
               TestReport testReport = testReportBuilder.setDateTime(Date.from(Instant.now()))
                     .setTestRunReports(testSuiteSession.getTestRunReportBuilders())
                     .addUnexpectedError(new UnexpectedErrorBuilder().fromThrowable(e))
                     .build();
               sessionStore.removeTestSuite(testSuiteId);
               recordAndReturnTestReport(testReport, testSuiteRun);
            }
         }
      }
   }

   private void finishTestSuite(TestSuiteRun testSuiteRun, TestSuiteSession testSuiteSession) {
      TestReport testReport;
      try {
         testReport = buildGenericTestReport(testSuiteRun)
               .setTestRunReports(testSuiteSession.getTestRunReportBuilders())
               .build();
      } catch (Exception e) {
         TestReportBuilder testReportBuilder = buildGenericTestReport(testSuiteRun);
         testReport = testReportBuilder
               .setTestRunReports(testSuiteSession.getTestRunReportBuilders())
               .setTestService(getTestServiceBuilder())
               .addUnexpectedError(new UnexpectedErrorBuilder().fromThrowable(e))
               .build();
      }
      sessionStore.removeTestSuite(testSuiteRun.getInternalId());
      recordAndReturnTestReport(testReport, testSuiteRun);
   }

   private TestReportBuilder buildGenericTestReport(TestSuiteRun testSuiteRun) {
      TestReportBuilder testReportBuilder = new TestReportBuilder()
            .setTestService(getTestServiceBuilder())
            .setDateTime(Date.from(Instant.now()))
            .setTestSuiteName(testSuiteRun.getTestSuiteName())
            .setAccessControlList(new AccessControlListBuilder(testSuiteRun.getAccessControlList()))
            .setSystemsUnderTest(testSuiteRun.getSystemsUnderTest().stream().map(SystemUnderTestBuilder::new).toList());
      if ((testSuiteRun.getInternalId() != null && !testSuiteRun.getInternalId().isBlank()) ||
          (testSuiteRun.getTestSuiteName() != null && !testSuiteRun.getTestSuiteName().isBlank())) {
         StringBuilder noteBuilder = buildNote(testSuiteRun);
         testReportBuilder.setNote(noteBuilder.toString());
      }
      return testReportBuilder;
   }

   private StringBuilder buildNote(TestSuiteRun testSuiteRun) {
      StringBuilder noteBuilder = new StringBuilder("Tests executed as part of the suite ");
      if (testSuiteRun.getTestSuiteName() != null && !testSuiteRun.getTestSuiteName().isBlank()) {
         noteBuilder.append(testSuiteRun.getTestSuiteName());
      }
      if (!noteBuilder.isEmpty()) {
         noteBuilder.append("#");
      }
      if (testSuiteRun.getTestSuiteClientId() != null && !testSuiteRun.getTestSuiteClientId().isBlank()) {
         noteBuilder.append(testSuiteRun.getTestSuiteClientId());
      }
      return noteBuilder;
   }

   private void recordAndReturnTestReport(TestReport testReport, TestSuiteRun testSuiteRun) {
      TestReportSummary reportSummary = null;
      try {
         TestReportRecordService.ItemReference reportRef = testReportRecordService.recordTestReportWithReferences(testReport);
         reportSummary = buildReportSummary(testReport, reportRef);
      } catch (Exception e) {
         List<UnexpectedError> errors = new ArrayList<>(testReport.getUnexpectedErrors());
         errors.add(new UnexpectedErrorBuilder().fromThrowable(e).build());
         reportSummary = new TestReportSummary()
               .setDateTime(testReport.getDateTime())
               .setResult(testReport.getResult())
               .setUnexpectedErrors(errors);
      } finally {
         testSuiteEventProducer.notify(new TestSuiteRunFinished(testSuiteRun, reportSummary));
      }
   }

   private TestServiceBuilder getTestServiceBuilder() {
      return new TestServiceBuilder()
            .setDisclaimer(
                  "This Gazelle execution service is provided for technical compliance purposes only and does not " +
                  "constitute legal, medical, or regulatory advice.")
            .setServiceIdentification(
                  new EntityIdentificationBuilder()
                        .setName(maestroService.getName())
                        .setVersion(maestroService.getVersion())
            );
   }

   private static TestReportSummary buildReportSummary(TestReport testReport,
                                                         TestReportRecordService.ItemReference reportRef) {
      TestReportSummary reportSummary = new TestReportSummary()
            .setId(reportRef.id())
            .setLocation(reportRef.location())
            .setDateTime(testReport.getDateTime())
            .setResult(testReport.getResult());
      if (testReport.getUnexpectedErrors() != null) {
         reportSummary.setUnexpectedErrors(testReport.getUnexpectedErrors());
      }
      return reportSummary;
   }



}
