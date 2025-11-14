package net.ihe.gazelle.maestro.engine.business;

import net.ihe.gazelle.maestro.api.business.MaestroObserver;
import net.ihe.gazelle.maestro.api.business.message.ExecutionFinished;
import net.ihe.gazelle.maestro.api.business.message.InteractWithUser;
import net.ihe.gazelle.maestro.api.business.message.UserInteractionCompleted;
import net.ihe.gazelle.maestro.api.business.property.StringProperty;
import net.ihe.gazelle.maestro.api.business.recording.TestReportRecordException;
import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.business.test.Test;
import net.ihe.gazelle.maestro.api.business.test.TestReference;
import net.ihe.gazelle.maestro.api.business.test.TestSuite;
import net.ihe.gazelle.maestro.api.business.testreport.StepResult;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestRunReport;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;
import net.ihe.gazelle.maestro.engine.business.context.TestRunSession;
import net.ihe.gazelle.maestro.engine.business.context.TestSuiteSession;
import net.ihe.gazelle.maestro.engine.business.mock.InMemoryTestReportRecordService;
import net.ihe.gazelle.maestro.engine.business.mock.RecordingMaestroObserver;
import net.ihe.gazelle.maestro.engine.business.mock.ScriptedStepExecutorProvider;
import net.ihe.gazelle.security.business.acl.AccessControlList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import technical.dao.SessionStoreImpl;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MaestroEngineTest {

   private ExecutorService executorService;
   private HardWiredEventBroker broker;
   private MaestroImpl maestro;
   private ScriptedStepExecutorProvider stepExecutorProvider;
   private InMemoryTestReportRecordService reportRecordService;

   @BeforeEach
   void setUp() {
      executorService = Executors.newCachedThreadPool();
      broker = new HardWiredEventBroker(executorService);
      stepExecutorProvider = new ScriptedStepExecutorProvider();
      reportRecordService = new InMemoryTestReportRecordService();

      SessionStoreImpl<MaestroObserver> testRunObserverStore = new SessionStoreImpl<>();
       SessionStoreImpl<MaestroObserver> testSuiteRunObserverStore = new SessionStoreImpl<>();
      SessionStoreImpl<TestSuiteSession> suiteSessionStore = new SessionStoreImpl<>();
      SessionStoreImpl<TestRunSession> testRunSessionStore = new SessionStoreImpl<>();

      TestSuiteRunner testSuiteRunner = new TestSuiteRunner(suiteSessionStore, broker);
      TestRunner testRunner = new TestRunner(testRunSessionStore, broker);
      StepRunner stepRunner = new StepRunner(stepExecutorProvider, broker);

      maestro = new MaestroImpl(testRunObserverStore, testSuiteRunObserverStore, broker, new MetadataServiceMock());
      broker.setServices(maestro, testSuiteRunner, testRunner, stepRunner);
   }

   @AfterEach
   void tearDown() {
      executorService.shutdownNow();
   }

   @org.junit.jupiter.api.Test
   void executeTestSuiteRunsEntireWorkflow() {
      RecordingMaestroObserver observer = new RecordingMaestroObserver();
      TestSuiteRun testSuiteRun = buildSuiteRun("suite-1", createTest("test-1", "step-1", "step-2"));
      stepExecutorProvider
            .returnsReport("step-1", passedReport("step-1"))
            .returnsReport("step-2", passedReport("step-2"));

      maestro.executeTestSuite(testSuiteRun, observer);

      ExecutionFinished finished = observer.awaitSuiteFinished(Duration.ofSeconds(5));
      assertThat(finished.getRun(), sameInstance(testSuiteRun));
      assertThat(reportRecordService.getRecordedReports(), hasSize(1));
      TestReport report = reportRecordService.getRecordedReports().get(0);
      assertThat(report.getTestRunReports(), hasSize(1));
      assertThat(report.getUnexpectedErrors(), empty());
   }

   @org.junit.jupiter.api.Test
   void executeTestSuiteWithFailingStepStillCompletes() {
      RecordingMaestroObserver observer = new RecordingMaestroObserver();
      Step failingStep = step("step-fail", "Failing step");
      Test test = createTest("test-2", "step-ok");
      test.addStep(failingStep);
      TestSuiteRun suiteRun = buildSuiteRun("suite-2", test);

      stepExecutorProvider
            .returnsReport("step-ok", passedReport("step-ok"))
            .failsWith("step-fail", new RuntimeException("boom"));

       ExecutionFinished finished = runSuite(observer, suiteRun);

      TestReport recorded = reportRecordService.getRecordedReports().get(0);
      boolean failingStepRecorded = recorded.getTestRunReports().stream()
            .flatMap(run -> run.getStepRunReports().stream())
            .anyMatch(stepReport -> stepReport.getStepId().equals("step-fail")
                                    && !stepReport.getUnexpectedErrors().isEmpty());
      assertThat(failingStepRecorded, is(true));
      assertThat(finished.getReport().getUnexpectedErrors(), empty());
   }

   @org.junit.jupiter.api.Test
   void executeTestSuiteRejectsInvalidDefinition() {
      RecordingMaestroObserver observer = new RecordingMaestroObserver();
      TestSuiteRun invalidSuite = new TestSuiteRun();

      assertThrows(IllegalArgumentException.class, () -> maestro.executeTestSuite(invalidSuite, observer));
      assertThat(reportRecordService.getRecordedReports(), empty());
   }

   @org.junit.jupiter.api.Test
   void executeTestRejectsInvalidRun() {
      RecordingMaestroObserver observer = new RecordingMaestroObserver();
      TestRun invalidRun = new TestRun();

      assertThrows(IllegalArgumentException.class, () -> maestro.executeTest(invalidRun, observer));
      assertThat(reportRecordService.getRecordedReports(), empty());
   }

   @org.junit.jupiter.api.Test
   void stepOutputsArePropagatedToNextStepContext() {
      RecordingMaestroObserver observer = new RecordingMaestroObserver();
      Step producer = step("producer-step", "Producer");
      Step consumer = step("consumer-step", "Consumer")
            .setProperties(List.of(new StringProperty("echo", "${producer-step.payload}")));

      Test test = new Test()
            .setId("context-test")
            .setName("Context propagation")
            .setSteps(List.of(producer, consumer));

      TestSuiteRun suiteRun = buildSuiteRun("suite-context", test);

      AtomicReference<String> observedValue = new AtomicReference<>();

      stepExecutorProvider
            .returnsReport("producer-step", new StepRunReport()
                  .setStepId("producer-step")
                  .setStepName("Producer")
                  .setType("SIMULATION")
                  .setResult(StepResult.PASSED)
                  .setOutputs(List.of(new StringProperty("payload", "HELLO-CONTEXT"))))
            .withExecutor("consumer-step", stepRun -> {
               observedValue.set(stepRun.getPropertyValue("echo"));
               return CompletableFuture.completedFuture(passedReport("consumer-step"));
            });

      maestro.executeTestSuite(suiteRun, observer);
      observer.awaitSuiteFinished(Duration.ofSeconds(5));

      assertThat(observedValue.get(), equalTo("HELLO-CONTEXT"));
      TestReport report = reportRecordService.getRecordedReports().get(0);
      assertThat(report.getTestRunReports().get(0).getStepRunReports(), hasSize(2));
   }

   @org.junit.jupiter.api.Test
   void executeTestSuiteRunsAllIncludedTests() {
      RecordingMaestroObserver observer = new RecordingMaestroObserver();
      Test firstTest = createTest("test-alpha", "alpha-step");
      Test secondTest = createTest("test-beta", "beta-step");

      TestSuite testSuite = new TestSuite()
            .setId("suite-multi")
            .setName("Multiple tests")
            .setTestReferences(List.of(
                  new TestReference().setTestId(firstTest.getId()),
                  new TestReference().setTestId(secondTest.getId())
            ));

      TestSuiteRun suiteRun = new TestSuiteRun()
            .setTestSuite(testSuite)
            .setTests(List.of(firstTest, secondTest))
            .setAccessControlList(new AccessControlList().setPublic(true));

      stepExecutorProvider
            .returnsReport("alpha-step", passedReport("alpha-step"))
            .returnsReport("beta-step", passedReport("beta-step"));

      maestro.executeTestSuite(suiteRun, observer);
      observer.awaitSuiteFinished(Duration.ofSeconds(5));

      TestReport report = reportRecordService.getRecordedReports().get(0);
      assertThat(report.getTestRunReports(), hasSize(2));
   }

   @org.junit.jupiter.api.Test
   void reportRecordingFailureStillReturnsSummaryWithError() {
      RecordingMaestroObserver observer = new RecordingMaestroObserver();
      TestSuiteRun suiteRun = buildSuiteRun("suite-record", createTest("test-record", "record-step"));
      stepExecutorProvider.returnsReport("record-step", passedReport("record-step"));
      reportRecordService.failNextWith(new TestReportRecordException("datahouse unreachable"));

       ExecutionFinished finished = runSuite(observer, suiteRun);

      assertThat(reportRecordService.getRecordedReports(), empty());
      assertThat(finished.getReport().getUnexpectedErrors(), is(not(empty())));
   }

   @org.junit.jupiter.api.Test
   void userInteractionDelaysStepCompletionUntilObserverResponds() {
      ManualInteractionObserver observer = new ManualInteractionObserver();
      TestSuiteRun suiteRun = buildSuiteRun("suite-interact", createTest("interaction-test", "interaction-step"));

      stepExecutorProvider.withExecutor("interaction-step", stepRun -> observer.interactWithUser(
                  new InteractWithUser("Confirm", "Proceed?"))
            .thenApply(done -> passedReport("interaction-step")));

      maestro.executeTestSuite(suiteRun, observer);

      observer.awaitInteraction(Duration.ofSeconds(2));
      assertThat(reportRecordService.getRecordedReports(), empty());

      observer.completeInteraction();
      observer.awaitSuiteFinished(Duration.ofSeconds(5));

      TestRunReport runReport = reportRecordService.getRecordedReports().get(0).getTestRunReports().get(0);
      assertThat(runReport.getStepRunReports().get(0).getResult(), is(StepResult.PASSED));
   }

   @org.junit.jupiter.api.Test
   void stepExecutorTypeMismatchProducesUndefinedStepReport() {
      RecordingMaestroObserver observer = new RecordingMaestroObserver();
      Step producer = step("producer", "Producer");
      Step consumer = step("consumer", "Consumer")
            .setProperties(List.of(new StringProperty("payloadRef", "${producer.payload}")));

      Test test = new Test()
            .setId("mismatch-test")
            .setName("Mismatch Test")
            .setSteps(List.of(producer, consumer));

      TestSuiteRun suiteRun = buildSuiteRun("suite-mismatch", test);

      stepExecutorProvider
            .returnsReport("producer", new StepRunReport()
                  .setStepId("producer")
                  .setStepName("Producer")
                  .setType("SIMULATION")
                  .setResult(StepResult.PASSED)
                  .setOutputs(List.of(new StringProperty("payload", "STRING-VALUE"))))
            .withExecutor("consumer", stepRun -> {
               // Force a ClassCastException to mimic a type mismatch at execution time
               Integer ignored = (Integer) stepRun.getPropertyValue("payloadRef");
               return CompletableFuture.completedFuture(passedReport("consumer"));
            });

      runSuite(observer, suiteRun);

      StepRunReport failingReport = reportRecordService.getRecordedReports().get(0)
            .getTestRunReports().get(0).getStepRunReports().get(1);
      assertThat(failingReport.getResult(), is(StepResult.UNDEFINED));
      assertThat(failingReport.getUnexpectedErrors(), is(not(empty())));
   }

   @org.junit.jupiter.api.Test
   void duplicateStepNamesCauseValidationError() {
      RecordingMaestroObserver observer = new RecordingMaestroObserver();
      Step stepA = step("dup-1", "duplicate");
      Step stepB = step("dup-2", "duplicate");
      Test duplicated = new Test()
            .setId("dup-test")
            .setName("Duplicate Steps Test")
            .setSteps(List.of(stepA, stepB));
      TestSuiteRun suiteRun = buildSuiteRun("suite-dup", duplicated);

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> maestro.executeTestSuite(suiteRun, observer));
      assertThat(exception.getMessage(), containsString("Steps must be unique"));
   }

   @org.junit.jupiter.api.Test
   void concurrentSuitesCompleteIndependently() {
      RecordingMaestroObserver observerOne = new RecordingMaestroObserver();
      RecordingMaestroObserver observerTwo = new RecordingMaestroObserver();

      TestSuiteRun suiteOne = buildSuiteRun("suite-concurrent-1", createTest("test-concurrent-1", "step-A"));
      TestSuiteRun suiteTwo = buildSuiteRun("suite-concurrent-2", createTest("test-concurrent-2", "step-B"));

      stepExecutorProvider
            .returnsReport("step-A", passedReport("step-A"))
            .returnsReport("step-B", passedReport("step-B"));

      CompletableFuture<ExecutionFinished> futureOne = CompletableFuture.supplyAsync(
            () -> runSuite(observerOne, suiteOne));
      CompletableFuture<ExecutionFinished> futureTwo = CompletableFuture.supplyAsync(
            () -> runSuite(observerTwo, suiteTwo));

      CompletableFuture.allOf(futureOne, futureTwo).join();

      List<TestReport> reports = reportRecordService.getRecordedReports();
      assertThat(reports, hasSize(2));
      assertThat(reports.stream()
            .map(TestReport::getTestRunReports)
            .flatMap(List::stream)
            .map(tr -> tr.getTest().getName())
            .toList(), containsInAnyOrder("Test test-concurrent-1", "Test test-concurrent-2"));
   }

   private ExecutionFinished runSuite(RecordingMaestroObserver observer, TestSuiteRun suiteRun) {
      maestro.executeTestSuite(suiteRun, observer);
      return observer.awaitSuiteFinished(Duration.ofSeconds(5));
   }

   private StepRunReport passedReport(String stepId) {
      return new StepRunReport()
            .setStepId(stepId)
            .setStepName(stepId)
            .setType("SIMULATION")
            .setResult(StepResult.PASSED);
   }

   private TestSuiteRun buildSuiteRun(String suiteId, Test test) {
      TestSuite testSuite = new TestSuite()
            .setId(suiteId)
            .setName("Suite " + suiteId)
            .setTestReferences(List.of(new TestReference().setTestId(test.getId())));

      return new TestSuiteRun()
            .setTestSuite(testSuite)
            .setTests(List.of(test))
            .setAccessControlList(new AccessControlList().setPublic(true));
   }

   private Test createTest(String testId, String... stepIds) {
      Test test = new Test()
            .setId(testId)
            .setName("Test " + testId);
      for (String stepId : stepIds) {
         test.addStep(step(stepId, "Step " + stepId));
      }
      return test;
   }

   private Step step(String id, String name) {
      return new Step()
            .setId(id)
            .setName(name)
            .setType("SIMULATION");
   }

   private static final class ManualInteractionObserver extends RecordingMaestroObserver {
      private final CompletableFuture<UserInteractionCompleted> pendingInteraction = new CompletableFuture<>();
      private final CountDownLatch interactionRequestedLatch = new CountDownLatch(1);

      @Override
      public CompletableFuture<UserInteractionCompleted> interactWithUser(InteractWithUser interactWithUser) {
         interactionRequestedLatch.countDown();
         return pendingInteraction;
      }

      void completeInteraction() {
         pendingInteraction.complete(new UserInteractionCompleted("manual-session"));
      }

      void awaitInteraction(Duration timeout) {
         try {
            if (!interactionRequestedLatch.await(timeout.toMillis(), TimeUnit.MILLISECONDS)) {
               throw new AssertionError("Interaction was not requested within " + timeout);
            }
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AssertionError("Interrupted while waiting for interaction request", e);
         }
      }
   }
}
