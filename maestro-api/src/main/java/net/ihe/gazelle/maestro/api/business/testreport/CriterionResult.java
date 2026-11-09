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

package net.ihe.gazelle.maestro.api.business.testreport;

/**
 * Enumeration representing the grading result for a criteria.
 */
public enum CriterionResult {

    /**
     * Indicates that the user set passed grading for a criteria.
     */
    PASSED,

    /**
     * Indicates that the user set failed grading for a criteria.
     */
    FAILED,

    /**
     * Indicates that the user did not set any grading for a criteria.
     */
    NOT_VERIFIED;

    /** String constant for {@link #PASSED}. */
    public static final String RESULT_PASSED = "PASSED";

    /** String constant for {@link #FAILED}. */
    public static final String RESULT_FAILED = "FAILED";

    /** String constant for {@link #NOT_VERIFIED}. */
    public static final String RESULT_NOT_VERIFIED = "NOT_VERIFIED";
}
