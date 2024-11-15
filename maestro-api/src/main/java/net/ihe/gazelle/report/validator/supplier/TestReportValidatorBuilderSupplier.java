package net.ihe.gazelle.report.validator.supplier;

import net.ihe.gazelle.framework.modelvalidator.domain.AbstractValidatorBuilderSupplier;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilder;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.report.model.*;

public class TestReportValidatorBuilderSupplier extends AbstractValidatorBuilderSupplier<TestReport> {

    public TestReportValidatorBuilderSupplier(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
    }

    @Override
    protected Class<? extends TestReport> getType() {
        return TestReport.class;
    }

    @Override
    protected ValidatorBuilder<TestReport> supplyRulesAndMembers(ValidatorBuilder<TestReport> validatorBuilder) {
        return validatorBuilder
                .addRule("uuidValid", TestReport::isUuidValid, "Uuid must be set and not empty")
                .addRule("dateTimeValid", TestReport::isDateTimeValid, "DateTime must be set and not in the future")
                .addRule("resultValid", TestReport::isResultValid, "Result must be set and reflect the global result of the test report")
                .addRule("testSuiteNameValid", TestReport::isTestSuiteNameValid, "TestSuite name must be not empty if present")
                .addRule("testCountersValid", TestReport::isTestCountersValid, "TestCounters must be set and reflect the number of contained testRuns")
                .addRule("noteValid", TestReport::isNoteValid, "Note must be not empty if present")
                .addRule("urlToTestSuiteResultValid", TestReport::isUrlToTestSuiteResultValid, "UrlToTestResult must be not empty if present")
                .addRule("atLeastOneResult", TestReport::hasAtLeastOneResult, "A report must have at least on result")
                .addMember("testService", TestReport::getTestService, TestService.class)
                .addMember("systemsUnderTest", TestReport::getSystemsUnderTest, SystemUnderTest.class)
                .addMember("testCounters", TestReport::getTestCounters, TestCounters.class)
                .addMember("subReports", TestReport::getSubReports, TestReport.class)
                .addMember("testRuns", TestReport::getTestRunReports, TestRunReport.class)
                .addMember("unexpectedErrors", TestReport::getUnexpectedErrors, UnexpectedError.class);
    }
}
