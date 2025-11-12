package net.ihe.gazelle.maestro.engine.business;

import net.ihe.gazelle.errorhandling.business.UnexpectedError;
import net.ihe.gazelle.errorhandling.business.UnexpectedErrorBuilder;
import net.ihe.gazelle.maestro.api.business.InvalidTestSuiteException;
import net.ihe.gazelle.maestro.api.business.message.StartTestRun;
import net.ihe.gazelle.maestro.api.business.message.StartTestSuiteRun;
import net.ihe.gazelle.maestro.api.business.message.TestRunFinished;
import net.ihe.gazelle.maestro.api.business.message.TestSuiteRunFinished;
import net.ihe.gazelle.maestro.api.business.property.Property;
import net.ihe.gazelle.maestro.api.business.recording.ItemReference;
import net.ihe.gazelle.maestro.api.business.recording.TestReportRecordException;
import net.ihe.gazelle.maestro.api.business.recording.TestReportRecordService;
import net.ihe.gazelle.maestro.api.business.test.TestReference;
import net.ihe.gazelle.maestro.api.business.test.TestSuite;
import net.ihe.gazelle.maestro.api.business.testreport.*;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRunValidator;
import net.ihe.gazelle.maestro.engine.business.context.SessionStore;
import net.ihe.gazelle.maestro.engine.business.context.TestRunCursor;
import net.ihe.gazelle.maestro.engine.business.context.TestSuiteSession;
import net.ihe.gazelle.security.business.acl.AccessControlListBuilder;
import net.ihe.gazelle.servicemetadata.api.business.MetadataService;
import net.ihe.gazelle.servicemetadata.api.business.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TestSuiteRunner {

   private static final Logger LOG = LoggerFactory.getLogger(TestSuiteRunner.class);

   private final SessionStore<TestSuiteSession> sessionStore;
   private final TestSuiteEventProducer testSuiteEventProducer;
   private final TestReportRecordService testReportRecordService;
   private final Service maestroService;
   private final TestSuiteRunValidator testSuiteRunValidator = new TestSuiteRunValidator();

   public TestSuiteRunner(SessionStore<TestSuiteSession> sessionStore,
                          TestSuiteEventProducer testSuiteEventProducer,
                          TestReportRecordService testReportRecordService, MetadataService metadataService) {
      this.sessionStore = sessionStore;
      this.testSuiteEventProducer = testSuiteEventProducer;
      this.testReportRecordService = testReportRecordService;
      this.maestroService = metadataService.getMetadata();
   }

   /**
    * Execute a TestSuite with given inputs.
    *
    * @param startTestSuiteRun the start test suite run command. Must not be null.
    *
    * @throws NullPointerException if startTestSuiteRun is null
    */
   public void run(StartTestSuiteRun startTestSuiteRun) {
      TestSuiteRun testSuiteRun = startTestSuiteRun.getTestSuiteRun();
      try {
         testSuiteRunValidator.validate(testSuiteRun).orThrow(InvalidTestSuiteException::new);

         TestRunCursor testRunCursor = TestRunCursor.from(consolidateTestRuns(testSuiteRun));
         sessionStore.addSession(startTestSuiteRun.getSessionId(), new TestSuiteSession(testSuiteRun, testRunCursor));
         testSuiteEventProducer.command(new StartTestRun(startTestSuiteRun.getSessionId(), testRunCursor.next()));
      } catch (Exception e) {
         LOG.atError().setCause(e).log("Error when starting test suite for session {}", startTestSuiteRun.getSessionId());
         TestReportBuilder testReportBuilder = buildGenericTestReport(testSuiteRun)
               .addUnexpectedError(new UnexpectedErrorBuilder().fromThrowable(e));
         TestReportSummary reportSummary = recordTestReport(testReportBuilder.build());
         sessionStore.removeSession(startTestSuiteRun.getSessionId());
         testSuiteEventProducer.notify(
               new TestSuiteRunFinished(startTestSuiteRun.getSessionId(), testSuiteRun, reportSummary));
      }
   }

   public void listen(TestRunFinished testRunFinished) {
      String sessionId = testRunFinished.getSessionId();
      if (sessionId != null && sessionStore.hasSession(sessionId)) {
         TestSuiteSession testSuiteSession = sessionStore.getSession(sessionId);
         try {
            if (testSuiteSession != null) {
               testSuiteSession.addTestRunReport(testRunFinished.getTestRunReport());
               TestRunCursor testRunCursor = testSuiteSession.getTestRunCursor();
               if (!testRunCursor.hasNext()) {
                  TestReport testReport = buildGenericTestReport(testSuiteSession.getTestSuiteRun())
                        .addTestRunReports(testSuiteSession.getTestRunReports())
                        .build();
                  TestReportSummary reportSummary = recordTestReport(testReport);
                  sessionStore.removeSession(sessionId);
                  testSuiteEventProducer.notify(
                        new TestSuiteRunFinished(sessionId, testSuiteSession.getTestSuiteRun(), reportSummary));
               } else {
                  testSuiteEventProducer.command(new StartTestRun(sessionId, testRunCursor.next()));
               }
            }
         } catch (Exception e) {
            LOG.atError().setCause(e).log("Error listening testRun finished on session {}", sessionId);
            TestReport testReport = buildGenericTestReport(testSuiteSession.getTestSuiteRun())
                  .addTestRunReports(testSuiteSession.getTestRunReports())
                  .addUnexpectedError(new UnexpectedErrorBuilder().fromThrowable(e))
                  .build();
            sessionStore.removeSession(sessionId);
            TestReportSummary reportSummary = recordTestReport(testReport);
            testSuiteEventProducer.notify(
                  new TestSuiteRunFinished(sessionId, testSuiteSession.getTestSuiteRun(), reportSummary));
         }
      }

   }

   /**
    * Create an ordered list of TestRuns from the TestSuiteRun definition, and with consolidated inputs.
    *
    * @param testSuiteRun the test suite run
    *
    * @return a list of ordered TestRuns to execute, with propagated inputs
    */
   private List<TestRun> consolidateTestRuns(TestSuiteRun testSuiteRun) {
      List<TestRun> testRuns = new ArrayList<>();
      for (TestReference testRef : testSuiteRun.getTestSuite().getTestReferences()) {
         String testId = testRef.getTestId();
         testRuns.add(new TestRun()
               .setTest(testSuiteRun.getTest(testId))
               .setInputs(consolidateInputs(testSuiteRun, testRef)));
      }
      return testRuns;
   }

   /**
    * Propagate test suite run inputs to test run inputs.
    *
    * @param testSuiteRun
    * @param testRef
    *
    * @return a merged list of properties to be consumed by the concerned test run
    */
   private List<Property> consolidateInputs(TestSuiteRun testSuiteRun, TestReference testRef) {
      List<Property> consolidatedInputs = new ArrayList<>(testSuiteRun.getInputs());
      consolidatedInputs.addAll(testRef.getProperties());
      return consolidatedInputs;
   }

   private TestReportBuilder buildGenericTestReport(TestSuiteRun testSuiteRun) {
      TestReportBuilder testReportBuilder = new TestReportBuilder()
            .setTestService(getTestServiceBuilder())
            .setAccessControlList(new AccessControlListBuilder(testSuiteRun.getAccessControlList()))
            .setSystemsUnderTest(testSuiteRun.getSystemsUnderTest().stream().map(SystemUnderTestBuilder::new).toList());
      if (testSuiteRun.getTestSuite() != null) {
         testReportBuilder.setTestSuiteName(testSuiteRun.getTestSuite().getName());
         testReportBuilder.setNote(buildNote(testSuiteRun.getTestSuite()));
      }
      return testReportBuilder;
   }

   private String buildNote(TestSuite testSuite) {
      if (testSuite.getName() != null || testSuite.getId() != null) {
         StringBuilder noteBuilder = new StringBuilder("Tests executed as part of the suite");
         if (testSuite.getName() != null) {
            noteBuilder.append(" ");
            noteBuilder.append(testSuite.getName());
         }
         if (testSuite.getId() != null) {
            noteBuilder.append(" #");
            noteBuilder.append(testSuite.getId());
         }
         return noteBuilder.toString();
      }
      return null;
   }

   private TestReportSummary recordTestReport(TestReport testReport) {
      TestReportSummary reportSummary = null;
      try {
         ItemReference reportRef = testReportRecordService.recordTestReportWithReferences(testReport);
         reportSummary = buildReportSummary(testReport, reportRef);
      } catch (TestReportRecordException e) {
         List<UnexpectedError> errors = new ArrayList<>(testReport.getUnexpectedErrors());
         errors.add(new UnexpectedErrorBuilder().fromThrowable(e).build());
         reportSummary = new TestReportSummary()
               .setDateTime(testReport.getDateTime())
               .setResult(testReport.getResult())
               .setUnexpectedErrors(errors);
      }
      return reportSummary;
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
                                                       ItemReference reportRef) {
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
