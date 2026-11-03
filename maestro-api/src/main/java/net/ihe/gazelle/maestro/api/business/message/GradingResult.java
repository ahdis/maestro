/*
 * Copyright 2026 IHE International.
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

package net.ihe.gazelle.maestro.api.business.message;

import net.ihe.gazelle.maestro.api.business.testreport.StepResult;

/**
 * Represents the result of a grading process or operation, capturing the
 * outcome of the grading evaluation.
 * <p>
 * The {@code GradingResult} enum provides predefined constants to
 * communicate the result of the grading process. Each status conveys
 * a specific state that can be used to interpret the outcome of an
 * operation.
 */
public enum GradingResult {

    /**
     * Indicates that the grading process requires additional evidence or
     * information to complete.
     */
    REQUEST_EVIDENCE,

    /**
     * Indicates that the grading process has passed validation
     * or met the required criteria.
     */
    PASSED,

    /**
     * Indicates that the grading process has failed validation
     * or did not meet the required criteria.
     */
    FAILED;

    /** String constant for {@link #REQUEST_EVIDENCE}. */
    public static final String RESULT_REQUEST_EVIDENCE = "REQUEST_EVIDENCE";

    /** String constant for {@link #PASSED}. */
    public static final String RESULT_PASSED = "PASSED";

    /** String constant for {@link #FAILED}. */
    public static final String RESULT_FAILED = "FAILED";

    public StepResult toStepResult() {
        if (this == REQUEST_EVIDENCE) {
            return StepResult.UNDEFINED;
        }
        return StepResult.valueOf(this.name());
    }

}
