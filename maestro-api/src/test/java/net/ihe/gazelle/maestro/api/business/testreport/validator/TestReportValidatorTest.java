package net.ihe.gazelle.maestro.api.business.testreport.validator;

import net.ihe.gazelle.framework.modelvalidator.business.ObjectResult;
import net.ihe.gazelle.maestro.api.business.testreport.StepResult;
import net.ihe.gazelle.maestro.api.business.testreport.StepRunReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.maestro.api.business.testreport.TestRunReport;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestReportValidatorTest {

    private final TestReportValidator testReportValidator = new TestReportValidator();

    @Test
    void test_report_validator_valid() {
        ObjectResult result = testReportValidator.validate(getTestReport());
        assertTrue(result.isValid());
        testReportValidator.assertValid(getTestReport());
    }

    @Test
    void test_report_validator_invalid() {
        TestReport testReport = getTestReport();
        testReport.addTestRunReport(new TestRunReport()
                .setDateTime(new Date())
                .setTest(new net.ihe.gazelle.maestro.api.business.testreport.Test("id"))
                .addStepRunReport(new StepRunReport()
                        .setStepId("id")
                        .setType("type")
                        .setResult(StepResult.PASSED)
                )
        );
        ObjectResult result = testReportValidator.validate(testReport);
        assertFalse(result.isValid());
    }

    private TestReport getTestReport() {
        TestRunReport testRunReport = new TestRunReport()
                .setDateTime(new Date())
                .setTest(new net.ihe.gazelle.maestro.api.business.testreport.Test("id"))
                .addStepRunReport(new StepRunReport()
                        .setStepId("id")
                        .setType("type")
                        .setResult(StepResult.PASSED)
                );
        testRunReport.computeResult();

        TestReport testReport = new TestReport()
                .setReportVersion("version")
                .setUuid(UUID.randomUUID().toString())
                .setDateTime(new Date())
                .addTestRunReport(testRunReport);
        testReport.computeResult();
        testReport.computeCounters();
        return testReport;
    }
}
