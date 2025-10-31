/*
 * Copyright 2025 Kereval.
 */
package net.ihe.gazelle.maestro.validation.step.business;

import net.ihe.gazelle.maestro.api.business.testreport.StepResult;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.spi.business.runner.StepRun;
import net.ihe.gazelle.maestro.validation.step.technical.handler.GazelleValidationServiceHandler;
import net.ihe.gazelle.validation.v2.api.business.report.ValidationReport;
import net.ihe.gazelle.validation.v2.api.business.report.ValidationTestResult;
import net.ihe.gazelle.validation.v2.api.business.request.ValidationRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ValidationStepExecutorTest {

    private ValidationStep buildStep(String name) {
        return new ValidationStep(name,
                new Property<>(ValidationStep.VALIDATION_SERVICE, "service"),
                new Property<>(ValidationStep.VALIDATION_PROFILE, "profile"),
                List.of(new Property<>("content", "abc".getBytes(StandardCharsets.UTF_8))));
    }

    private ReportSerializer serializerReturning(byte[] bytes) {
        ReportSerializer s = mock(ReportSerializer.class);
        when(s.toByteArray(any(ValidationReport.class))).thenReturn(bytes);
        return s;
    }

    private GazelleValidationServiceHandler handlerReturning(ValidationTestResult result) {
        GazelleValidationServiceHandler handler = Mockito.mock(GazelleValidationServiceHandler.class, CALLS_REAL_METHODS);
        doReturn(true).when(handler).isAvailable();
        doAnswer(inv -> {
            ValidationReport r = new ValidationReport();
            r.setOverallResult(result);
            return r;
        }).when(handler).validate(any(ValidationRequest.class));
        return handler;
    }

    @Test
    void execute_setsPassed_whenReportPassed() {
        ValidationStepExecutor runner = new ValidationStepExecutor(handlerReturning(ValidationTestResult.PASSED),
              serializerReturning("{}".getBytes(StandardCharsets.UTF_8)));
        StepRun stepRun = new StepRun("ts", "tr", buildStep("v1"));
        StepRunReportBuilder b = runner.execute(stepRun).join();
        StepRunReport report = b.build();
        assertEquals(StepResult.PASSED, report.getResult());
        assertEquals("v1", report.getStepId());
        assertEquals(ValidationStep.TYPE, report.getType());
    }

    @Test
    void execute_setsFailed_whenReportFailed() {
        ValidationStepExecutor runner = new ValidationStepExecutor(handlerReturning(ValidationTestResult.FAILED),
              serializerReturning("{}".getBytes(StandardCharsets.UTF_8)));
        StepRun stepRun = new StepRun("ts", "tr", buildStep("v2"));
        StepRunReport report = runner.execute(stepRun).join().build();
        assertEquals(StepResult.FAILED, report.getResult());
    }

    @Test
    void execute_setsUndefined_whenReportUndefined() {
        ValidationStepExecutor runner = new ValidationStepExecutor(handlerReturning(null),
              serializerReturning("{}".getBytes(StandardCharsets.UTF_8)));
        StepRun stepRun = new StepRun("ts", "tr", buildStep("v3"));
        StepRunReport report = runner.execute(stepRun).join().build();
        assertEquals(StepResult.UNDEFINED, report.getResult());
    }

    @Test
    void getSupportedStep_returnsCanonicalName() {
        ValidationStepExecutor runner = new ValidationStepExecutor(handlerReturning(ValidationTestResult.PASSED),
              serializerReturning(new byte[]{}));
        assertEquals(ValidationStep.class.getCanonicalName(), runner.getSupportedStep());
    }
}

