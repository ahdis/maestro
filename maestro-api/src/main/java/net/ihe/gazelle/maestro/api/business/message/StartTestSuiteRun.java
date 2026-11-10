/*
 * Copyright 2025-2026 IHE International.
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

import net.ihe.gazelle.lang.Command;
import net.ihe.gazelle.maestro.api.business.test.Test;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;

import java.io.Serial;
import java.time.Duration;
import java.time.Instant;

/**
 * A message to launch test set execution
 */
@Command
public class StartTestSuiteRun implements Message {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The session id of the execution
     */
    private String sessionId;

    /**
     * The test suite run to be executed
     */
    private TestSuiteRun testSuiteRun;

    /**
     * The test session id of the execution
     */
    private String testSessionId;

    /**
     * Whether to persist the test suite run execution or not
     */
    private boolean persist;

    /**
     * The expiration timestamp for the test suite run execution
     */
    private Instant expirationTimestamp;

    /**
     * Default constructor
     */
    public StartTestSuiteRun() {
        this(null, null);
    }

    /**
     * Initializes a new instance of the StartTestSuiteRun class with the specified session ID and test suite run details.
     *
     * @param sessionId    the unique identifier for the session during which the test suite run will be executed
     * @param testSuiteRun the details of the test suite run to be executed
     */
    public StartTestSuiteRun(String sessionId, TestSuiteRun testSuiteRun) {
        this.sessionId = sessionId;
        this.testSuiteRun = testSuiteRun;
    }

    /**
     * Retrieves the session ID associated with the test suite execution.
     *
     * @return the session ID as a string, or null if no session ID is set.
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Sets the session ID associated with the test suite execution.
     *
     * @param sessionId the unique identifier for the session during which the test suite run will be executed
     * @return the current instance of {@code StartTestSuiteRun} for method chaining
     */
    public StartTestSuiteRun setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    /**
     * Retrieves the details of the test suite run to be executed.
     *
     * @return the {@code TestSuiteRun} object representing the details of the test suite run.
     */
    public TestSuiteRun getTestSuiteRun() {
        return testSuiteRun;
    }

    /**
     * Sets the test suite run details to be executed.
     *
     * @param testSuiteRun the {@code TestSuiteRun} object representing the details of the test suite run
     * @return the current instance of {@code StartTestSuiteRun} for method chaining
     */
    public StartTestSuiteRun setTestSuiteRun(TestSuiteRun testSuiteRun) {
        this.testSuiteRun = testSuiteRun;
        return this;
    }

    /**
     * Retrieves the test session ID associated with the execution.
     *
     * @return the test session ID as a string, or null if no test session ID is set.
     */
    public String getTestSessionId() {
        return testSessionId;
    }

    /**
     * Sets the test session ID associated with the execution.
     *
     * @param testSessionId the test session ID to be set.
     * @return the current instance of {@code StartTestRun} for method chaining.
     */
    public StartTestSuiteRun setTestSessionId(String testSessionId) {
        this.testSessionId = testSessionId;
        return this;
    }

    /**
     * Indicates whether persistence is enabled for the current test suite run instance.
     *
     * @return {@code true} if persistence is enabled; {@code false} otherwise.
     */
    public boolean isPersist() {
        return persist;
    }

    /**
     * Sets whether persistence is enabled for the current test suite run instance.
     *
     * @param persist a boolean indicating whether persistence should be enabled (true) or disabled (false).
     * @return the current instance of {@code StartTestSuiteRun} for method chaining.
     */
    public StartTestSuiteRun setPersist(boolean persist) {
        this.persist = persist;
        return this;
    }

    /**
     * Retrieves the expiration timestamp associated with the test suite run execution.
     *
     * @return the expiration timestamp as an {@code Instant}, or null if no expiration timestamp is set.
     */
    public Instant getExpirationTimestamp() {
        return expirationTimestamp != null ? expirationTimestamp : getTimeout(null);
    }

    /**
     * Sets the timeout value, in seconds, for the test suite run execution.
     *
     * @param duration the timeout duration to be set.
     * @return the current instance of {@code StartTestRun} for method chaining.
     */
    public StartTestSuiteRun setExpirationTimestamp(Duration duration) {
        this.expirationTimestamp = getTimeout(duration);
        return this;
    }

    private Instant getTimeout(Duration duration) {
        if (duration != null) {
            return Instant.now().plus(duration);
        } else {
            return Instant.now().plusMillis(Test.TEST_DEFAULT_TIMEOUT_MILLIS);
        }
    }
}
