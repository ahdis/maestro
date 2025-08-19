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

import net.ihe.gazelle.framework.modelvalidator.business.ValidatorBuilderFactory;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractBuilder;
import net.ihe.gazelle.framework.modelvalidator.business.utils.AbstractValidator;
import net.ihe.gazelle.maestro.api.business.acl.AccessControlListBuilder;
import net.ihe.gazelle.maestro.api.business.testreport.validator.TestReportValidator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TestReportBuilder extends AbstractBuilder<TestReport> {

    private String reportVersion;
    private final String uuid;
    private Date dateTime;
    private TestServiceBuilder testService;
    private List<SystemUnderTestBuilder> systemsUnderTest;
    private String testSuiteName;
    private String note;
    private String urlToTestSuiteResult;
    private List<TestReportBuilder> subReports;
    private List<TestRunReportBuilder> testRunReports;
    private List<UnexpectedErrorBuilder> unexpectedErrors;
    private AccessControlListBuilder accessControlListBuilder;

    public TestReportBuilder() {
        super();
        uuid = UUID.randomUUID().toString();
        dateTime = new Date();
        systemsUnderTest = new ArrayList<>();
        subReports = new ArrayList<>();
        testRunReports = new ArrayList<>();
        unexpectedErrors = new ArrayList<>();
    }

    public TestReportBuilder(ValidatorBuilderFactory validatorBuilderFactory) {
        super(validatorBuilderFactory);
        uuid = UUID.randomUUID().toString();
        dateTime = new Date();
        systemsUnderTest = new ArrayList<>();
        subReports = new ArrayList<>();
        testRunReports = new ArrayList<>();
        unexpectedErrors = new ArrayList<>();
    }

    public TestReportBuilder setReportVersion() {
        this.reportVersion = TestReport.REPORT_VERSION;
        return this;
    }

    public TestReportBuilder setDateTime(Date dateTime) {
        this.dateTime = new Date(dateTime.getTime());
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

    public TestReportBuilder setTestRunReports(List<TestRunReportBuilder> testRunReports) {
        this.testRunReports = testRunReports;
        return this;
    }

    public TestReportBuilder addTestRunReport(TestRunReportBuilder testRunReport) {
        this.testRunReports.add(testRunReport);
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
                        !subReports.isEmpty() ? systemsUnderTest.stream()
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
                        !testRunReports.isEmpty() ? testRunReports.stream()
                                .map(AbstractBuilder::staticBuildWithoutValidation)
                                .toList() : new ArrayList<>()
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
