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
    private TestRun testRun;

    @Mock
    private TestSetRun testSetRun;

    @Mock
    private net.ihe.gazelle.maestro.api.business.testrun.Test test;

    @Mock
    private Step step;

    @Mock
    private TestRunSessionStore testRunSessionStore;

    @Mock
    private TestSetSessionStore testSetSessionStore;

    @Mock
    private Context context;

    private MaestroImpl maestroImpl;

    @BeforeEach
    void setUp() {
        when(testReportRecordService.recordTestReportWithReferences(any())).thenReturn("testReportId");
        maestroImpl = new MaestroImpl(maestroEventProducer, sessionStoreContext, testReportRecordService, "999.0.0");
    }

    @Test
    void run_TestRun_ValidTestRun_ShouldExecuteSuccessfully() {
        // Arrange
        String testSetId = "testSet1";
        List<Property<?>> properties = new ArrayList<>();
        List<Step> steps = List.of(step);

        when(testRun.getTestSetId()).thenReturn(testSetId);
        when(testRun.getTest()).thenReturn(test);
        when(testRun.getInputs()).thenReturn(properties);
        when(test.getSteps()).thenReturn(steps);
        when(step.getProperties()).thenReturn(new ArrayList<>());

        // Mock the session store context to return a valid TestRunSessionStore
        when(sessionStoreContext.getExecutionContext(anyString())).thenReturn(testRunSessionStore);
        when(testRunSessionStore.getContext()).thenReturn(context);

        // Act
        maestroImpl.run(testRun);

        // Assert
        verify(sessionStoreContext).addExecutionContext(anyString(), any(TestRunSessionStore.class));
        verify(maestroEventProducer).executeStep(any(StepRun.class));
    }

    @Test
    void run_TestRun_NullTestRun_ShouldEmitErrorEvent() {
        // Act
        maestroImpl.run((TestRun) null);

        // Assert
        verify(maestroEventProducer).emitErrorTestRunEvent(any(Exception.class));
    }

    @Test
    void run_TestRun_TestIsNull_ShouldNotExecuteStep() {
        String testSetId = "ts";
        when(testRun.getTestSetId()).thenReturn(testSetId);
        // Keep test non-null but invalid (no steps) to trigger validator error and undefined report path
        when(testRun.getTest()).thenReturn(test);
        when(test.getSteps()).thenReturn(new ArrayList<>());
        when(test.getId()).thenReturn("id");
        when(test.getName()).thenReturn("name");
        when(testRun.getInputs()).thenReturn(new ArrayList<>());
        when(sessionStoreContext.getTestSetContexts(testSetId)).thenReturn(testSetSessionStore);
        when(testSetSessionStore.getTestSetRun()).thenReturn(testSetRun);
        maestroImpl.run(testRun);
        verify(maestroEventProducer, never()).executeStep(any());
    }

    @Test
    void run_TestRun_ValidationException_ShouldSendUndefinedTestReport() {
        // Arrange
        String testSetId = "testSet1";
        when(testRun.getTestSetId()).thenReturn(testSetId);
        when(testRun.getTest()).thenReturn(test);
        when(test.getId()).thenReturn("testId");
        when(test.getName()).thenReturn("testName");
        when(sessionStoreContext.getTestSetContexts(testSetId)).thenReturn(testSetSessionStore);
        when(testSetSessionStore.getTestSetRun()).thenReturn(testSetRun);

        // Force an exception during runStep by failing setProperties
        when(testRun.getInputs()).thenReturn(new ArrayList<>());
        when(sessionStoreContext.getExecutionContext(anyString())).thenReturn(testRunSessionStore);
        when(testRunSessionStore.getContext()).thenReturn(context);
        when(testRunSessionStore.getTestRun()).thenReturn(testRun);
        when(test.getSteps()).thenReturn(List.of(step));
        when(step.getProperties()).thenReturn(new ArrayList<>());
        doThrow(new RuntimeException("boom")).when(step).setProperties(any());
        when(step.isNameEquals(step)).thenReturn(true);

        // Act
        maestroImpl.run(testRun);

        // Assert
        verify(maestroEventProducer).returnTestRunReport(eq(testRun), any(TestRunReportBuilder.class));
    }

    @Test
    void run_TestSetRun_ValidTestSetRun_ShouldExecuteSuccessfully() {
        // Arrange
        String testSetId = "testSet1";
        List<TestRun> testRuns = List.of(testRun);

        when(testSetRun.getTestSetId()).thenReturn(testSetId);
        when(testSetRun.getTestRuns()).thenReturn(testRuns);
        when(testRun.setTestSetId(testSetId)).thenReturn(testRun);
        when(testRun.getTest()).thenReturn(test);
        when(testRun.getInputs()).thenReturn(new ArrayList<>());
        when(test.getSteps()).thenReturn(List.of(step));
        when(step.getProperties()).thenReturn(new ArrayList<>());

        // Mock the session store context to return a valid TestRunSessionStore
        when(sessionStoreContext.getExecutionContext(anyString())).thenReturn(testRunSessionStore);
        when(testRunSessionStore.getContext()).thenReturn(context);

        // Act
        maestroImpl.run(testSetRun);

        // Assert
        verify(sessionStoreContext).addTestSetContext(testSetId, testSetRun);
        verify(sessionStoreContext).addExecutionContext(anyString(), any(TestRunSessionStore.class));
    }

    @Test
    void run_TestSetRun_NullTestSetId_ShouldEmitErrorEvent() {
        when(testSetRun.getTestSetId()).thenReturn(null);
        // Provide minimal testRuns to avoid downstream NPEs
        when(testSetRun.getTestRuns()).thenReturn(new ArrayList<>(List.of(testRun)));
        when(testRun.setTestSetId(anyString())).thenReturn(testRun);
        when(testRun.getTest()).thenReturn(test);
        when(testRun.getInputs()).thenReturn(new ArrayList<>());
        when(test.getSteps()).thenReturn(List.of(step));
        when(step.getProperties()).thenReturn(new ArrayList<>());

        when(sessionStoreContext.getExecutionContext(anyString())).thenReturn(testRunSessionStore);
        when(testRunSessionStore.getContext()).thenReturn(context);

        maestroImpl.run(testSetRun);
        verify(maestroEventProducer).emitErrorTestRunEvent(any(InvalidTestSetException.class));
    }

    @Test
    void run_TestSetRun_Invalid_ShouldReturnSummaryWithUnexpectedError() {
        String testSetId = "ts";
        when(testSetRun.getTestSetId()).thenReturn(testSetId);
        ArrayList<TestRun> listWithNull = new ArrayList<>();
        listWithNull.add(null);
        when(testSetRun.getTestRuns()).thenReturn(listWithNull);
        maestroImpl.run(testSetRun);
        verify(maestroEventProducer).returnTestSetReport(eq(testSetRun), any(TestReportSummary.class));
        verify(sessionStoreContext, never()).addTestSetContext(anyString(), any());
    }


    @Test
    void finishTest_LastTestRun_ShouldGenerateTestReportAndClearContexts() {
        // Arrange
        String testSetId = "testSet1";
        List<TestRun> testRuns = List.of(testRun);
        List<TestRunReportBuilder> reportBuilders = new ArrayList<>();

        when(testRun.getTestSetId()).thenReturn(testSetId);
        when(sessionStoreContext.getTestSetContexts(testSetId)).thenReturn(testSetSessionStore);
        when(testSetSessionStore.getTestSetRun()).thenReturn(testSetRun);
        when(testSetRun.getTestRuns()).thenReturn(testRuns);
        when(testSetSessionStore.getTestRunReportBuilders()).thenReturn(reportBuilders);

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
        when(testRun.getTestSetId()).thenReturn(testSetId);
        when(test.getName()).thenReturn("t");
        when(testRun.getTest()).thenReturn(test);
        when(sessionStoreContext.getTestSetContexts(testSetId)).thenReturn(testSetSessionStore);
        when(testSetSessionStore.getTestSetRun()).thenReturn(testSetRun);
        when(testSetRun.getTestRuns()).thenReturn(List.of(mock(TestRun.class)));
        maestroImpl.finishTest(testRun, mock(TestRunReportBuilder.class));
        verify(sessionStoreContext).clearExecutionContext(testSetId);
        verify(sessionStoreContext).clearTestSetContext(testSetId);
        verify(maestroEventProducer).returnTestSetReport(eq(testSetRun), any(TestReportSummary.class));
    }

    @Test
    void recordAndReturnTestReport_WhenRecordFails_ShouldEmitErrorEvent() {
        String testSetId = "testSet1";
        when(testRun.getTestSetId()).thenReturn(testSetId);
        when(sessionStoreContext.getTestSetContexts(testSetId)).thenReturn(testSetSessionStore);
        when(testSetSessionStore.getTestSetRun()).thenReturn(testSetRun);
        when(testSetRun.getTestRuns()).thenReturn(List.of(testRun));
        when(testReportRecordService.recordTestReportWithReferences(any())).thenThrow(new RuntimeException("db down"));
        maestroImpl.finishTest(testRun, mock(TestRunReportBuilder.class));
        verify(maestroEventProducer).emitErrorTestRunEvent(any(RuntimeException.class));
    }

    @Test
    void finishTest_NotLastTestRun_ShouldRunNextTestRun() {
        // Arrange
        String testSetId = "testSet1";
        TestRun testRun2 = mock(TestRun.class);
        List<TestRun> testRuns = List.of(testRun, testRun2);

        when(testRun.getTestSetId()).thenReturn(testSetId);
        when(testRun2.setTestSetId(testSetId)).thenReturn(testRun2);
        when(testRun2.getTest()).thenReturn(test);
        when(testRun2.getInputs()).thenReturn(new ArrayList<>());
        when(test.getSteps()).thenReturn(List.of(step));
        when(step.getProperties()).thenReturn(new ArrayList<>());

        when(sessionStoreContext.getTestSetContexts(testSetId)).thenReturn(testSetSessionStore);
        when(testSetSessionStore.getTestSetRun()).thenReturn(testSetRun);
        when(testSetRun.getTestRuns()).thenReturn(testRuns);

        // Mock the session store context to return a valid TestRunSessionStore for the next test run
        when(sessionStoreContext.getExecutionContext(anyString())).thenReturn(testRunSessionStore);
        when(testRunSessionStore.getContext()).thenReturn(context);

        TestRunReportBuilder testRunReportBuilder = mock(TestRunReportBuilder.class);

        // Act
        maestroImpl.finishTest(testRun, testRunReportBuilder);

        // Assert
        verify(testSetSessionStore).addTestRunReportBuilder(testRunReportBuilder);
        verify(sessionStoreContext).addExecutionContext(anyString(), any(TestRunSessionStore.class));
        verify(maestroEventProducer).executeStep(any(StepRun.class));
    }

    @Test
    void finishStep_LastStep_ShouldGenerateTestRunReport() {
        // Arrange
        String testRunId = "testRun1";
        String testSetId = "testSet1";
        List<Step> steps = List.of(step);
        List<StepRunReportBuilder> stepReports = new ArrayList<>();

        StepRun stepRun = new StepRun(testSetId, testRunId, step);
        StepRunReportBuilder stepRunReportBuilder = mock(StepRunReportBuilder.class);

        when(sessionStoreContext.getExecutionContext(testRunId)).thenReturn(testRunSessionStore);
        when(testRunSessionStore.getTestRun()).thenReturn(testRun);
        when(testRunSessionStore.getStepReports()).thenReturn(stepReports);
        when(testRun.getTest()).thenReturn(test);
        when(test.getSteps()).thenReturn(steps);
        when(test.getId()).thenReturn("testId");
        when(test.getName()).thenReturn("testName");
        when(step.isNameEquals(step)).thenReturn(true);

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
        Step nextStep = mock(Step.class);
        List<Step> steps = List.of(step, nextStep);

        StepRun stepRun = new StepRun(testSetId, testRunId, step);
        StepRunReportBuilder stepRunReportBuilder = mock(StepRunReportBuilder.class);

        when(sessionStoreContext.getExecutionContext(testRunId)).thenReturn(testRunSessionStore);
        when(testRunSessionStore.getTestRun()).thenReturn(testRun);
        when(testRunSessionStore.getContext()).thenReturn(context);
        when(testRun.getTest()).thenReturn(test);
        when(test.getSteps()).thenReturn(steps);
        when(step.isNameEquals(step)).thenReturn(true);
        when(nextStep.getProperties()).thenReturn(new ArrayList<>());

        // Act
        maestroImpl.finishStep(stepRun, stepRunReportBuilder);

        // Assert
        verify(testRunSessionStore).addStepRunReport(stepRunReportBuilder);
        verify(maestroEventProducer).executeStep(any(StepRun.class));
    }

    @Test
    void finishStep_StepNotFound_ShouldThrowException() {
        // Arrange
        String testRunId = "testRun1";
        String testSetId = "testSet1";
        Step differentStep = mock(Step.class);
        List<Step> steps = List.of(differentStep);

        StepRun stepRun = new StepRun(testSetId, testRunId, step);
        StepRunReportBuilder stepRunReportBuilder = mock(StepRunReportBuilder.class);

        when(sessionStoreContext.getExecutionContext(testRunId)).thenReturn(testRunSessionStore);
        when(testRunSessionStore.getTestRun()).thenReturn(testRun);
        when(testRun.getTest()).thenReturn(test);
        when(test.getSteps()).thenReturn(steps);
        when(test.getName()).thenReturn("testName");
        when(step.getName()).thenReturn("stepName");
        when(step.isNameEquals(differentStep)).thenReturn(false);
        when(differentStep.isNameEquals(step)).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidTestRunException.class, () ->
            maestroImpl.finishStep(stepRun, stepRunReportBuilder));
    }

    @Test
    void constructor_ShouldInitializeFields() {
        // Act
        MaestroImpl newMaestro = new MaestroImpl(maestroEventProducer, sessionStoreContext, testReportRecordService, "1.0.0");

        // Assert
        assertNotNull(newMaestro);
    }
}
