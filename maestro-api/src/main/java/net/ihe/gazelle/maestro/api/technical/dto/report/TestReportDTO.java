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

package net.ihe.gazelle.maestro.api.technical.dto.report;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import net.ihe.gazelle.errorhandling.technical.UnexpectedErrorDTO;
import net.ihe.gazelle.maestro.api.business.testreport.Result;
import net.ihe.gazelle.maestro.api.business.testreport.TestReport;
import net.ihe.gazelle.modelmarshaller.technical.dto.DTO;

import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "modelVersion",
        "uuid",
        "date",
        "testService",
        "systemsUnderTest",
        "status",
        "testSuiteName",
        "testCounters",
        "note",
        "urlToTestSuiteResult",
        "testRunReports",
        "subReports",
        "unexpectedErrors"
})
public class TestReportDTO implements DTO<TestReport> {

    public static final String ISO_8601_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSX";

    @JsonIgnore
    private final TestReport testReport;

    public TestReportDTO() {
        this(new TestReport());
    }

    public TestReportDTO(TestReport testReport) {
         this.testReport = testReport;
    }

    @Override
    @JsonIgnore
    public TestReport getBusinessObject() {
        return testReport;
    }

    @JsonGetter(value = "reportVersion")
    public String getReportVersion() {
        return getBusinessObject().getReportVersion();
    }

    @JsonSetter(value = "reportVersion")
    public void setReportVersion(String reportVersion) {
        // do nothing
    }

    @JsonGetter(value = "uuid")
    public String getUuid() {
        return getBusinessObject().getUuid();
    }

    @JsonSetter(value = "uuid")
    public void setUuid(String uuid) {
        getBusinessObject().setUuid(uuid);
    }

    @JsonGetter(value = "date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ISO_8601_DATETIME_PATTERN) // ISO 8601
    public Date getDateTime() {
        return getBusinessObject().getDateTime();
    }

    @JsonSetter(value = "date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ISO_8601_DATETIME_PATTERN) // ISO 8601
    public void setDateTime(Date dateTime) {
        getBusinessObject().setDateTime(dateTime);
    }

    @JsonGetter(value = "testService")
    public TestServiceDTO getTestService() {
        if (testReport.getTestService() != null) {
            return new TestServiceDTO(testReport.getTestService());
        }
        return null;
    }

    @JsonSetter(value = "testService")
    public void setTestService(TestServiceDTO testServiceDTO) {
        getBusinessObject().setTestService(testServiceDTO.getBusinessObject());
    }

    @JsonGetter(value = "systemsUnderTest")
    public List<SystemUnderTestDTO> getSystemsUnderTest() {
        return getBusinessObject()
                .getSystemsUnderTest()
                .stream()
                .map(SystemUnderTestDTO::new)
                .toList();
    }

    @JsonSetter(value = "systemsUnderTest")
    public void setSystemsUnderTest(List<SystemUnderTestDTO> systemsUnderTest) {
        getBusinessObject().setSystemsUnderTest(
                systemsUnderTest.stream().map(SystemUnderTestDTO::getBusinessObject).toList()
        );
    }

    @JsonGetter(value = "status")
    public String getResult() {
        return getBusinessObject().getResult().name();
    }

    @JsonSetter(value = "status")
    public void setResult(String result) {
        getBusinessObject().setResult(Result.valueOf(result));
    }

    @JsonGetter(value = "testSuiteName")
    public String getTestSuiteName() {
        return getBusinessObject().getTestSuiteName();
    }

    @JsonSetter(value = "testSuiteName")
    public void setTestSuiteName(String testSuiteName) {
        getBusinessObject().setTestSuiteName(testSuiteName);
    }

    @JsonGetter(value = "testCounters")
    public TestCountersDTO getTestCounters() {
        return getBusinessObject().getTestCounters() != null
                ? new TestCountersDTO(getBusinessObject().getTestCounters())
                : new TestCountersDTO();
    }

    @JsonSetter(value = "testCounters")
    public void setTestCounters(TestCountersDTO testCounters) {
        getBusinessObject().setTestCounters(testCounters.getBusinessObject());
    }

    @JsonGetter(value = "note")
    public String getNote() {
        return getBusinessObject().getNote();
    }

    @JsonSetter(value = "note")
    public void setNote(String note) {
        getBusinessObject().setNote(note);
    }

    @JsonGetter(value = "urlToTestSuiteResult")
    public String getUrlToTestSuiteResult() {
        return getBusinessObject().getUrlToTestSuiteResult();
    }

    @JsonSetter(value = "urlToTestSuiteResult")
    public void setUrlToTestSuiteResult(String urlToTestSuiteResult) {
        getBusinessObject().setUrlToTestSuiteResult(urlToTestSuiteResult);
    }

    @JsonGetter(value = "testRunReports")
    public List<TestRunReportDTO> getTestRunReports() {
        return getBusinessObject()
                .getTestRunReports()
                .stream()
                .map(TestRunReportDTO::new)
                .toList();
    }

    @JsonSetter(value = "testRunReports")
    public void setTestRunReports(List<TestRunReportDTO> testRunReports) {
        getBusinessObject().setTestRunReports(
                testRunReports.stream().map(TestRunReportDTO::getBusinessObject).toList()
        );
    }

    @JsonGetter(value = "subReports")
    public List<TestReportDTO> getSubReports() {
        return getBusinessObject()
                .getSubReports()
                .stream()
                .map(TestReportDTO::new)
                .toList();
    }

    @JsonSetter(value = "subReports")
    public void setSubReports(List<TestReportDTO> subReports) {
        getBusinessObject().setSubReports(
                subReports.stream().map(TestReportDTO::getBusinessObject).toList()
        );
    }

    @JsonGetter(value = "unexpectedErrors")
    public List<UnexpectedErrorDTO> getUnexpectedErrors() {
        return getBusinessObject()
                .getUnexpectedErrors()
                .stream()
                .map(UnexpectedErrorDTO::new)
                .toList();
    }

    @JsonSetter(value = "unexpectedErrors")
    public void setUnexpectedErrors(List<UnexpectedErrorDTO> unexpectedErrors) {
        getBusinessObject().setUnexpectedErrors(
                unexpectedErrors.stream().map(UnexpectedErrorDTO::getBusinessObject).toList()
        );
    }
}
