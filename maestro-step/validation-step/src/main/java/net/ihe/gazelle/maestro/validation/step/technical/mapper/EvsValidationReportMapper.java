package net.ihe.gazelle.maestro.validation.step.technical.mapper;


import net.ihe.gazelle.validation.v2.api.business.report.*;
import net.ihe.gazelle.validation.v2.api.business.request.ValidationItemBuilder;
import net.ihe.gazelle.evsapi.client.business.response.report.ConstraintPriority;
import net.ihe.gazelle.validation.v2.api.business.request.ValidationRequest;

import java.util.regex.Pattern;

public class EvsValidationReportMapper {

    private static final String LINE_COL_REGEX = "^line \\d+(, column \\d+)?$";
    private static final Pattern LINE_COL_PATTERN = Pattern.compile(LINE_COL_REGEX);

    private static final String EXTENDED_REGEX =  "^(\\w+):(.+)$";
    private static final Pattern EXTENDED_PATTERN = Pattern.compile(EXTENDED_REGEX);
    public ValidationReport mapEvsReportToValidationReport(net.ihe.gazelle.evsapi.client.business.response.report.ValidationReport evsReport, ValidationRequest validationRequest) {
        if (evsReport == null) {
            throw new IllegalArgumentException("EVS ValidationReport cannot be null");
        }

        ValidationReportBuilder reportBuilder = new ValidationReportBuilder();
        reportBuilder.setModelVersion(ValidationReport.MODEL_VERSION);

        // Build the main components
        buildValidationOverview(evsReport, reportBuilder);
        addValidationItems(validationRequest, reportBuilder);
        addSubReports(evsReport, reportBuilder);

        return reportBuilder.build();
    }

    /**
     * Builds validation overview section including disclaimer, validation method, and metadata
     */
    private void buildValidationOverview(net.ihe.gazelle.evsapi.client.business.response.report.ValidationReport evsReport, ValidationReportBuilder reportBuilder) {
        if (evsReport.getValidationOverview() == null) {
            return;
        }
        ValidationReportBuilder builder = reportBuilder
                .setDisclaimer(evsReport.getValidationOverview().getDisclaimer())
                .setValidationMethod(createValidationMethod(evsReport.getValidationOverview()));

        addMetadataIfPresent(evsReport.getValidationOverview(), builder);
    }

    /**
     * Creates validation method from EVS validation overview
     */
    private ValidationMethod createValidationMethod(net.ihe.gazelle.evsapi.client.business.response.report.ValidationOverview overview) {
        return new ValidationMethod()
                .setValidationProfileID(overview.getValidatorID())
                .setValidationServiceName(overview.getValidationServiceName())
                .setValidationProfileVersion(overview.getValidatorVersion() != null ? overview.getValidatorVersion() : "UNKNOWN")
                .setValidationServiceVersion(overview.getValidationServiceVersion());
    }

    /**
     * Adds metadata to the report builder if metadata exists and is not empty
     */
    private void addMetadataIfPresent(net.ihe.gazelle.evsapi.client.business.response.report.ValidationOverview overview, ValidationReportBuilder builder) {
        if (overview.getAdditionalMetadata() != null && !overview.getAdditionalMetadata().isEmpty()) {
            builder.addAdditionalMetadata(
                    overview.getAdditionalMetadata().stream()
                            .map(metadata -> new MetadataBuilder()
                                    .setName(metadata.getName())
                                    .setValue(metadata.getValue()))
                            .toList());
        }
    }

    /**
     * Adds validation items from the validation request if present
     */
    private void addValidationItems(ValidationRequest validationRequest, ValidationReportBuilder reportBuilder) {
        if (!hasValidationItems(validationRequest)) {
            return;
        }

        reportBuilder.addValidationItem(
                new ValidationItemBuilder()
                        .setContent(validationRequest.getValidationItems().getFirst().getContent())
                        .setItemId(validationRequest.getValidationItems().getFirst().getItemId())
                        .setLocation(validationRequest.getValidationItems().getFirst().getLocation())
                        .setRole(validationRequest.getValidationItems().getFirst().getRole())
        );
    }

    /**
     * Checks if validation request has valid items
     */
    private boolean hasValidationItems(ValidationRequest validationRequest) {
        return validationRequest != null &&
               validationRequest.getValidationItems() != null &&
               !validationRequest.getValidationItems().isEmpty();
    }

    /**
     * Adds sub-reports to the validation report if present
     */
    private void addSubReports(net.ihe.gazelle.evsapi.client.business.response.report.ValidationReport evsReport, ValidationReportBuilder reportBuilder) {
        if (evsReport.getSubReport() == null || evsReport.getSubReport().isEmpty()) {
            return;
        }

        reportBuilder.addSubReports(
                evsReport.getSubReport().stream()
                        .map(this::createValidationSubReport)
                        .toList()
        );
    }

    /**
     * Creates a single validation sub-report from EVS sub-report
     */
    private ValidationSubReportBuilder createValidationSubReport(net.ihe.gazelle.evsapi.client.business.response.report.ValidationSubReport subReport) {
        ValidationSubReportBuilder subReportBuilder = new ValidationSubReportBuilder()
                .setName(subReport != null ? subReport.getName() : null);

        addAssertionReports(subReport, subReportBuilder);
        return subReportBuilder;
    }

    /**
     * Adds assertion reports to the sub-report if constraints exist
     */
    private void addAssertionReports(net.ihe.gazelle.evsapi.client.business.response.report.ValidationSubReport subReport, ValidationSubReportBuilder subReportBuilder) {
        if (!hasConstraints(subReport)) {
            return;
        }

        subReport.getConstraints().forEach(constraint -> {
            if (constraint != null) {
                AssertionReportBuilder assertionBuilder = createAssertionReport(constraint);
                addUnexpectedErrors(constraint, assertionBuilder);
                subReportBuilder.addAssertionReport(assertionBuilder);
            }
        });
    }

    /**
     * Checks if sub-report has valid constraints
     */
    private boolean hasConstraints(net.ihe.gazelle.evsapi.client.business.response.report.ValidationSubReport subReport) {
        return subReport != null &&
               subReport.getConstraints() != null &&
               !subReport.getConstraints().isEmpty();
    }

    /**
     * Creates assertion report from EVS constraint
     */
    private AssertionReportBuilder createAssertionReport(net.ihe.gazelle.evsapi.client.business.response.report.ConstraintValidation constraint) {
        String validLocation = getValidatedLocation(constraint.getLocationInValidatedObject());

        return new AssertionReportBuilder()
                .setAssertionID(constraint.getConstraintID())
                .setAssertionType(constraint.getConstraintType())
                .setDescription(constraint.getConstraintDescription())
                .setSubjectLocation(validLocation)
                .setSubjectValue(constraint.getValueInValidatedObject())
                .setRequirementIDs(getRequirementIds(constraint))
                .setPriority(mapPriority(constraint.getPriority()))
                .setResult(mapTestResult(constraint.getTestResult()));
    }

    /**
     * Gets validated location, fixing invalid formats if necessary
     */
    private String getValidatedLocation(String originalLocation) {
        return isSubjectLocationValid(originalLocation)
                ? originalLocation
                : makeSubjectLocationValid(originalLocation);
    }

    /**
     * Gets requirement IDs if they exist and are not empty
     */
    private java.util.List<String> getRequirementIds(net.ihe.gazelle.evsapi.client.business.response.report.ConstraintValidation constraint) {
        return constraint.getAssertionIDs() != null && !constraint.getAssertionIDs().isEmpty()
                ? constraint.getAssertionIDs()
                : null;
    }

    /**
     * Adds unexpected errors to assertion report if they exist
     */
    private void addUnexpectedErrors(net.ihe.gazelle.evsapi.client.business.response.report.ConstraintValidation constraint, AssertionReportBuilder assertionBuilder) {
        if (constraint.getUnexpectedErrors() == null || constraint.getUnexpectedErrors().isEmpty()) {
            return;
        }

        constraint.getUnexpectedErrors().forEach(error -> {
            if (error != null) {
                assertionBuilder.addUnexpectedErrorBuilder(
                        new UnexpectedErrorBuilder()
                                .setName(error.getName())
                                .setMessage(error.getMessage())
                );
            }
        });
    }

    private RequirementPriority mapPriority(ConstraintPriority constraintPriority) {
        if (constraintPriority == null) {
            return RequirementPriority.MANDATORY;
        }

        return switch (constraintPriority) {
            case MANDATORY -> RequirementPriority.MANDATORY;
            case RECOMMENDED -> RequirementPriority.RECOMMENDED;
            case PERMITTED -> RequirementPriority.PERMITTED;
        };
    }

    private ValidationTestResult mapTestResult(net.ihe.gazelle.evsapi.client.business.response.report.ValidationTestResult testResult) {
        if (testResult == null) {
            return ValidationTestResult.UNDEFINED;
        }

        return switch (testResult) {
            case PASSED -> ValidationTestResult.PASSED;
            case FAILED -> ValidationTestResult.FAILED;
            case UNDEFINED -> ValidationTestResult.UNDEFINED;
        };
    }

    private boolean isSubjectLocationValid(String subjectLocation) {
        if (subjectLocation == null) {
            return true;
        } else if (subjectLocation.isBlank()) {
            return false;
        } else {
            return EXTENDED_PATTERN.matcher(subjectLocation).find() || LINE_COL_PATTERN.matcher(subjectLocation).find();
        }
    }

    public String makeSubjectLocationValid(String subjectLocation) {
        if (subjectLocation == null || subjectLocation.isBlank()) {
            return null;
        }

        String fixedLocation = subjectLocation.trim(); // remove outer spaces
        fixedLocation = fixedLocation.replaceAll("\\s+", " "); // normalize all spaces

        if (isSubjectLocationValid(fixedLocation)) {
            return fixedLocation;
        }
        return null;
    }

}
