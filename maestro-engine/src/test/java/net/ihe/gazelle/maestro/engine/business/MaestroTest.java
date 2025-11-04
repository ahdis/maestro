/*
 * Copyright 2025 Kereval.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ihe.gazelle.maestro.engine.business;

import net.ihe.gazelle.maestro.api.business.InvalidTestSuiteException;
import net.ihe.gazelle.maestro.api.business.Maestro;
import net.ihe.gazelle.maestro.api.business.message.Context;
import net.ihe.gazelle.maestro.api.business.test.Step;
import net.ihe.gazelle.maestro.api.business.test.Test;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testreport.TestReportSummary;
import net.ihe.gazelle.maestro.api.business.testreport.TestRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;
import net.ihe.gazelle.maestro.engine.business.TestReportRecordService.ItemReference;
import net.ihe.gazelle.maestro.engine.business.context.SessionStore;
import net.ihe.gazelle.maestro.engine.business.context.StepCursor;
import net.ihe.gazelle.maestro.engine.business.context.TestRunCursor;
import net.ihe.gazelle.maestro.spi.business.StepRun;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MaestroTest {

    @Mock
    private SessionStore sessionStore;

    @Mock
    private TestReportRecordService testReportRecordService;

    @Mock
    StepExecutorProvider stepExecutorProvider;

    private Maestro maestro;
    private TestSuiteRunner testSuiteRunner;
    private TestRunner testRunner;
    private StepRunner stepRunner;

    private ExecutorService executorService;
    private HardWiredEventBroker broker;

    @BeforeEach
    void setUp() {
        when(testReportRecordService.recordTestReportWithReferences(any()))
              .thenReturn(new ItemReference("testReportId", null));

        executorService = Executors.newFixedThreadPool(10);
        broker = new HardWiredEventBroker(executorService);

        maestro = new MaestroImpl(broker);
        testSuiteRunner = new TestSuiteRunner(sessionStore, broker, testReportRecordService, new MetadataServiceMock());
        testRunner = new TestRunner(sessionStore, broker);
        stepRunner = new StepRunner(stepExecutorProvider, broker);

        broker.setServices(maestro, testSuiteRunner, testRunner, stepRunner);

    }

    @AfterEach
    void tearDown() {
        executorService.shutdown();
    }

    @org.junit.jupiter.api.Test
    void testNullArgument() {
        assertThrows(InvalidTestSuiteException.class, () -> maestro.executeTestSuite(null, null));
    }

    @org.junit.jupiter.api.Test
    void testWithoutStep() {

        // Keep test non-null but invalid (no steps) to trigger validator error
        Test test = new Test()
              .setId("id")
              .setName("name")
              .setSteps(new ArrayList<>());

        TestRun testRun = new TestRun()
              .setTest(test)
              .setInputs(List.of(new Property<>("key1", "value1")));

        TestSuiteRun testSuiteRun = new TestSuiteRun()
              .setTestRuns(List.of(testRun));

        assertThrows(InvalidTestSuiteException.class, () -> maestro.executeTestSuite(testSuiteRun, null));
    }

    @org.junit.jupiter.api.Test
    void run_TestRun_ValidationException_ShouldSendUndefinedTestReport() {
        // Arrange
        String testSetId = "testSet1";

        // Use a mock step that throws an exception when setProperties is called
        Step step = mock(Step.class);
        when(step.getName()).thenReturn("step1");
        when(step.getType()).thenReturn("TEST");
        when(step.getProperties()).thenReturn(new ArrayList<>());
        doThrow(new RuntimeException("boom")).when(step).setProperties(any());
        when(step.isNameEquals(step)).thenReturn(true);

        Test test = new Test();
        test.setId("testId");
        test.setName("testName");
        test.setSteps(List.of(step));

        TestRun testRun = new TestRun();
        testRun.setTestSuiteId(testSetId);
        testRun.setTest(test);
        testRun.setInputs(new ArrayList<>());

        Context context = new Context();

        // Mock for standalone test run - no testSuiteSession
        when(sessionStore.getSession(testSetId)).thenReturn(null);
        when(sessionStore.getSession(anyString())).thenReturn(testRunSession);
        when(testRunSession.getContext()).thenReturn(context);
        when(testRunSession.getTestRun()).thenReturn(testRun);

        StepCursor stepCursor = mock(StepCursor.class);
        when(stepCursor.next()).thenReturn(step);
        when(testRunSession.getStepCursor()).thenReturn(stepCursor);

        // Act
        testRunner.run(testRun);

        // Assert
        verify(maestroEventProducer).returnTestRunReport(eq(testRun), any(TestRunReportBuilder.class));
    }

    @org.junit.jupiter.api.Test
    void finishTest_LastTestRun_ShouldGenerateTestReportAndClearContexts() {
        // Arrange
        String testSetId = "testSet1";

        TestRun testRun = new TestRun();
        testRun.setTestSuiteId(testSetId);

        TestSuiteRun testSuiteRun = new TestSuiteRun();
        testSuiteRun.setTestRuns(List.of(testRun));
        List<TestRunReportBuilder> reportBuilders = new ArrayList<>();

        TestRunCursor testRunCursor = new TestRunCursor(List.of(testRun));
        testRunCursor.next(); // Move cursor to indicate this test run has executed

        when(sessionStore.getSession(testSetId)).thenReturn(testSuiteSession);
        when(testSuiteSession.getTestSuiteRun()).thenReturn(testSuiteRun);
        when(testSuiteSession.getTestRunReportBuilders()).thenReturn(reportBuilders);
        when(testSuiteSession.getTestRunCursor()).thenReturn(testRunCursor);

        TestRunReportBuilder testRunReportBuilder = mock(TestRunReportBuilder.class);

        // Act
        testRunner.finishTest(testRun, testRunReportBuilder);

        // Assert
        verify(testSuiteSession).addTestRunReportBuilder(testRunReportBuilder);
        verify(sessionStore).removeSession(testSetId);
        verify(sessionStore).removeSession(testSetId);
        verify(maestroEventProducer).returnTestSetReport(eq(testSuiteRun), any(TestReportSummary.class));
    }



    @org.junit.jupiter.api.Test
    void finishTest_RunNextTestRun_NotFound_ShouldSendErrorAndClearContexts() {
        String testSetId = "testSet1";

        Test test = new Test();
        test.setName("t");

        TestRun testRun = new TestRun();
        testRun.setTestSuiteId(testSetId);
        testRun.setTest(test);

        TestRun anotherTestRun = new TestRun();

        TestSuiteRun testSuiteRun = new TestSuiteRun();
        testSuiteRun.setTestRuns(List.of(anotherTestRun));
        TestRunCursor testRunCursor = new TestRunCursor(List.of(anotherTestRun));
        testRunCursor.next(); // Move cursor to indicate run has executed

        when(sessionStore.getSession(testSetId)).thenReturn(testSuiteSession);
        when(testSuiteSession.getTestSuiteRun()).thenReturn(testSuiteRun);
        when(testSuiteSession.getTestRunCursor()).thenReturn(testRunCursor);

        testRunner.finishTest(testRun, mock(TestRunReportBuilder.class));
        verify(sessionStore).removeSession(testSetId);
        verify(sessionStore).removeSession(testSetId);
        verify(maestroEventProducer).returnTestSetReport(eq(testSuiteRun), any(TestReportSummary.class));
    }

    @org.junit.jupiter.api.Test
    void recordAndReturnTestReport_WhenRecordFails_ShouldEmitErrorEvent() {
        String testSetId = "testSet1";

        TestRun testRun = new TestRun();
        testRun.setTestSuiteId(testSetId);

        TestSuiteRun testSuiteRun = new TestSuiteRun();
        testSuiteRun.setTestRuns(List.of(testRun));
        TestRunCursor testRunCursor = new TestRunCursor(List.of(testRun));
        testRunCursor.next(); // Move cursor to indicate run has executed

        when(sessionStore.getSession(testSetId)).thenReturn(testSuiteSession);
        when(testSuiteSession.getTestSuiteRun()).thenReturn(testSuiteRun);
        when(testSuiteSession.getTestRunCursor()).thenReturn(testRunCursor);
        when(testReportRecordService.recordTestReportWithReferences(any())).thenThrow(new RuntimeException("db down"));

        testRunner.finishTest(testRun, mock(TestRunReportBuilder.class));
        verify(maestroEventProducer).emitErrorTestRunEvent(any(RuntimeException.class));
    }

    @org.junit.jupiter.api.Test
    void finishStep_LastStep_ShouldGenerateTestRunReport() {
        // Arrange
        String testRunId = "testRun1";
        String testSetId = "testSet1";

        Step step = new TestStep();
        step.setName("step1");

        Test test = new Test();
        test.setId("testId");
        test.setName("testName");
        test.setSteps(List.of(step));

        TestRun testRun = new TestRun();
        testRun.setTest(test);

        StepCursor stepCursor = new StepCursor(List.of(step));
        stepCursor.next(); // Move cursor to indicate step has executed

        List<StepRunReportBuilder> stepReports = new ArrayList<>();
        StepRun stepRun = new StepRun(testSetId, testRunId, step);
        StepRunReportBuilder stepRunReportBuilder = mock(StepRunReportBuilder.class);

        when(sessionStore.getSession(testRunId)).thenReturn(testRunSession);
        when(testRunSession.getTestRun()).thenReturn(testRun);
        when(testRunSession.getStepReports()).thenReturn(stepReports);
        when(testRunSession.getStepCursor()).thenReturn(stepCursor);

        // Act
        testRunner.listen(stepRun, stepRunReportBuilder);

        // Assert
        verify(testRunSession).addStepRunReport(stepRunReportBuilder);
        verify(maestroEventProducer).returnTestRunReport(eq(testRun), any(TestRunReportBuilder.class));
    }

    @org.junit.jupiter.api.Test
    void finishStep_NotLastStep_ShouldRunNextStep() {
        // Arrange
        String testRunId = "testRun1";
        String testSetId = "testSet1";

        Step step = new TestStep();
        step.setName("step1");

        Step nextStep = new TestStep();
        nextStep.setName("step2");
        nextStep.setProperties(new ArrayList<>());

        Test test = new Test();
        test.setSteps(List.of(step, nextStep));

        TestRun testRun = new TestRun();
        testRun.setTest(test);

        StepCursor stepCursor = new StepCursor(List.of(step, nextStep));
        stepCursor.next(); // Move cursor to first step

        Context context = new Context();
        StepRun stepRun = new StepRun(testSetId, testRunId, step);
        StepRunReportBuilder stepRunReportBuilder = mock(StepRunReportBuilder.class);

        when(sessionStore.getSession(testRunId)).thenReturn(testRunSession);
        when(testRunSession.getTestRun()).thenReturn(testRun);
        when(testRunSession.getContext()).thenReturn(context);
        when(testRunSession.getStepCursor()).thenReturn(stepCursor);

        // Act
        testRunner.listen(stepRun, stepRunReportBuilder);

        // Assert
        verify(testRunSession).addStepRunReport(stepRunReportBuilder);
        verify(maestroEventProducer).executeStep(any(StepRun.class));
    }

    @org.junit.jupiter.api.Test
    void constructor_ShouldInitializeFields() {
        // Act
        TestRunner newMaestro = new TestRunner(maestroEventProducer, sessionStore, testReportRecordService, "1.0.0");

        // Assert
        assertNotNull(newMaestro);
    }
}
