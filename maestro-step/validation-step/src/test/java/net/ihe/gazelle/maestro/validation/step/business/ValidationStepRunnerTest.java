/*
 * Copyright 2025 Kereval.
 */
package net.ihe.gazelle.maestro.validation.step.business;

import net.ihe.gazelle.maestro.api.business.testreport.StepResult;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRun;
import net.ihe.gazelle.maestro.validation.step.technical.handler.GazelleValidationServiceHandler;
import net.ihe.gazelle.validation.v2.api.business.report.ValidationReport;
import net.ihe.gazelle.validation.v2.api.business.report.ValidationTestResult;
import net.ihe.gazelle.validation.v2.api.business.request.ValidationRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ValidationStepRunnerTest {

    private ValidationStep buildStep(String name) {
        return new ValidationStep(name,
                new Property<>(ValidationStep.VALIDATION_SERVICE, "service"),
                new Property<>(ValidationStep.VALIDATION_PROFILE, "profile"),
                List.of(new Property<>("content", "abc".getBytes(StandardCharsets.UTF_8))));
    }

    private ReportSerialization serializerReturning(byte[] bytes) {
        ReportSerialization s = mock(ReportSerialization.class);
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
    void run_setsPassed_whenReportPassed() {
        ValidationStepRunner runner = new ValidationStepRunner(serializerReturning("{}".getBytes(StandardCharsets.UTF_8)), handlerReturning(ValidationTestResult.PASSED));
        StepRun stepRun = new StepRun("ts", "tr", buildStep("v1"));
        StepRunReportBuilder b = runner.run(stepRun).join();
        StepRunReport report = b.build();
        assertEquals(StepResult.PASSED, report.getResult());
        assertEquals("v1", report.getStepId());
        assertEquals(ValidationStep.TYPE, report.getType());
    }

    @Test
    void run_setsFailed_whenReportFailed() {
        ValidationStepRunner runner = new ValidationStepRunner(serializerReturning("{}".getBytes(StandardCharsets.UTF_8)), handlerReturning(ValidationTestResult.FAILED));
        StepRun stepRun = new StepRun("ts", "tr", buildStep("v2"));
        StepRunReport report = runner.run(stepRun).join().build();
        assertEquals(StepResult.FAILED, report.getResult());
    }

    @Test
    void run_setsUndefined_whenReportUndefined() {
        ValidationStepRunner runner = new ValidationStepRunner(serializerReturning("{}".getBytes(StandardCharsets.UTF_8)), handlerReturning(null));
        StepRun stepRun = new StepRun("ts", "tr", buildStep("v3"));
        StepRunReport report = runner.run(stepRun).join().build();
        assertEquals(StepResult.UNDEFINED, report.getResult());
    }

    @Test
    void getSupportedStep_returnsCanonicalName() {
        ValidationStepRunner runner = new ValidationStepRunner(serializerReturning(new byte[]{}), handlerReturning(ValidationTestResult.PASSED));
        assertEquals(ValidationStep.class.getCanonicalName(), runner.getSupportedStep());
    }
}

