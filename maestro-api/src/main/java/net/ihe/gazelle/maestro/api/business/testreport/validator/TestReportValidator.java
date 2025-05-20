package net.ihe.gazelle.maestro.api.business.testreport.validator;

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;

public class TestReportValidator extends AbstractValidator<TestReport> {

    private final TestServiceValidator testServiceValidator;
    private final SystemUnderTestValidator systemUnderTestValidator;
    private final TestReportValidator subReportValidator;
    private final TestRunReportValidator testRunReportValidator;
    private final UnexpectedErrorValidator unexpectedErrorValidator;

    public TestReportValidator() {
        super();
        testServiceValidator = new TestServiceValidator();
        systemUnderTestValidator = new SystemUnderTestValidator();
        subReportValidator = new TestReportValidator(this);
        testRunReportValidator = new TestRunReportValidator();
        unexpectedErrorValidator = new UnexpectedErrorValidator();
    }

    public TestReportValidator(TestReportValidator testReportValidator) {
        super();
        testServiceValidator = new TestServiceValidator();
        systemUnderTestValidator = new SystemUnderTestValidator();
        subReportValidator = testReportValidator;
        testRunReportValidator = new TestRunReportValidator();
        unexpectedErrorValidator = new UnexpectedErrorValidator();
    }

    public TestReportValidator(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
        testServiceValidator = new TestServiceValidator();
        systemUnderTestValidator = new SystemUnderTestValidator();
        subReportValidator = new TestReportValidator(validatorBuilderFactory, this);
        testRunReportValidator = new TestRunReportValidator();
        unexpectedErrorValidator = new UnexpectedErrorValidator();
    }

    public TestReportValidator(ValidatorBuilderFactory builderFactory, TestReportValidator testReportValidator) {
        super(builderFactory);
        testServiceValidator = new TestServiceValidator();
        systemUnderTestValidator = new SystemUnderTestValidator();
        subReportValidator = testReportValidator;
        testRunReportValidator = new TestRunReportValidator();
        unexpectedErrorValidator = new UnexpectedErrorValidator();
    }

    @Override
    protected ValidatorBuilder<? extends TestReport> instantiateValidatorBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        return validatorBuilderFactory.getBuilder(TestReport.class);
    }

    @Override
    protected void addRulesAndMembers(ValidatorBuilder<? extends TestReport> validatorBuilder) {
        validatorBuilder
                .addRule("uuidValid", TestReport::isUuidValid, "Uuid must be set and not empty")
                .addRule("dateTimeValid", TestReport::isDateTimeValid, "DateTime must be set and not in the future")
                .addRule("resultValid", TestReport::isResultValid, "Result must be set and reflect the global result of the test testreport")
                .addRule("testSuiteNameValid", TestReport::isTestSuiteNameValid, "TestSuite name must be not empty if present")
                .addRule("testCountersValid", TestReport::isTestCountersValid, "TestCounters must be set and reflect the number of contained testRuns")
                .addRule("noteValid", TestReport::isNoteValid, "Note must be not empty if present")
                .addRule("urlToTestSuiteResultValid", TestReport::isUrlToTestSuiteResultValid, "UrlToTestResult must be not empty if present")
                .addRule("atLeastOneResult", TestReport::hasAtLeastOneResult, "A testReport must have at least on result")
                .addMember("testService", TestReport::getTestService, testServiceValidator::getAssembledValidatorBuilder)
                .addMember("systemsUnderTest", TestReport::getSystemsUnderTest, systemUnderTestValidator::getAssembledValidatorBuilder)
                .addMember("subReports", TestReport::getSubReports, subReportValidator::getAssembledValidatorBuilder)
                .addMember("testRuns", TestReport::getTestRunReports, testRunReportValidator::getAssembledValidatorBuilder)
                .addMember("unexpectedErrors", TestReport::getUnexpectedErrors, unexpectedErrorValidator::getAssembledValidatorBuilder);

    }
}
