package net.ihe.gazelle.maestro.validation.step.technical.mapper;

import net.ihe.gazelle.evsapi.client.business.request.HandledObject;
import net.ihe.gazelle.evsapi.client.business.request.ValidationRequest;
import net.ihe.gazelle.evsapi.client.business.response.report.*;
import net.ihe.gazelle.maestro.api.business.testrun.Property;
import net.ihe.gazelle.maestro.validation.step.business.ValidationStep;
import net.ihe.gazelle.validation.v2.api.business.report.ValidationReport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EvsValidationMapperTest {

    private final EvsValidationMapper mapper = new EvsValidationMapper();

    @Test
    void should_map_validation_report() {

        ValidationReport validationReport = mapper.mapEvsReportToValidationReport(createValidationReport(), createValidationRequest());

        // Basic validation and disclaimer (3 assertions)
        assertNotNull(validationReport);
        assertEquals(ValidationReport.MODEL_VERSION, validationReport.getModelVersion());
        assertTrue(validationReport.getDisclaimer().contains("This report comes without licensing")
                && validationReport.getDisclaimer().contains("AS IS"));

        // Validate validation method (3 assertions)
        var validationMethod = validationReport.getValidationMethod();
        assertNotNull(validationMethod);
        assertEquals("Gazelle HL7v3 Validator", validationMethod.getValidationServiceName());
        assertEquals("3.9.2", validationMethod.getValidationServiceVersion());

        // Validate validation items (4 assertions)
        assertArrayEquals("test content".getBytes(), validationReport.getInputs().getFirst().getContent());

        var reports = validationReport.getReports();
        assertNotNull(reports);
        assertEquals(1, reports.size());
        var subReport = reports.getFirst();
        assertEquals("Schema validation", subReport.getName());

        assertSubReports(subReport);

        // Validate counters if present (5 assertions)
        var counters = validationReport.getCounters();
        if (counters != null) {
            assertAll("Counter validation",
                () -> assertEquals(1, counters.getNumberOfFailedWithErrors()),
                () -> assertEquals(0, counters.getNumberOfFailedWithWarnings()),
                () -> assertEquals(0, counters.getNumberOfFailedWithInfos()),
                () -> assertEquals(0, counters.getNumberOfUnexpectedErrors()),
                () -> assertEquals(1, counters.getNumberOfAssertions())
            );
        }
    }

    @Test
    void should_map_validation_request() {
        ValidationRequest request = mapper.mapToEvsRequest(createValidationStep());
        assertNotNull(request);
        assertEquals("testService", request.getValidationService().getName());
        assertEquals("ITI-44", request.getValidationService().getValidator());
        assertArrayEquals("content".getBytes(), request.getHandledObjects().getFirst().getContent());
    }

    private static void assertSubReports(net.ihe.gazelle.validation.v2.api.business.report.ValidationSubReport subReport) {
        var assertionReports = subReport.getAssertionReports();
        assertNotNull(assertionReports);
        assertEquals(1, assertionReports.size());
        var assertionReport = assertionReports.getFirst();
        assertAll("Assertion report properties",
            () -> assertEquals("Cannot parse document to extract part to validate", assertionReport.getDescription()),
            () -> assertEquals("ERROR", assertionReport.getSeverity().toString()),
            () -> assertEquals("MANDATORY", assertionReport.getPriority().toString()),
            () -> assertEquals("FAILED", assertionReport.getResult().toString()),
            () -> assertEquals("line: 0", assertionReport.getSubjectLocations().getFirst().getValue())
        );
    }


    private net.ihe.gazelle.evsapi.client.business.response.report.ValidationReport createValidationReport() {
        net.ihe.gazelle.evsapi.client.business.response.report.ValidationReport report = new net.ihe.gazelle.evsapi.client.business.response.report.ValidationReport();
        report.setUuid("662d1d93-5f2a-43a2-91d7-55b15d6cdbcc");

        ValidationOverview overview = new ValidationOverview();
        overview.setDisclaimer("This report comes without licensing. Unless required by applicable law or agreed to in writing, this report is distributed on an \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.");
        overview.setValidationServiceName("Gazelle HL7v3 Validator");
        overview.setValidationServiceVersion("3.9.2");
        overview.setValidatorID("[ITI-44] Patient Identity Feed HL7V3 - Add Patient Record");
        overview.setValidationOverallResult(ValidationTestResult.FAILED);
        try {
            overview.setValidationDateTime(new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").parse("2025-05-08T01:52:08.000Z"));
        } catch (java.text.ParseException e) {
            // Handle parsing exception if needed
        }
        report.setValidationOverview(overview);

        ValidationCounters counters = new ValidationCounters();
        counters.setFailedWithInfoNumber(0);
        counters.setNumberOfConstraints(1);
        counters.setNumberOfErrors(1);
        counters.setNumberOfWarnings(0);
        report.setCounters(counters);

        ValidationSubReport subReport = new ValidationSubReport();
        subReport.setName("Schema validation");
        subReport.setSubReportResult(ValidationTestResult.FAILED);

        ValidationCounters subCounters = new ValidationCounters();
        subCounters.setFailedWithInfoNumber(0);
        subCounters.setNumberOfConstraints(1);
        subCounters.setNumberOfErrors(1);
        subCounters.setNumberOfWarnings(0);
        subReport.setSubCounters(subCounters);

        ConstraintValidation constraint = new ConstraintValidation();
        constraint.setPriority(ConstraintPriority.MANDATORY);
        constraint.setSeverity(SeverityLevel.ERROR);
        constraint.setTestResult(ValidationTestResult.FAILED);
        constraint.setConstraintDescription("Cannot parse document to extract part to validate");
        constraint.setLocationInValidatedObject("line: 0");

        java.util.List<ConstraintValidation> constraints = new java.util.ArrayList<>();
        constraints.add(constraint);
        subReport.setConstraints(constraints);

        java.util.List<ValidationSubReport> subReports = new java.util.ArrayList<>();
        subReports.add(subReport);
        report.setSubReport(subReports);

        return report;
    }

    private net.ihe.gazelle.evsapi.client.business.request.ValidationRequest createValidationRequest() {
        return new ValidationRequest()
                .setValidationService(
                        new net.ihe.gazelle.evsapi.client.business.ValidationService()
                                .setName("TestService")
                                .setValidator("ITI-44"))
                .addValidationItem(
                        new HandledObject()
                                .setContent("test content".getBytes())
                );
    }
    private ValidationStep createValidationStep() {
        return new ValidationStep("validationStep",
                new Property<>(ValidationStep.VALIDATION_SERVICE, "testService"),
                new Property<>(ValidationStep.VALIDATION_PROFILE, "ITI-44"),
                new Property<>(ValidationStep.CONTENT_TO_VALIDATE, "content".getBytes()));
    }


}
