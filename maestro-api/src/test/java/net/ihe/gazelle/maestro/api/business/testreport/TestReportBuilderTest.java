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

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestReportBuilderTest {

    @org.junit.jupiter.api.Test
    void testReport_builder_minimum_valid() {
        TestReport testReport = new TestReportBuilder()
                .setDateTime(new Date())
                .addTestRunReport(new TestRunReportBuilder()
                        .setDateTime(new Date())
                        .setTest(
                                new TestBuilder()
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
        TestRunReportBuilder testRunReportBuilder = createTestRunBuilder();

        TestReportBuilder subReportBuilder = createTestReportBuilder()
                .addTestRunReport(testRunReportBuilder);

        TestReport testReport = new TestReportBuilder()
                .setDateTime(new Date())
                .addTestRunReport(createTestRunBuilder())
                .addSubReport(subReportBuilder)
                .build();

        assertEquals(Result.PASSED, testReport.getResult());
        assertTrue(testReport.isValid());
        assertEquals(2, testReport.getTestCounters().getTotal());
        assertEquals(2, testReport.getTestCounters().getPassed());
    }

    @org.junit.jupiter.api.Test
    void testReport_builder_with_subReports_failed() {
        TestRunReportBuilder testRunReportBuilder = createTestRunBuilder();
        testRunReportBuilder.addStepRunReport(new StepRunReportBuilder()
                .setStepId("stepId")
                .setType("validation")
                .setResult(StepResult.FAILED));

        TestReportBuilder subReportBuilder = createTestReportBuilder()
                .addTestRunReport(testRunReportBuilder);

        TestReport testReport = new TestReportBuilder()
                .setDateTime(new Date())
                .addTestRunReport(createTestRunBuilder())
                .addSubReport(subReportBuilder)
                .build();

        assertEquals(Result.FAILED, testReport.getResult());
        assertEquals(2, testReport.getTestCounters().getTotal());
        assertEquals(1, testReport.getTestCounters().getPassed());
        assertEquals(1, testReport.getTestCounters().getFailed());
    }

    @org.junit.jupiter.api.Test
    void testReport_builder_with_subReports_undefined() {
        TestRunReportBuilder testRunReportBuilder = createTestRunBuilder();
        testRunReportBuilder.addStepRunReport(new StepRunReportBuilder()
                .setStepId("stepId")
                .setType("validation")
                .setResult(StepResult.FAILED));
        testRunReportBuilder.addStepRunReport(new StepRunReportBuilder()
                .setStepId("stepId")
                .setType("validation")
                .setResult(StepResult.UNDEFINED));

        TestReportBuilder subReportBuilder = createTestReportBuilder()
                .addTestRunReport(testRunReportBuilder);

        TestReport testReport = new TestReportBuilder()
                .setDateTime(new Date())
                .addTestRunReport(createTestRunBuilder())
                .addSubReport(subReportBuilder)
                .build();

        assertEquals(Result.UNDEFINED, testReport.getResult());
        assertEquals(2, testReport.getTestCounters().getTotal());
        assertEquals(1, testReport.getTestCounters().getPassed());
        assertEquals(1, testReport.getTestCounters().getUndefined());
    }

    @org.junit.jupiter.api.Test
    void testReport_builder_with_subReports_error() {
        TestRunReportBuilder testRunReportBuilder = createTestRunBuilder();
        testRunReportBuilder.addStepRunReport(new StepRunReportBuilder()
                .setStepId("stepId")
                .setType("validation")
                .setResult(StepResult.FAILED));
        testRunReportBuilder.addUnexpectedError(new UnexpectedErrorBuilder()
                .setName("error")
                .setMessage("error"));
        testRunReportBuilder.addUnexpectedError(new UnexpectedErrorBuilder()
                .fromTrowable(new Exception("error")));

        TestReportBuilder subReportBuilder = createTestReportBuilder()
                .addTestRunReport(testRunReportBuilder);

        TestReport testReport = new TestReportBuilder()
                .setDateTime(new Date())
                .addTestRunReport(createTestRunBuilder())
                .addSubReport(subReportBuilder)
                .build();

        assertEquals(Result.UNDEFINED, testReport.getResult());
        assertEquals(2, testReport.getTestCounters().getTotal());
        assertEquals(1, testReport.getTestCounters().getPassed());
        assertEquals(2, testReport.getTestCounters().getUnexpectedErrors());
    }

    @org.junit.jupiter.api.Test
    void testReport_builder_with_run_failed() {
        TestRunReportBuilder testRunReportBuilder = createTestRunBuilder();
        testRunReportBuilder.addStepRunReport(new StepRunReportBuilder()
                .setStepId("stepId")
                .setType("validation")
                .setResult(StepResult.FAILED));

        TestReportBuilder subReportBuilder = createTestReportBuilder();
        subReportBuilder.addTestRunReport(createTestRunBuilder());

        TestReport testReport = new TestReportBuilder()
                .setDateTime(new Date())
                .addTestRunReport(testRunReportBuilder)
                .addSubReport(subReportBuilder)
                .build();

        assertEquals(Result.FAILED, testReport.getResult());
        assertEquals(2, testReport.getTestCounters().getTotal());
        assertEquals(1, testReport.getTestCounters().getPassed());
        assertEquals(1, testReport.getTestCounters().getFailed());
    }

    private TestRunReportBuilder createTestRunBuilder() {
        return new TestRunReportBuilder()
                .setDateTime(new Date())
                .setTest(new TestBuilder()
                        .setId("testId")
                        .setName("testName")
                        .setDescription("description")
                        .setVersion("1.0"))
                .setOutputs(new ArrayList<>())
                .addOutput(new ItemBuilder()
                        .setName("name")
                        .setReference("reference"))
                .setStepRunReports(new ArrayList<>())
                .addStepRunReport(new StepRunReportBuilder()
                        .setStepId("stepId")
                        .setType("validation")
                        .setResult(StepResult.PASSED)
                        .setUnexpectedErrors(new ArrayList<>())
                )
                .setUnexpectedErrors(new ArrayList<>());
    }

    private TestReportBuilder createTestReportBuilder() {
        return new TestReportBuilder()
                .setReportVersion()
                .setDateTime(new Date())
                .setTestService(new TestServiceBuilder()
                        .setServiceIdentification(new EntityIdentificationBuilder()
                                .setName("service")
                                .setVersion("1.0"))
                        .setDisclaimer("disclaimer"))
                .setSystemsUnderTest(new ArrayList<>())
                .addSystemUnderTest(new SystemUnderTestBuilder()
                        .setSystemIdentification(new EntityIdentificationBuilder()
                                .setName("system")
                                .setVersion("1.0"))
                        .setMacAddresses(new ArrayList<>())
                        .setMacAddresses(new ArrayList<>())
                        .addMacAddress("mac")
                        .setHostNames(new ArrayList<>())
                        .setHostNames(new ArrayList<>())
                        .addHostName("host")
                        .setIpAddresses(new ArrayList<>())
                        .setIpAddresses(new ArrayList<>())
                        .addIpAddress("ip"))
                .setTestRunReports(new ArrayList<>())
                .setSubReports(new ArrayList<>())
                .setUnexpectedErrors(new ArrayList<>());
    }
}
