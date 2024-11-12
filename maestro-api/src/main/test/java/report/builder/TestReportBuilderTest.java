package report.builder;

import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.interlay.adapter.BValidatorBuilderFactory;
import net.ihe.gazelle.report.builder.*;
import net.ihe.gazelle.report.model.Result;
import net.ihe.gazelle.report.model.StepResult;
import net.ihe.gazelle.report.model.TestReport;
import org.junit.jupiter.api.BeforeAll;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestReportBuilderTest {

    private static ValidatorBuilderFactory validatorBuilderFactory;

    @BeforeAll
    public static void setup() {
        validatorBuilderFactory = new BValidatorBuilderFactory();
    }

    @org.junit.jupiter.api.Test
    void testReport_builder_minimum_valid() {
        TestReport testReport = new TestReportBuilder(validatorBuilderFactory)
                .setUuid()
                .setDateTime(new Date())
                .addTestRun(new TestRunBuilder(validatorBuilderFactory)
                        .setDateTime(new Date())
                        .setTest(
                                new TestBuilder(validatorBuilderFactory)
                                        .setId("id")
                        )
                )
                .build();

        assertEquals(Result.UNDEFINED, testReport.getResult());
        assertTrue(testReport.isValid());
        assertEquals(1, testReport.getTestCounters().getTotal());
        assertEquals(1, testReport.getTestCounters().getUndefined());
    }

    @org.junit.jupiter.api.Test
    void testReport_builder_with_subReports_passed() {
        TestRunBuilder testRunBuilder = createTestRunBuilder();

        TestReportBuilder subReportBuilder = createTestReportBuilder()
                .addTestRun(testRunBuilder);

        TestReport testReport = new TestReportBuilder(validatorBuilderFactory)
                .setUuid()
                .setDateTime(new Date())
                .addTestRun(createTestRunBuilder())
                .addSubReport(subReportBuilder)
                .build();

        assertEquals(Result.PASSED, testReport.getResult());
        assertTrue(testReport.isValid());
        assertEquals(2, testReport.getTestCounters().getTotal());
        assertEquals(2, testReport.getTestCounters().getPassed());
    }

    @org.junit.jupiter.api.Test
    void testReport_builder_with_subReports_failed() {
        TestRunBuilder testRunBuilder = createTestRunBuilder();
        testRunBuilder.addStepRun(new StepRunBuilder(validatorBuilderFactory)
                .setStepId("stepId")
                .setType("validation")
                .setResult(StepResult.FAILED));

        TestReportBuilder subReportBuilder = createTestReportBuilder()
                .addTestRun(testRunBuilder);

        TestReport testReport = new TestReportBuilder(validatorBuilderFactory)
                .setUuid()
                .setDateTime(new Date())
                .addTestRun(createTestRunBuilder())
                .addSubReport(subReportBuilder)
                .build();

        assertEquals(Result.FAILED, testReport.getResult());
        assertEquals(2, testReport.getTestCounters().getTotal());
        assertEquals(1, testReport.getTestCounters().getPassed());
        assertEquals(1, testReport.getTestCounters().getFailed());
    }

    @org.junit.jupiter.api.Test
    void testReport_builder_with_subReports_undefined() {
        TestRunBuilder testRunBuilder = createTestRunBuilder();
        testRunBuilder.addStepRun(new StepRunBuilder(validatorBuilderFactory)
                .setStepId("stepId")
                .setType("validation")
                .setResult(StepResult.FAILED));
        testRunBuilder.addStepRun(new StepRunBuilder(validatorBuilderFactory)
                .setStepId("stepId")
                .setType("validation")
                .setResult(StepResult.UNDEFINED));

        TestReportBuilder subReportBuilder = createTestReportBuilder()
                .addTestRun(testRunBuilder);

        TestReport testReport = new TestReportBuilder(validatorBuilderFactory)
                .setUuid()
                .setDateTime(new Date())
                .addTestRun(createTestRunBuilder())
                .addSubReport(subReportBuilder)
                .build();

        assertEquals(Result.UNDEFINED, testReport.getResult());
        assertEquals(2, testReport.getTestCounters().getTotal());
        assertEquals(1, testReport.getTestCounters().getPassed());
        assertEquals(1, testReport.getTestCounters().getUndefined());
    }

    @org.junit.jupiter.api.Test
    void testReport_builder_with_subReports_error() {
        TestRunBuilder testRunBuilder = createTestRunBuilder();
        testRunBuilder.addStepRun(new StepRunBuilder(validatorBuilderFactory)
                .setStepId("stepId")
                .setType("validation")
                .setResult(StepResult.FAILED));
        testRunBuilder.addUnexpectedError(new UnexpectedErrorBuilder(validatorBuilderFactory)
                .setName("error")
                .setMessage("error"));

        TestReportBuilder subReportBuilder = createTestReportBuilder()
                .addTestRun(testRunBuilder);

        TestReport testReport = new TestReportBuilder(validatorBuilderFactory)
                .setUuid()
                .setDateTime(new Date())
                .addTestRun(createTestRunBuilder())
                .addSubReport(subReportBuilder)
                .build();

        assertEquals(Result.UNDEFINED, testReport.getResult());
        assertEquals(2, testReport.getTestCounters().getTotal());
        assertEquals(1, testReport.getTestCounters().getPassed());
        assertEquals(1, testReport.getTestCounters().getUnexpectedErrors());
    }

    @org.junit.jupiter.api.Test
    void testReport_builder_with_run_failed() {
        TestRunBuilder testRunBuilder = createTestRunBuilder();
        testRunBuilder.addStepRun(new StepRunBuilder(validatorBuilderFactory)
                .setStepId("stepId")
                .setType("validation")
                .setResult(StepResult.FAILED));

        TestReportBuilder subReportBuilder = createTestReportBuilder();
        subReportBuilder.addTestRun(createTestRunBuilder());

        TestReport testReport = new TestReportBuilder(validatorBuilderFactory)
                .setUuid()
                .setDateTime(new Date())
                .addTestRun(testRunBuilder)
                .addSubReport(subReportBuilder)
                .build();

        assertEquals(Result.FAILED, testReport.getResult());
        assertEquals(2, testReport.getTestCounters().getTotal());
        assertEquals(1, testReport.getTestCounters().getPassed());
        assertEquals(1, testReport.getTestCounters().getFailed());
    }

    private TestRunBuilder createTestRunBuilder() {
        return new TestRunBuilder(validatorBuilderFactory)
                .setDateTime(new Date())
                .setTest(new TestBuilder(validatorBuilderFactory).setId("testId"))
                .addStepRun(new StepRunBuilder(validatorBuilderFactory)
                        .setStepId("stepId")
                        .setType("validation")
                        .setResult(StepResult.PASSED)
                );
    }

    private TestReportBuilder createTestReportBuilder() {
        return new TestReportBuilder(validatorBuilderFactory)
                .setUuid()
                .setDateTime(new Date());
    }
}
