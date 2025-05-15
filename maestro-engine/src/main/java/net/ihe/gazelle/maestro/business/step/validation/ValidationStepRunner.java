package net.ihe.gazelle.maestro.business.step.validation;

import net.ihe.gazelle.maestro.business.step.validation.spi.ValidationReportSerializer;
import net.ihe.gazelle.maestro.runner.StepRun;
import net.ihe.gazelle.maestro.runner.StepRunner;
import net.ihe.gazelle.testreport.ItemBuilder;
import net.ihe.gazelle.testreport.StepResult;
import net.ihe.gazelle.testreport.StepRunReportBuilder;
import net.ihe.gazelle.testreport.UnexpectedErrorBuilder;
import net.ihe.gazelle.validation.v2.api.business.report.ValidationReport;
import net.ihe.gazelle.validation.v2.api.business.report.ValidationTestResult;
import net.ihe.gazelle.validation.v2.api.business.report.validator.ValidationReportValidator;
import net.ihe.gazelle.validation.v2.api.business.request.ValidationRequest;

import java.util.List;

/**
 * A StepRunner is an object used to run ValidationStep {@link ValidationStep}
 * <br>
 * Translate ValidationStep into ValidationRequest via ValidationRequestMapper {@link ValidationRequestMapper}
 * <br>
 * Use ValidationHandler to validate content which return a ValidationReport {@link ValidationHandler}
 * <br>
 * Assert ValidationReport to extract result and build a ValidationReportBuilder {@link net.ihe.gazelle.validation.api.business.report.ValidationReportBuilder}
 * <br>
 */
public class ValidationStepRunner implements StepRunner {

    private final ValidationReportSerializer serializer;
    private final ValidationHandler validationHandler;

    public ValidationStepRunner(ValidationReportSerializer serializer, ValidationHandler validationHandler) {
        this.serializer = serializer;
        this.validationHandler = validationHandler;
    }

    @Override
    public StepRunReportBuilder run(StepRun stepRun) {
        ValidationRequest validationRequest = ValidationRequestMapper.
                createValidationRequest((ValidationStep) stepRun.step());
        ValidationReport validationReport = validationHandler.validate(validationRequest);
        ItemBuilder itemBuilder = new ItemBuilder()
                .setContent(serializer.toByteArray(validationReport))
                .setName("validationReport");
        StepRunReportBuilder stepRunReportBuilder = new StepRunReportBuilder()
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
    private void computeResult(ValidationReport validationReport, StepRunReportBuilder stepRunReportBuilder) {
        ValidationReportValidator validator = new ValidationReportValidator();
        validator.validate(validationReport).getAllInvalidRules().forEach(
                ruleResult -> stepRunReportBuilder.addUnexpectedError(
                        new UnexpectedErrorBuilder()
                                .setName(ruleResult.getId())
                                .setMessage(ruleResult.getDescription())
                )
        );
        if (ValidationTestResult.UNDEFINED.equals(validationReport.getOverallResult())) {
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
