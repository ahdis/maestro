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

package net.ihe.gazelle.maestro.api.business.test;

/**
 * Enumeration of grading methods for tests.
 */
public enum GradingMethod {

    /**
     * Indicates that a test is being evaluated by the user himself.
     */
    SELF_ASSESSED,

    /**
     * Indicates that a test shall be evaluated by a monitor.
     */
    MONITORED,

    /**
     * Indicates that a test is evaluated automatically by the result of its steps.
     */
    AUTOMATED;

    /** String constant for {@link #SELF_ASSESSED}. */
    public static final String GRADING_SELF_ASSESSED = "SELF_ASSESSED";

    /** String constant for {@link #MONITORED}. */
    public static final String GRADING_MONITORED = "MONITORED";

    /** String constant for {@link #AUTOMATED}. */
    public static final String GRADING_AUTOMATED = "AUTOMATED";
}
