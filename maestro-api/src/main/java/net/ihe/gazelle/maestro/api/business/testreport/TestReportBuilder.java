/*
 * Copyright 2025 Kereval.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ihe.gazelle.maestro.api.business.testreport;

import net.ihe.gazelle.errorhandling.business.UnexpectedErrorBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.maestro.api.business.testreport.validator.TestReportValidator;
import net.ihe.gazelle.security.business.acl.AccessControlListBuilder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestReportBuilder extends AbstractBuilder<TestReport> {

    private String reportVersion;
    private final String uuid;
    private Instant dateTime;
    private TestServiceBuilder testService;
    private List<SystemUnderTestBuilder> systemsUnderTest;
    private String testSuiteName;
    private String note;
    private String urlToTestSuiteResult;
    private List<TestReportBuilder> subReports;
    private List<TestRunReport> testRunReports;
    private List<TestRunReportBuilder> testRunReportBuilders;
    private List<UnexpectedErrorBuilder> unexpectedErrors;
    private AccessControlListBuilder accessControlListBuilder;

    public TestReportBuilder() {
        super();
        uuid = UUID.randomUUID().toString();
        dateTime = Instant.now();
        systemsUnderTest = new ArrayList<>();
        subReports = new ArrayList<>();
        testRunReports = new ArrayList<>();
        testRunReportBuilders = new ArrayList<>();
        unexpectedErrors = new ArrayList<>();
    }

    public TestReportBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
        uuid = UUID.randomUUID().toString();
        dateTime = Instant.now();
        systemsUnderTest = new ArrayList<>();
        subReports = new ArrayList<>();
        testRunReports = new ArrayList<>();
        testRunReportBuilders = new ArrayList<>();
        unexpectedErrors = new ArrayList<>();
    }

    public TestReportBuilder setReportVersion() {
        this.reportVersion = TestReport.REPORT_VERSION;
        return this;
    }

    public TestReportBuilder setDateTime(Instant dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public TestReportBuilder setTestService(TestServiceBuilder testService) {
        this.testService = testService;
        return this;
    }

    public TestReportBuilder setSystemsUnderTest(List<SystemUnderTestBuilder> systemsUnderTest) {
        this.systemsUnderTest = new ArrayList<>(systemsUnderTest);
        return this;
    }

    public TestReportBuilder addSystemUnderTest(SystemUnderTestBuilder systemUnderTest) {
        this.systemsUnderTest.add(systemUnderTest);
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
        this.subReports = new ArrayList<>(subReports);
        return this;
    }

    public TestReportBuilder addSubReport(TestReportBuilder subReport) {
        this.subReports.add(subReport);
        return this;
    }

    public TestReportBuilder addTestRunReportBuilders(List<TestRunReportBuilder> testRunReports) {
        this.testRunReportBuilders.addAll(testRunReports);
        return this;
    }

    public TestReportBuilder addTestRunReports(List<TestRunReport> testRunReports) {
        this.testRunReports.addAll(testRunReports);
        return this;
    }

    public TestReportBuilder addTestRunReport(TestRunReportBuilder testRunReport) {
        this.testRunReportBuilders.add(testRunReport);
        return this;
    }

    public TestReportBuilder addTestRunReport(TestRunReport testRunReport) {
        this.testRunReports.add(testRunReport);
        return this;
    }

    public TestReportBuilder setUnexpectedErrors(List<UnexpectedErrorBuilder> unexpectedErrors) {
        this.unexpectedErrors = unexpectedErrors != null
                ? new ArrayList<>(unexpectedErrors)
                : new ArrayList<>();
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

    public TestReportBuilder setAccessControlList(AccessControlListBuilder accessControlListBuilder){
        this.accessControlListBuilder = accessControlListBuilder;
        return this;
    }

    @Override
    protected AbstractValidator<TestReport> instantiateValidator(ValidatorBuilderFactory validatorBuilderFactory) {
        return new TestReportValidator(validatorBuilderFactory);
    }

    @Override
    protected TestReport instantiate() {
        return new TestReport();
    }

    @Override
    protected void make(TestReport testReport) {
        testReport
                .setReportVersion(reportVersion)
                .setUuid(uuid)
                .setDateTime(dateTime)
                .setTestService(AbstractBuilder.staticBuildWithoutValidation(testService))
                .setSystemsUnderTest(
                        !systemsUnderTest.isEmpty() ? systemsUnderTest.stream()
                                .map(AbstractBuilder::staticBuildWithoutValidation)
                                .toList() : new ArrayList<>()
                )
                .setTestSuiteName(testSuiteName)
                .setNote(note)
                .setUrlToTestSuiteResult(urlToTestSuiteResult)
                .setSubReports(
                        !subReports.isEmpty() ? subReports.stream()
                                .map(AbstractBuilder::staticBuildWithoutValidation)
                                .toList() : new ArrayList<>()
                )
                .setTestRunReports(
                        !testRunReportBuilders.isEmpty() ? testRunReportBuilders.stream()
                                .map(AbstractBuilder::staticBuildWithoutValidation)
                                .toList() : new ArrayList<>()
                )
                .addTestRunReports(
                        !testRunReports.isEmpty() ? testRunReports : new ArrayList<>()
                )
                .setUnexpectedErrors(
                        !unexpectedErrors.isEmpty() ? unexpectedErrors.stream()
                                .map(AbstractBuilder::staticBuildWithoutValidation)
                                .toList() : new ArrayList<>()
                )
                .setAccessControlList(AbstractBuilder.staticBuildWithoutValidation(accessControlListBuilder))
        ;
        testReport.computeResult();
        testReport.computeCounters();
    }
}
