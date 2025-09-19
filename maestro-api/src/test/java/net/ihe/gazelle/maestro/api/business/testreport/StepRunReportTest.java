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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StepRunReportTest {

    @Test
    void stepRun_valid() {
        StepRunReport stepRunReport = new StepRunReport("id", "type", StepResult.PASSED)
                .addOutput(new Output("name", "reference", "format"));
        assertEquals("id", stepRunReport.getStepId());
        assertEquals("type", stepRunReport.getType());
        assertEquals(StepResult.PASSED, stepRunReport.getResult());
        assertEquals(1, stepRunReport.getOutputs().size());
        assertTrue(stepRunReport.isValid());
    }

    @Test
    void stepRun_minimum_valid() {
        StepRunReport stepRunReport = new StepRunReport()
                .setStepId("id")
                .setType("type")
                .setResult(StepResult.PASSED);
        assertEquals("id", stepRunReport.getStepId());
        assertEquals("type", stepRunReport.getType());
        assertEquals(StepResult.PASSED, stepRunReport.getResult());
        assertTrue(stepRunReport.isValid());
    }

    @Test
    void stepRun_not_valid_without_type() {
        StepRunReport stepRunReport = new StepRunReport().setStepId("id");
        assertEquals(StepResult.UNDEFINED, stepRunReport.getResult());
        assertFalse(stepRunReport.isValid());
    }

    @Test
    void stepRun_not_valid_without_result() {
        StepRunReport stepRunReport = new StepRunReport()
                .setStepId("id")
                .setType("type")
                .addOutput(new Output("name", "reference", "format"));
        stepRunReport.setResult(null);
        assertFalse(stepRunReport.isValid());
    }
}
