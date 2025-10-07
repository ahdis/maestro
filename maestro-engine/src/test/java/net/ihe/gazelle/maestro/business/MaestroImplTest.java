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

package net.ihe.gazelle.maestro.business;

import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testreport.TestReportSummary;
import net.ihe.gazelle.maestro.api.business.testreport.TestRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testrun.*;
import net.ihe.gazelle.maestro.api.business.testrun.context.Context;
import net.ihe.gazelle.maestro.api.business.testrun.context.TestRunSessionStore;
import net.ihe.gazelle.maestro.api.business.testrun.context.TestSetSessionStore;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRun;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MaestroImplTest {

    @Mock
    private MaestroEventProducer maestroEventProducer;

    @Mock
    private SessionStoreContext sessionStoreContext;

    @Mock
    private TestReportRecordService testReportRecordService;

    @Mock
    private TestRunSessionStore testRunSessionStore;

    @Mock
    private TestSetSessionStore testSetSessionStore;

    private MaestroImpl maestroImpl;

    @BeforeEach
    void setUp() {
        when(testReportRecordService.recordTestReportWithReferences(any())).thenReturn("testReportId");
        maestroImpl = new MaestroImpl(maestroEventProducer, sessionStoreContext, testReportRecordService, "999.0.0");
    }

    @Test
    void run_TestRun_NullTestRun_ShouldThrow() {
        assertThrows(InvalidTestRunException.class, () -> maestroImpl.run((TestRun) null));
    }

    @Test
    void run_TestRun_WithNoSteps_ShouldNotExecuteStep() {
        String testSetId = "ts";

        // Keep test non-null but invalid (no steps) to trigger validator error
        net.ihe.gazelle.maestro.api.business.testrun.Test test = new net.ihe.gazelle.maestro.api.business.testrun.Test();
        test.setId("id");
        test.setName("name");
        test.setSteps(new ArrayList<>());

        TestRun testRun = new TestRun();
        testRun.setTestSetId(testSetId);
        testRun.setTest(test);
        testRun.setInputs(List.of(new Property<>("key1", "value1")));

        TestSetRun testSetRun = new TestSetRun();
        testSetRun.setTestRuns(List.of(testRun));

        TestRunCursor testRunCursor = new TestRunCursor(List.of(testRun));

        when(sessionStoreContext.getTestSetContexts(testSetId)).thenReturn(testSetSessionStore);
        when(testSetSessionStore.getTestSetRun()).thenReturn(testSetRun);
        when(testSetSessionStore.getTestRunCursor()).thenReturn(testRunCursor);
        when(testRunSessionStore.getStepCursor()).thenReturn(StepCursor.from(test));

        maestroImpl.run(testRun);
        verify(maestroEventProducer, never()).executeStep(any());
    }

    @Test
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

        net.ihe.gazelle.maestro.api.business.testrun.Test test = new net.ihe.gazelle.maestro.api.business.testrun.Test();
        test.setId("testId");
        test.setName("testName");
        test.setSteps(List.of(step));

        TestRun testRun = new TestRun();
        testRun.setTestSetId(testSetId);
        testRun.setTest(test);
        testRun.setInputs(new ArrayList<>());

        Context context = new Context();

        // Mock for standalone test run - no testSetSessionStore
        when(sessionStoreContext.getTestSetContexts(testSetId)).thenReturn(null);
        when(sessionStoreContext.getExecutionContext(anyString())).thenReturn(testRunSessionStore);
        when(testRunSessionStore.getContext()).thenReturn(context);
        when(testRunSessionStore.getTestRun()).thenReturn(testRun);

        StepCursor stepCursor = mock(StepCursor.class);
        when(stepCursor.next()).thenReturn(step);
        when(testRunSessionStore.getStepCursor()).thenReturn(stepCursor);

        // Act
        maestroImpl.run(testRun);

        // Assert
        verify(maestroEventProducer).returnTestRunReport(eq(testRun), any(TestRunReportBuilder.class));
    }

    @Test
    void finishTest_LastTestRun_ShouldGenerateTestReportAndClearContexts() {
        // Arrange
        String testSetId = "testSet1";

        TestRun testRun = new TestRun();
        testRun.setTestSetId(testSetId);

        TestSetRun testSetRun = new TestSetRun();
        testSetRun.setTestRuns(List.of(testRun));
        List<TestRunReportBuilder> reportBuilders = new ArrayList<>();

        TestRunCursor testRunCursor = new TestRunCursor(List.of(testRun));
        testRunCursor.next(); // Move cursor to indicate this test run has executed

        when(sessionStoreContext.getTestSetContexts(testSetId)).thenReturn(testSetSessionStore);
        when(testSetSessionStore.getTestSetRun()).thenReturn(testSetRun);
        when(testSetSessionStore.getTestRunReportBuilders()).thenReturn(reportBuilders);
        when(testSetSessionStore.getTestRunCursor()).thenReturn(testRunCursor);

        TestRunReportBuilder testRunReportBuilder = mock(TestRunReportBuilder.class);

        // Act
        maestroImpl.finishTest(testRun, testRunReportBuilder);

        // Assert
        verify(testSetSessionStore).addTestRunReportBuilder(testRunReportBuilder);
        verify(sessionStoreContext).clearExecutionContext(testSetId);
        verify(sessionStoreContext).clearTestSetContext(testSetId);
        verify(maestroEventProducer).returnTestSetReport(eq(testSetRun), any(TestReportSummary.class));
    }



    @Test
    void finishTest_RunNextTestRun_NotFound_ShouldSendErrorAndClearContexts() {
        String testSetId = "testSet1";

        net.ihe.gazelle.maestro.api.business.testrun.Test test = new net.ihe.gazelle.maestro.api.business.testrun.Test();
        test.setName("t");

        TestRun testRun = new TestRun();
        testRun.setTestSetId(testSetId);
        testRun.setTest(test);

        TestRun anotherTestRun = new TestRun();

        TestSetRun testSetRun = new TestSetRun();
        testSetRun.setTestRuns(List.of(anotherTestRun));
        TestRunCursor testRunCursor = new TestRunCursor(List.of(anotherTestRun));
        testRunCursor.next(); // Move cursor to indicate run has executed

        when(sessionStoreContext.getTestSetContexts(testSetId)).thenReturn(testSetSessionStore);
        when(testSetSessionStore.getTestSetRun()).thenReturn(testSetRun);
        when(testSetSessionStore.getTestRunCursor()).thenReturn(testRunCursor);

        maestroImpl.finishTest(testRun, mock(TestRunReportBuilder.class));
        verify(sessionStoreContext).clearExecutionContext(testSetId);
        verify(sessionStoreContext).clearTestSetContext(testSetId);
        verify(maestroEventProducer).returnTestSetReport(eq(testSetRun), any(TestReportSummary.class));
    }

    @Test
    void recordAndReturnTestReport_WhenRecordFails_ShouldEmitErrorEvent() {
        String testSetId = "testSet1";

        TestRun testRun = new TestRun();
        testRun.setTestSetId(testSetId);

        TestSetRun testSetRun = new TestSetRun();
        testSetRun.setTestRuns(List.of(testRun));
        TestRunCursor testRunCursor = new TestRunCursor(List.of(testRun));
        testRunCursor.next(); // Move cursor to indicate run has executed

        when(sessionStoreContext.getTestSetContexts(testSetId)).thenReturn(testSetSessionStore);
        when(testSetSessionStore.getTestSetRun()).thenReturn(testSetRun);
        when(testSetSessionStore.getTestRunCursor()).thenReturn(testRunCursor);
        when(testReportRecordService.recordTestReportWithReferences(any())).thenThrow(new RuntimeException("db down"));

        maestroImpl.finishTest(testRun, mock(TestRunReportBuilder.class));
        verify(maestroEventProducer).emitErrorTestRunEvent(any(RuntimeException.class));
    }

    @Test
    void finishStep_LastStep_ShouldGenerateTestRunReport() {
        // Arrange
        String testRunId = "testRun1";
        String testSetId = "testSet1";

        Step step = new TestStep();
        step.setName("step1");

        net.ihe.gazelle.maestro.api.business.testrun.Test test = new net.ihe.gazelle.maestro.api.business.testrun.Test();
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

        when(sessionStoreContext.getExecutionContext(testRunId)).thenReturn(testRunSessionStore);
        when(testRunSessionStore.getTestRun()).thenReturn(testRun);
        when(testRunSessionStore.getStepReports()).thenReturn(stepReports);
        when(testRunSessionStore.getStepCursor()).thenReturn(stepCursor);

        // Act
        maestroImpl.finishStep(stepRun, stepRunReportBuilder);

        // Assert
        verify(testRunSessionStore).addStepRunReport(stepRunReportBuilder);
        verify(maestroEventProducer).returnTestRunReport(eq(testRun), any(TestRunReportBuilder.class));
    }

    @Test
    void finishStep_NotLastStep_ShouldRunNextStep() {
        // Arrange
        String testRunId = "testRun1";
        String testSetId = "testSet1";

        Step step = new TestStep();
        step.setName("step1");

        Step nextStep = new TestStep();
        nextStep.setName("step2");
        nextStep.setProperties(new ArrayList<>());

        net.ihe.gazelle.maestro.api.business.testrun.Test test = new net.ihe.gazelle.maestro.api.business.testrun.Test();
        test.setSteps(List.of(step, nextStep));

        TestRun testRun = new TestRun();
        testRun.setTest(test);

        StepCursor stepCursor = new StepCursor(List.of(step, nextStep));
        stepCursor.next(); // Move cursor to first step

        Context context = new Context();
        StepRun stepRun = new StepRun(testSetId, testRunId, step);
        StepRunReportBuilder stepRunReportBuilder = mock(StepRunReportBuilder.class);

        when(sessionStoreContext.getExecutionContext(testRunId)).thenReturn(testRunSessionStore);
        when(testRunSessionStore.getTestRun()).thenReturn(testRun);
        when(testRunSessionStore.getContext()).thenReturn(context);
        when(testRunSessionStore.getStepCursor()).thenReturn(stepCursor);

        // Act
        maestroImpl.finishStep(stepRun, stepRunReportBuilder);

        // Assert
        verify(testRunSessionStore).addStepRunReport(stepRunReportBuilder);
        verify(maestroEventProducer).executeStep(any(StepRun.class));
    }

    @Test
    void constructor_ShouldInitializeFields() {
        // Act
        MaestroImpl newMaestro = new MaestroImpl(maestroEventProducer, sessionStoreContext, testReportRecordService, "1.0.0");

        // Assert
        assertNotNull(newMaestro);
    }
}
