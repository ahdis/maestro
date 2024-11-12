package report.validator;

import net.ihe.gazelle.framework.modelvalidator.domain.result.ObjectResult;
import net.ihe.gazelle.framework.modelvalidator.interlay.adapter.BValidatorBuilderFactory;
import net.ihe.gazelle.report.model.StepResult;
import net.ihe.gazelle.report.model.StepRun;
import net.ihe.gazelle.report.model.TestReport;
import net.ihe.gazelle.report.model.TestRun;
import net.ihe.gazelle.report.validator.TestReportValidator;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestReportValidatorTest {

    private final TestReportValidator testReportValidator = new TestReportValidator(new BValidatorBuilderFactory());

    @Test
    void test_report_validator_valid() {
        ObjectResult result = testReportValidator.validate(getTestReport());
        assertTrue(result.isValid());
        testReportValidator.assertValid(getTestReport());
        assertTrue(testReportValidator.isValid(getTestReport()));
    }

    @Test
    void test_report_validator_invalid() {
        TestReport testReport = getTestReport();
        testReport.addTestRun(new TestRun()
                .setDateTime(new Date())
                .setTest(new net.ihe.gazelle.report.model.Test("id"))
                .addStepRun(new StepRun()
                        .setStepId("id")
                        .setType("type")
                        .setResult(StepResult.PASSED)
                )
        );
        ObjectResult result = testReportValidator.validate(testReport);
        assertFalse(result.isValid());
        assertFalse(testReportValidator.isValid(testReport));
    }

    private TestReport getTestReport() {
        TestRun testRun = new TestRun()
                .setDateTime(new Date())
                .setTest(new net.ihe.gazelle.report.model.Test("id"))
                .addStepRun(new StepRun()
                        .setStepId("id")
                        .setType("type")
                        .setResult(StepResult.PASSED)
                );
        testRun.computeResult();

        TestReport testReport = new TestReport()
                .setReportVersion("version")
                .setUuid(UUID.randomUUID().toString())
                .setDateTime(new Date())
                .addTestRun(testRun);
        testReport.computeResult();
        testReport.computeCounters();
        return testReport;
    }
}
