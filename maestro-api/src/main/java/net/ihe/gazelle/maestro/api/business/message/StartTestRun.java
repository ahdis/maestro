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
import net.ihe.gazelle.maestro.api.business.testrun.TestRun;

import java.io.Serial;
import java.time.Instant;

/**
 * A message to start TestRun Execution
 */
@Command
public class StartTestRun implements Message {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The session id of the execution
     */
    private String sessionId;

    /**
     * The test run to be executed
     */
    private TestRun testRun;

    /**
     * The test session id of the execution
     */
    private String testSessionId;

    /**
     * Whether to persist the test run execution or not
     */
    private boolean persist;

    /**
     * The expiration timestamp for the test run execution
     */
    private Instant expirationTimestamp;

    /**
     * Default constructor
     */
    public StartTestRun() {
        this(null, null);
    }

    /**
     * Constructs a new {@code StartTestRun} instance with the specified session ID and test run.
     *
     * @param sessionId the session ID associated with the test run execution.
     * @param testRun   the {@code TestRun} instance representing the test to be executed.
     */
    public StartTestRun(String sessionId, TestRun testRun) {
        this.sessionId = sessionId;
        this.testRun = testRun;
    }

    /**
     * Retrieves the session ID associated with the test run execution.
     *
     * @return the session ID as a string, or null if no session ID is set.
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Sets the session ID associated with the test run execution.
     *
     * @param sessionId the session ID to be set.
     * @return the current instance of {@code StartTestRun} for method chaining.
     */
    public StartTestRun setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    /**
     * Retrieves the {@code TestRun} instance associated with this execution.
     *
     * @return the {@code TestRun} to be executed, or null if no {@code TestRun} is set.
     */
    public TestRun getTestRun() {
        return testRun;
    }

    /**
     * Sets the {@code TestRun} instance to be executed.
     *
     * @param testRun the {@code TestRun} instance representing the test to be executed.
     * @return the current instance of {@code StartTestRun} for method chaining.
     */
    public StartTestRun setTestRun(TestRun testRun) {
        this.testRun = testRun;
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
    public StartTestRun setTestSessionId(String testSessionId) {
        this.testSessionId = testSessionId;
        return this;
    }

    /**
     * Indicates whether persistence is enabled for the current test run configuration.
     *
     * @return true if persistence is enabled, false otherwise.
     */
    public boolean isPersist() {
        return persist;
    }

    /**
     * Sets whether persistence is enabled for the current test run configuration.
     *
     * @param persist a boolean indicating whether persistence should be enabled (true) or disabled (false).
     * @return the current instance of {@code StartTestRun} for method chaining.
     */
    public StartTestRun setPersist(boolean persist) {
        this.persist = persist;
        return this;
    }

    /**
     * Retrieves the expiration timestamp for the test run execution.
     *
     * @return the expiration timestamp as an {@code Instant}, or null if no expiration timestamp is set.
     */
    public Instant getExpirationTimestamp() {
        return expirationTimestamp != null ? expirationTimestamp : getTimeout(null);
    }

    /**
     * Sets the expiration timestamp for the test run execution.
     *
     * @param timeoutSeconds the expiration timestamp to be set as an {@code Instant}.
     *                            It can be null to indicate no expiration timestamp is specified.
     * @return the current instance of {@code StartTestRun} for method chaining.
     */
    public StartTestRun setExpirationTimestamp(Long timeoutSeconds) {
        this.expirationTimestamp = getTimeout(timeoutSeconds);
        return this;
    }

    /**
     * Sets the expiration timestamp for the test run execution.
     *
     * @param expirationTimestamp the expiration timestamp to be set as an {@code Instant}.
     *                            It can be null to indicate no expiration timestamp is specified.
     * @return the current instance of {@code StartTestRun} for method chaining.
     */
    public StartTestRun setExpirationTimestamp(Instant expirationTimestamp) {
        this.expirationTimestamp = expirationTimestamp;
        return this;
    }

    private Instant getTimeout(Long timeoutSeconds) {
        if (timeoutSeconds != null) {
            return Instant.now().plusSeconds(timeoutSeconds);
        } else {
            return Instant.now().plusMillis(Test.TEST_DEFAULT_TIMEOUT_MILLIS);
        }
    }
}
