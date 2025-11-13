package net.ihe.gazelle.maestro.engine.business;

import net.ihe.gazelle.maestro.api.business.message.StartTestRun;
import net.ihe.gazelle.maestro.api.business.message.StartTestSuiteRun;
import net.ihe.gazelle.maestro.api.business.message.TestRunFinished;
import net.ihe.gazelle.maestro.api.business.message.TestSuiteRunFinished;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.recording.TestReportRecordException;
import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.business.test.Test;
import net.ihe.gazelle.maestro.api.business.test.TestReference;
import net.ihe.gazelle.maestro.api.business.test.TestSuite;
import net.ihe.gazelle.maestro.api.business.testreport.Result;
import net.ihe.gazelle.maestro.api.business.testreport.TestRunReport;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;
import net.ihe.gazelle.maestro.engine.business.context.TestSuiteSession;
import net.ihe.gazelle.maestro.engine.business.mock.InMemoryTestReportRecordService;
import org.junit.jupiter.api.BeforeEach;
import technical.dao.SessionStoreImpl;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class TestSuiteRunnerTest {

   private SessionStoreImpl<TestSuiteSession> sessionStore;
   private RecordingTestSuiteEventProducer eventProducer;
   private InMemoryTestReportRecordService reportRecordService;
   private TestSuiteRunner testSuiteRunner;
   private Test firstTest;
   private Test secondTest;
   private TestSuiteRun suiteRun;

   @BeforeEach
   void setUp() {
      sessionStore = new SessionStoreImpl<>();
      eventProducer = new RecordingTestSuiteEventProducer();
      reportRecordService = new InMemoryTestReportRecordService();
      testSuiteRunner = new TestSuiteRunner(sessionStore, eventProducer, reportRecordService, new MetadataServiceMock());

      firstTest = createTest("test-a", "Step A");
      secondTest = createTest("test-b", "Step B");

      TestSuite testSuite = new TestSuite()
            .setId("suite")
            .setName("Suite")
            .setTestReferences(List.of(
                  new TestReference().setTestId(firstTest.getId()),
                  new TestReference().setTestId(secondTest.getId())
            ));

      suiteRun = new TestSuiteRun()
            .setTestSuite(testSuite)
            .setTests(List.of(firstTest, secondTest))
            .setInputs(List.of(new StringProperty("global", "G")))
            .setAccessControlList(new net.ihe.gazelle.security.business.acl.AccessControlList().setPublic(true));
   }

   @org.junit.jupiter.api.Test
   void runValidSuiteStartsFirstTestRunWithConsolidatedInputs() {
      testSuiteRunner.run(new StartTestSuiteRun("suite-session", suiteRun));

      assertThat(eventProducer.getCommands(), hasSize(1));
      StartTestRun command = eventProducer.getCommands().get(0);
      assertThat(command.getTestRun().getInputs(), hasItem(hasProperty("name", equalTo("global"))));
      assertThat(sessionStore.hasSession("suite-session"), is(true));
   }

   @org.junit.jupiter.api.Test
   void listenMovesToNextTestThenFinishesWithReport() {
      testSuiteRunner.run(new StartTestSuiteRun("suite-session", suiteRun));

      StartTestRun firstCommand = eventProducer.getCommands().get(0);
      TestRunReport firstReport = new TestRunReport()
            .setTest(new net.ihe.gazelle.maestro.api.business.testreport.Test().setId(firstTest.getId()).setName(firstTest.getName()))
            .setResult(Result.PASSED);

      testSuiteRunner.listen(new TestRunFinished("suite-session", firstCommand.getTestRun(), firstReport));

      assertThat(eventProducer.getCommands(), hasSize(2));

      StartTestRun secondCommand = eventProducer.getCommands().get(1);
      TestRunReport secondReport = new TestRunReport()
            .setTest(new net.ihe.gazelle.maestro.api.business.testreport.Test().setId(secondTest.getId()).setName(secondTest.getName()))
            .setResult(Result.PASSED);

      testSuiteRunner.listen(new TestRunFinished("suite-session", secondCommand.getTestRun(), secondReport));

      assertThat(eventProducer.getNotifications(), hasSize(1));
      TestSuiteRunFinished finished = eventProducer.getNotifications().get(0);
      assertThat(finished.getReportSummary().getResult(), is(Result.PASSED));
      assertThat(sessionStore.hasSession("suite-session"), is(false));
      assertThat(reportRecordService.getRecordedReports(), hasSize(1));
   }

   @org.junit.jupiter.api.Test
   void listenWhenRecordingFailsStillNotifiesWithUnexpectedError() {
      testSuiteRunner.run(new StartTestSuiteRun("suite-session", suiteRun));
      StartTestRun firstCommand = eventProducer.getCommands().get(0);
      TestRunReport report = new TestRunReport()
            .setTest(new net.ihe.gazelle.maestro.api.business.testreport.Test().setId(firstTest.getId()).setName(firstTest.getName()))
            .setResult(Result.PASSED);

      testSuiteRunner.listen(new TestRunFinished("suite-session", firstCommand.getTestRun(), report));
      reportRecordService.failNextWith(new TestReportRecordException("down"));

      StartTestRun secondCommand = eventProducer.getCommands().get(1);
      testSuiteRunner.listen(new TestRunFinished("suite-session", secondCommand.getTestRun(), report));

      TestSuiteRunFinished finished = eventProducer.getNotifications().get(0);
      assertThat(finished.getReportSummary().getUnexpectedErrors(), is(not(empty())));
   }

   @org.junit.jupiter.api.Test
   void runInvalidSuiteEmitsSummaryWithError() {
      TestSuiteRun invalid = new TestSuiteRun(); // missing suite definition

      testSuiteRunner.run(new StartTestSuiteRun("invalid", invalid));

      assertThat(eventProducer.getCommands(), empty());
      assertThat(eventProducer.getNotifications(), hasSize(1));
      assertThat(eventProducer.getNotifications().get(0).getReportSummary().getUnexpectedErrors(), is(not(empty())));
   }

   private static final class RecordingTestSuiteEventProducer implements TestSuiteEventProducer {
      private final List<StartTestRun> commands = new ArrayList<>();
      private final List<TestSuiteRunFinished> notifications = new ArrayList<>();

      @Override
      public void command(StartTestRun startTestRun) {
         commands.add(startTestRun);
      }

      @Override
      public void notify(TestSuiteRunFinished testSuiteRunFinished) {
         notifications.add(testSuiteRunFinished);
      }

      List<StartTestRun> getCommands() {
         return commands;
      }

      List<TestSuiteRunFinished> getNotifications() {
         return notifications;
      }
   }

   private Test createTest(String id, String stepName) {
      Step step = new Step().setId(id + "-" + stepName).setName(stepName).setType("SIM");
      return new Test()
            .setId(id)
            .setName("Test " + id)
            .setSteps(List.of(step));
   }
}
