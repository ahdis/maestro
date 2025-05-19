package net.ihe.gazelle.maestro.business.step.validation;

import net.ihe.gazelle.validation.v2.api.business.ValidationService;
import net.ihe.gazelle.validation.v2.api.business.profile.ValidationProfile;
import net.ihe.gazelle.validation.v2.api.business.report.*;
import net.ihe.gazelle.validation.v2.api.business.request.ValidationItem;
import net.ihe.gazelle.validation.v2.api.business.request.ValidationRequest;

import java.util.Arrays;
import java.util.List;

/**
 * This class is a wrapper for the ValidationServiceV1 interface into ValidationServiceV2.
 */
public class ValidationServiceV1Wrapper implements ValidationService {

    net.ihe.gazelle.validation.v1.api.application.ValidationService validationService;

    public ValidationServiceV1Wrapper(net.ihe.gazelle.validation.v1.api.application.ValidationService validationServiceV1) {
        this.validationService = validationServiceV1;
    }
    @Override
    public ValidationReport validate(ValidationRequest validationRequest) {
        return mapToV2(validationService.validate(mapToV1(validationRequest)));
    }

    @Override
    public List<ValidationProfile> getValidationProfiles() {
        return mapValidationProfileToV2(validationService.getValidationProfiles());
    }

    private net.ihe.gazelle.validation.v1.api.domain.request.structure.ValidationRequest mapToV1(ValidationRequest validationRequest) {
        return new net.ihe.gazelle.validation.v1.api.domain.request.structure.ValidationRequest()
                .setApiVersion(validationRequest.getApiVersion())
                .setValidationProfileId(validationRequest.getValidationProfileId())
                .setValidationItems(mapValidationItemToV1(validationRequest.getValidationItems()))
                .setValidationServiceName("unknown service name"); //WARN potential incompatibility

    }

    private List<net.ihe.gazelle.validation.v1.api.domain.request.structure.ValidationItem> mapValidationItemToV1(List<ValidationItem> validationItems) {
        return validationItems.stream().map(item -> new net.ihe.gazelle.validation.v1.api.domain.request.structure.ValidationItem()
                .setContent(item.getContent())
                .setLocation(item.getLocation())
                .setRole(item.getRole())
                .setItemId(item.getItemId())).toList();
    }

    private ValidationReport mapToV2(net.ihe.gazelle.validation.v1.api.domain.report.structure.ValidationReport validationReport) {
        return new ValidationReport()
                .setUuid(validationReport.getUuid())
                .setDateTime(validationReport.getDateTime())
                .setDisclaimer(validationReport.getDisclaimer())
                .setModelVersion(validationReport.getModelVersion())
                .setCounters(mapValidationCounterToV2(validationReport.getCounters()))
                .setValidationMethod(mapValidationMethodToV2(validationReport.getValidationMethod()))
                .setOverallResult(mapTestResultToV2(validationReport.getOverallResult()))
                .setAdditionalMetadata(validationReport.getAdditionalMetadata() != null ? mapMetadataToV2(validationReport.getAdditionalMetadata()): List.of())
                .setValidationItems(validationReport.getValidationItems() != null ? mapValidationItemToV2(validationReport.getValidationItems()) : List.of())
                .setReports(validationReport.getReports() != null ? mapSubReportsToV2(validationReport.getReports()) : List.of());
    }

    private List<ValidationItem> mapValidationItemToV2(List<net.ihe.gazelle.validation.v1.api.domain.request.structure.ValidationItem> validationItems) {
        return validationItems.stream().map(item -> new ValidationItem()
                .setContent(item.getContent())
                .setLocation(item.getLocation())
                .setRole(item.getRole())
                .setItemId(item.getItemId())).toList();
    }

    private List<ValidationProfile> mapValidationProfileToV2(List<net.ihe.gazelle.validation.v1.api.domain.metadata.structure.ValidationProfile> validationProfiles) {
        return validationProfiles.stream().map((validationProfile) -> new ValidationProfile()
                    .setProfileID(validationProfile.getProfileID())
                    .setProfileName(validationProfile.getProfileName())
                    .setCoveredItems(validationProfile.getCoveredItems())
                    .setDomain(validationProfile.getDomain())
                    .setInputs(List.of()) //WARN potential incompatibility
                    .setStandards(List.of()) //WARN potential incompatibility
                    .setVersion("unknown version") //WARN potential incompatibility
        ).toList();
    }

    private ValidationCounters mapValidationCounterToV2(net.ihe.gazelle.validation.v1.api.domain.report.structure.ValidationCounters validationCounters) {
        return new ValidationCounters()
                .setNumberOfAssertions(validationCounters.getNumberOfAssertions())
                .setNumberOfFailedWithErrors(validationCounters.getNumberOfFailedWithErrors())
                .setNumberOfFailedWithInfos(validationCounters.getNumberOfFailedWithInfos())
                .setNumberOfFailedWithWarnings(validationCounters.getNumberOfFailedWithWarnings())
                .setNumberOfUnexpectedErrors(validationCounters.getNumberOfUnexpectedErrors());
    }

    private ValidationMethod mapValidationMethodToV2(net.ihe.gazelle.validation.v1.api.domain.report.structure.ValidationMethod validationMethod) {
        return new ValidationMethod()
                .setValidationServiceName(validationMethod.getValidationServiceName())
                .setValidationServiceVersion(validationMethod.getValidationServiceVersion())
                .setValidationProfileID(validationMethod.getValidationProfileID())
                .setValidationProfileVersion(validationMethod.getValidationProfileVersion());
    }

    private List<Metadata> mapMetadataToV2(List<net.ihe.gazelle.validation.v1.api.domain.report.structure.Metadata> metadata) {
        return metadata.stream().map(item -> new Metadata()
                .setName(item.getName())
                .setValue(item.getValue())).toList();
    }

    private List<ValidationSubReport> mapSubReportsToV2(List<net.ihe.gazelle.validation.v1.api.domain.report.structure.ValidationSubReport> reports) {
        return reports.stream().map(item -> new ValidationSubReport()
                .setName(item.getName())
                .setStandards(item.getStandards() != null ? List.of(item.getStandards()) : List.of())
                .setSubCounters(mapValidationCounterToV2(item.getSubCounters()))
                .setSubReports(item.getSubReports() != null ? mapSubReportsToV2(item.getSubReports()) : List.of())
                .setSubReportResult(mapTestResultToV2(item.getSubReportResult()))
                .setAssertionReports(mapAssertionReportToV2(item.getAssertionReports()))
                .setUnexpectedErrors(item.getUnexpectedErrors() != null ? mapUnexpectedErrorToV2(item.getUnexpectedErrors()) : List.of())
        ).toList();
    }

    private List<AssertionReport> mapAssertionReportToV2(List<net.ihe.gazelle.validation.v1.api.domain.report.structure.AssertionReport> assertionReports) {
        return assertionReports.stream().map(item -> new AssertionReport()
                .setAssertionID(item.getAssertionID())
                .setDescription(item.getDescription())
                .setSeverity(mapSeverityToV2(item.getSeverity()))
                .setPriority(mapRequirementPriorityToV2(item.getPriority()))
                .setFormalExpression("Unknown") //WARN potential incompatibility
                .setUnexpectedErrors(item.getUnexpectedErrors() != null  ? mapUnexpectedErrorToV2(item.getUnexpectedErrors()) : List.of())
                .setRequirementIDs(item.getRequirementIDs() != null ? Arrays.stream(item.getRequirementIDs()).toList() : List.of())
        ).toList();
    }

    private List<UnexpectedError> mapUnexpectedErrorToV2(List<net.ihe.gazelle.validation.v1.api.domain.report.structure.UnexpectedError> unexpectedErrors) {
        return unexpectedErrors.stream().map(this::mapUnexpectedErrorToV2).toList();
    }

    private UnexpectedError mapUnexpectedErrorToV2(net.ihe.gazelle.validation.v1.api.domain.report.structure.UnexpectedError unexpectedError) {
        return new UnexpectedError()
                .setName(unexpectedError.getName())
                .setMessage(unexpectedError.getMessage())
                .setCause(mapUnexpectedErrorToV2(unexpectedError.getCause())
        );
    }

    private ValidationTestResult mapTestResultToV2(net.ihe.gazelle.validation.v1.api.domain.report.structure.ValidationTestResult testResult) {
        return switch (testResult) {
            case net.ihe.gazelle.validation.v1.api.domain.report.structure.ValidationTestResult.PASSED ->
                    ValidationTestResult.PASSED;
            case net.ihe.gazelle.validation.v1.api.domain.report.structure.ValidationTestResult.FAILED ->
                    ValidationTestResult.FAILED;
            case net.ihe.gazelle.validation.v1.api.domain.report.structure.ValidationTestResult.UNDEFINED ->
                    ValidationTestResult.UNDEFINED;
        };
    }

    private SeverityLevel mapSeverityToV2(net.ihe.gazelle.validation.v1.api.domain.report.structure.SeverityLevel severity) {
        return switch (severity) {
            case net.ihe.gazelle.validation.v1.api.domain.report.structure.SeverityLevel.INFO ->
                    SeverityLevel.INFO;
            case net.ihe.gazelle.validation.v1.api.domain.report.structure.SeverityLevel.WARNING ->
                    SeverityLevel.WARNING;
            case net.ihe.gazelle.validation.v1.api.domain.report.structure.SeverityLevel.ERROR ->
                    SeverityLevel.ERROR;
        };
    }

    private RequirementPriority mapRequirementPriorityToV2(net.ihe.gazelle.validation.v1.api.domain.report.structure.RequirementPriority requirementPriority) {
        return switch (requirementPriority) {
            case net.ihe.gazelle.validation.v1.api.domain.report.structure.RequirementPriority.MANDATORY ->
                    RequirementPriority.MANDATORY;
            case net.ihe.gazelle.validation.v1.api.domain.report.structure.RequirementPriority.PERMITTED ->
                    RequirementPriority.PERMITTED;
            case net.ihe.gazelle.validation.v1.api.domain.report.structure.RequirementPriority.RECOMMENDED ->
                    RequirementPriority.RECOMMENDED;
        };
    }
}
