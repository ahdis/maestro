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

package net.ihe.gazelle.maestro.api.business.testreport.validator;

import net.ihe.gazelle.framework.modelvalidator.business.ObjectResult;
import net.ihe.gazelle.maestro.api.business.testreport.Result;
import net.ihe.gazelle.maestro.api.business.testreport.TestReportSummary;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestReportSummaryValidatorTest {

    private final TestReportSummaryValidator validator = new TestReportSummaryValidator();

    @Test
    void shouldValidateSummaryWithMinimalInformation() {
        TestReportSummary summary = new TestReportSummary()
                .setId("summary-id")
                .setDateTime(Instant.now().minusSeconds(1))
                .setResult(Result.PASSED);

        ObjectResult result = validator.validate(summary);

        assertTrue(result.isValid(), () -> "Unexpected failures: " + result);
    }

    @Test
    void shouldRejectSummaryWithoutIdentifier() {
        TestReportSummary summary = new TestReportSummary()
                .setDateTime(Instant.now().minusSeconds(1))
                .setResult(Result.PASSED);

        ObjectResult result = validator.validate(summary);

        assertFalse(result.isValid());
    }
}

