package net.ihe.gazelle.report.builder;

import net.ihe.gazelle.framework.modelvalidator.domain.AbstractBuilder;
import net.ihe.gazelle.framework.modelvalidator.domain.BuilderVisitor;
import net.ihe.gazelle.framework.modelvalidator.domain.ValidatorBuilderFactory;
import net.ihe.gazelle.report.model.TestReport;
import net.ihe.gazelle.report.visitor.TestReportBuilderVisitor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TestReportBuilder extends AbstractBuilder<TestReport> {

    private String reportVersion;
    private String uuid;
    private Date dateTime;
    private TestServiceBuilder testService;
    private List<SystemUnderTestBuilder> systemsUnderTest;
    private String testSuiteName;
    private String note;
    private String urlToTestSuiteResult;
    private List<TestReportBuilder> subReports;
    private List<TestRunReportBuilder> testRunReports;
    private List<UnexpectedErrorBuilder> unexpectedErrors;

    public TestReportBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
        systemsUnderTest = new ArrayList<>();
        subReports = new ArrayList<>();
        testRunReports = new ArrayList<>();
        unexpectedErrors = new ArrayList<>();
    }

    public TestReportBuilder setReportVersion() {
        this.reportVersion = TestReport.REPORT_VERSION;
        return this;
    }

    public TestReportBuilder setUuid() {
        this.uuid = UUID.randomUUID().toString();
        return this;
    }

    public TestReportBuilder setDateTime(Date dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public TestReportBuilder setTestService(TestServiceBuilder testService) {
        this.testService = testService;
        return this;
    }

    public TestReportBuilder setSystemsUnderTest(List<SystemUnderTestBuilder> systemsUnderTest) {
        this.systemsUnderTest = systemsUnderTest;
        return this;
    }

    public TestReportBuilder addSystemUnderTest(SystemUnderTestBuilder systemUnderTest) {
        this.systemsUnderTest.add(systemUnderTest);
        return this;
    }

    public TestReportBuilder addSystemsUnderTest(List<SystemUnderTestBuilder> systemsUnderTest) {
        this.systemsUnderTest.addAll(systemsUnderTest);
        return this;
    }

    public TestReportBuilder setTestSuiteName(String testSuiteName) {
        this.testSuiteName = testSuiteName;
        return this;
    }

    public TestReportBuilder setNote(String note) {
        this.note = note;
        return this;
    }

    public TestReportBuilder setUrlToTestSuiteResult(String urlToTestSuiteResult) {
        this.urlToTestSuiteResult = urlToTestSuiteResult;
        return this;
    }

    public TestReportBuilder setSubReports(List<TestReportBuilder> subReports) {
        this.subReports = subReports;
        return this;
    }

    public TestReportBuilder addSubReport(TestReportBuilder subReport) {
        this.subReports.add(subReport);
        return this;
    }

    public TestReportBuilder addSubReports(List<TestReportBuilder> subReports) {
        this.subReports.addAll(subReports);
        return this;
    }

    public TestReportBuilder setTestRunReports(List<TestRunReportBuilder> testRunReports) {
        this.testRunReports = testRunReports;
        return this;
    }

    public TestReportBuilder addTestRunReport(TestRunReportBuilder testRunReport) {
        this.testRunReports.add(testRunReport);
        return this;
    }

    public TestReportBuilder addTestRunReports(List<TestRunReportBuilder> testRunReports) {
        this.testRunReports.addAll(testRunReports);
        return this;
    }

    public TestReportBuilder setUnexpectedErrors(List<UnexpectedErrorBuilder> unexpectedErrors) {
        this.unexpectedErrors = unexpectedErrors;
        return this;
    }

    public TestReportBuilder addUnexpectedError(UnexpectedErrorBuilder unexpectedError) {
        this.unexpectedErrors.add(unexpectedError);
        return this;
    }

    public TestReportBuilder addUnexpectedErrors(List<UnexpectedErrorBuilder> unexpectedErrors) {
        this.unexpectedErrors.addAll(unexpectedErrors);
        return this;
    }

    @Override
    protected BuilderVisitor getBuilderVisitor() {
        return TestReportBuilderVisitor.INSTANCE;
    }

    @Override
    protected Class<? extends TestReport> getParameterClass() {
        return TestReport.class;
    }

    @Override
    protected void make() {
        this.getObjectToBuild()
                .setReportVersion(reportVersion)
                .setUuid(uuid)
                .setDateTime(dateTime)
                .setTestService(AbstractBuilder.buildField(testService))
                .setSystemsUnderTest(
                        !subReports.isEmpty() ? systemsUnderTest.stream()
                                .map(AbstractBuilder::buildField)
                                .toList() : new ArrayList<>()
                )
                .setTestSuiteName(testSuiteName)
                .setNote(note)
                .setUrlToTestSuiteResult(urlToTestSuiteResult)
                .setSubReports(
                        !subReports.isEmpty() ? subReports.stream()
                                .map(AbstractBuilder::buildField)
                                .toList() : new ArrayList<>()
                )
                .setTestRunReports(
                        !testRunReports.isEmpty() ? testRunReports.stream()
                                .map(AbstractBuilder::buildField)
                                .toList() : new ArrayList<>()
                )
                .setUnexpectedErrors(
                        !unexpectedErrors.isEmpty() ? unexpectedErrors.stream()
                                .map(AbstractBuilder::buildField)
                                .toList() : new ArrayList<>()
                );
        this.getObjectToBuild().computeResult();
        this.getObjectToBuild().computeCounters();
    }
}
