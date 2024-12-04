package net.ihe.gazelle.maestro.business.step.validation;

import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.interlay.adapter.BValidatorBuilderFactory;
import net.ihe.gazelle.maestro.business.step.validation.spi.ValidationReportSerializer;
import net.ihe.gazelle.maestro.runner.StepRun;
import net.ihe.gazelle.maestro.runner.StepRunner;
import net.ihe.gazelle.testreport.ItemBuilder;
import net.ihe.gazelle.testreport.StepResult;
import net.ihe.gazelle.testreport.StepRunReportBuilder;
import net.ihe.gazelle.validation.api.application.report.validator.ValidationReportValidator;
import net.ihe.gazelle.validation.api.domain.report.structure.ValidationReport;
import net.ihe.gazelle.validation.api.domain.report.structure.ValidationTestResult;
import net.ihe.gazelle.validation.api.domain.request.structure.ValidationRequest;

import java.util.List;

/**
 * A StepRunner is an object used to run ValidationStep
 * @see ValidationStep
 * Translate ValidationStep into ValidationRequest via ValidationRequestMapper
 * @see ValidationRequestMapper
 * Use ValidationHandler to validate content which return a ValidationReport
 * @see ValidationHandler
 * Assert ValidationReport to extract result and build a ValidationReportBuildert
 * @see net.ihe.gazelle.validation.api.domain.report.structure.ValidationReportBuilder
 */
public class ValidationStepRunner implements StepRunner {

    private final ValidationReportSerializer serializer;
    private final ValidatorBuilderFactory validatorBuilderFactory;
    private final ValidationHandler validationHandler;

    public ValidationStepRunner(ValidationReportSerializer serializer, ValidatorBuilderFactory validatorBuilderFactory, ValidationHandler validationHandler) {
        this.serializer = serializer;
        this.validatorBuilderFactory = validatorBuilderFactory;
        this.validationHandler = validationHandler;
    }

    @Override
    public StepRunReportBuilder run(StepRun stepRun) {
        ValidationRequest validationRequest = ValidationRequestMapper.
                createValidationRequest((ValidationStep) stepRun.step());
        ValidationReport validationReport = validationHandler.validate(validationRequest);
        ItemBuilder itemBuilder = new ItemBuilder(validatorBuilderFactory)
                .setContent(serializer.toByteArray(validationReport))
                .setName(stepRun.step().getName());
        StepRunReportBuilder stepRunReportBuilder = new StepRunReportBuilder(validatorBuilderFactory)
                .setOutputs(List.of(itemBuilder))
                .setStepId(stepRun.step().getName())
                .setType(ValidationStep.TYPE);
        computeResult(validationReport, stepRunReportBuilder);
        return stepRunReportBuilder;
    }

    /**
     * Compute the validation step result from the received validation report from external validation tool
     * @param validationReport The received validation report
     * @param stepRunReportBuilder An object to build the Step run report
     */
    private static void computeResult(ValidationReport validationReport, StepRunReportBuilder stepRunReportBuilder) {
        ValidationReportValidator validator = new ValidationReportValidator(new BValidatorBuilderFactory());
        if (!validator.isValid(validationReport) || ValidationTestResult.UNDEFINED.equals(validationReport.getOverallResult())) {
            stepRunReportBuilder.setResult(StepResult.UNDEFINED);
        } else if (ValidationTestResult.FAILED.equals(validationReport.getOverallResult())) {
            stepRunReportBuilder.setResult(StepResult.FAILED);
        } else {
            stepRunReportBuilder.setResult(StepResult.PASSED);
        }
    }

    @Override
    public final String getSupportedStep() {
        return ValidationStep.class.getCanonicalName();
    }
}
