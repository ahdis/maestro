/*
 * Copyright 2025 IHE International.
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

import net.ihe.gazelle.lang.UnexpectedError;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class TestRunReportTest {

    @Test
    void testRun_valid() {
        TestRunReport testRunReport = buildTestRun();
        assertEquals("id", testRunReport.getRunId());
        assertEquals(Result.UNDEFINED, testRunReport.getResult());
        assertEquals("id", testRunReport.getTest().getId());
        assertEquals(1, testRunReport.getInputs().size());
        assertEquals(1, testRunReport.getOutputs().size());
        assertEquals(1, testRunReport.getStepRunReports().size());
        assertEquals("urlToTestRun", testRunReport.getUrlToTestRun());
        assertEquals(0, testRunReport.getUnexpectedErrors().size());
        testRunReport.computeResult();
        assertTrue(testRunReport.isValid());
    }

    @Test
    void testRun_valid_compute_result() {
        TestRunReport testRunReport = buildTestRun();
        testRunReport.computeResult();
        assertEquals(Result.PASSED, testRunReport.getResult());
        testRunReport.addStepRunReport(new StepRunReport("id", "type", StepResult.FAILED));
        testRunReport.computeResult();
        assertEquals(Result.FAILED, testRunReport.getResult());
        testRunReport.addStepRunReport(new StepRunReport("id", "type", StepResult.UNDEFINED));
        testRunReport.computeResult();
        assertEquals(Result.UNDEFINED, testRunReport.getResult());
        assertTrue(testRunReport.isValid());
    }

    @Test
    void testRun_valid_compute_result_with_done() {
        TestRunReport testRunReport = buildTestRun();
        testRunReport.computeResult();
        assertEquals(Result.PASSED, testRunReport.getResult());
        testRunReport.addStepRunReport(new StepRunReport("id", "type", StepResult.DONE));
        testRunReport.computeResult();
        assertEquals(Result.PASSED, testRunReport.getResult());
        assertTrue(testRunReport.isValid());
    }

    @Test
    void testRun_valid_compute_result_with_errors() {
        TestRunReport testRunReport = buildTestRun();
        testRunReport.computeResult();
        assertEquals(Result.PASSED, testRunReport.getResult());
        testRunReport.addUnexpectedError(
                new UnexpectedError()
                        .setName("name")
                        .setMessage("message")
        );
        assertEquals(1, testRunReport.getUnexpectedErrors().size());
        testRunReport.computeResult();
        assertEquals(Result.UNDEFINED, testRunReport.getResult());
        assertTrue(testRunReport.isValid());
    }

    @Test
    void testRun_valid_with_no_result() {
        TestRunReport testRunReport = new TestRunReport()
                .setDateTime(new Date())
                .setTest(new net.ihe.gazelle.maestro.api.business.testreport.Test("id"));
        testRunReport.computeResult();
        assertEquals(Result.UNDEFINED, testRunReport.getResult());
        assertTrue(testRunReport.isValid());
    }

    @Test
    void testRun_not_valid() {
        TestRunReport testRunReport = new TestRunReport();
        assertFalse(testRunReport.isValid());
    }

    private TestRunReport buildTestRun() {
        return new TestRunReport()
                .setRunId("id")
                .setDateTime(new Date())
                .setTest(new net.ihe.gazelle.maestro.api.business.testreport.Test("id"))
                .addInput(new Item("input", "reference"))
                .addOutput(new Item("output", "reference"))
                .addStepRunReport(new StepRunReport("id", "type", StepResult.PASSED))
                .setUrlToTestRun("urlToTestRun");
    }
}
