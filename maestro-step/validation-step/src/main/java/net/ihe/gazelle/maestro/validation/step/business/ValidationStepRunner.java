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

package net.ihe.gazelle.maestro.validation.step.business;

import net.ihe.gazelle.maestro.api.business.testreport.ItemBuilder;
import net.ihe.gazelle.maestro.api.business.testreport.StepResult;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testrun.runner.Handler;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRun;
import net.ihe.gazelle.maestro.api.business.testrun.runner.StepRunner;
import net.ihe.gazelle.maestro.validation.step.technical.handler.EvsValidationHandler;
import net.ihe.gazelle.maestro.validation.step.technical.handler.GazelleValidationServiceHandler;
import net.ihe.gazelle.maestro.validation.step.technical.mapper.EvsValidationMapper;
import net.ihe.gazelle.validation.v2.api.business.report.ValidationReport;
import net.ihe.gazelle.validation.v2.api.business.report.ValidationTestResult;
import net.ihe.gazelle.validation.v2.api.business.request.ValidationRequest;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A StepRunner is an object used to run ValidationStep {@link ValidationStep}
 * <br>
 * Translate ValidationStep into ValidationRequest via ValidationRequestMapper {@link ValidationRequestMapper}
 * <br>
 * Use ValidationHandler to validate content which return a ValidationReport {@link ValidationHandler}
 * <br>
 * Assert ValidationReport to extract result and build a ValidationReportBuilder {@link net.ihe.gazelle.validation.v2.api.business.report.ValidationReportBuilder}
 * <br>
 */
public class ValidationStepRunner implements StepRunner {

    private final ReportSerialization serializer;
    private final Handler handler;
    private final EvsValidationMapper evsValidationMapper = new EvsValidationMapper();

    public ValidationStepRunner(ReportSerialization serializer, ValidationHandler validationHandler) {
        this.serializer = serializer;
        this.handler = validationHandler;
    }

    @Override
    public CompletableFuture<StepRunReportBuilder> run(StepRun stepRun) {
        ValidationStep validationStep = (ValidationStep) stepRun.step();
        ValidationRequest validationRequest = ValidationRequestMapper.
                createValidationRequest((ValidationStep) stepRun.step());
        ValidationReport validationReport = new ValidationReport();

        if(handler instanceof EvsValidationHandler evsHandler) {
            net.ihe.gazelle.evsapi.client.business.request.ValidationRequest evsRequest = evsValidationMapper.mapToEvsRequest(validationStep);
            net.ihe.gazelle.evsapi.client.business.response.report.ValidationReport evsReport = evsHandler.validate(evsRequest);
            validationReport = evsValidationMapper.mapEvsReportToValidationReport(evsReport, evsRequest);
        }
        if(handler instanceof GazelleValidationServiceHandler validationServiceHandler) {
            validationReport = validationServiceHandler.validate(validationRequest);
        }

        ItemBuilder itemBuilder = new ItemBuilder()
                .setContent(serializer.toByteArray(validationReport))
                .setName("validationReport");
        StepRunReportBuilder stepRunReportBuilder = new StepRunReportBuilder()
                .setOutputs(List.of(itemBuilder))
                .setStepId(stepRun.step().getName())
                .setType(ValidationStep.TYPE);
        computeResult(validationReport, stepRunReportBuilder);
        return CompletableFuture.completedFuture(stepRunReportBuilder);
    }

    /**
     * Compute the validation step result from the received validation report from external validation tool
     *
     * @param validationReport     The received validation report
     * @param stepRunReportBuilder An object to build the Step run report
     */
    private void computeResult(ValidationReport validationReport, StepRunReportBuilder stepRunReportBuilder) {
        if (ValidationTestResult.PASSED.equals(validationReport.getOverallResult())) {
            stepRunReportBuilder.setResult(StepResult.PASSED);
        } else if (ValidationTestResult.FAILED.equals(validationReport.getOverallResult())) {
            stepRunReportBuilder.setResult(StepResult.FAILED);
        } else {
            stepRunReportBuilder.setResult(StepResult.UNDEFINED);
        }
    }

    @Override
    public final String getSupportedStep() {
        return ValidationStep.class.getCanonicalName();
    }
}
