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

package net.ihe.gazelle.maestro.engine.business.context;

import net.ihe.gazelle.maestro.api.business.testreport.TestRunReport;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * TestSuiteSession is a structure used to store report builder from executed TestRun, testSet's id and reference to
 * this testSet.
 */
public class TestSuiteSession {

    private final TestSuiteRun testSuiteRun;
    private final TestRunCursor testRunCursor;
    private final List<TestRunReport> testRunReports;
    private Instant expirationTimestamp;

    /**
     * Creates a new {@code TestSuiteSession} for the specified {@link TestSuiteRun} and {@link TestRunCursor}.
     *
     * @param testSuiteRun  the test suite run associated with this session
     * @param testRunCursor the cursor for iterating over the test runs in this suite
     */
    public TestSuiteSession(TestSuiteRun testSuiteRun, TestRunCursor testRunCursor) {
        this.testSuiteRun = testSuiteRun;
        this.testRunCursor = testRunCursor;
        this.testRunReports = new ArrayList<>();
    }

    /**
     * Retrieves the {@link TestSuiteRun} associated with this session.
     *
     * @return the test suite run
     */
    public TestSuiteRun getTestSuiteRun() {
        return testSuiteRun;
    }

    /**
     * Returns a copy of the list of executed test run reports.
     *
     * @return a list of {@link TestRunReport} instances
     */
    public List<TestRunReport> getTestRunReports() {
        return new ArrayList<>(testRunReports);
    }

    /**
     * Adds the specified test run report to this session.
     *
     * @param report the test run report to add
     * @return this test suite session
     */
    public TestSuiteSession addTestRunReport(TestRunReport report) {
        testRunReports.add(report);
        return this;
    }

    /**
     * Returns the {@link TestRunCursor} for iterating over the test runs in this suite.
     *
     * @return the test run cursor
     */
    public TestRunCursor getTestRunCursor() {
        return testRunCursor;
    }

    /**
     * Returns the expiration timestamp for this test suite session.
     *
     * @return the instant at which this session expires, or {@code null} if no expiration timestamp has been set
     */
    public Instant getExpirationTimestamp() {
        return expirationTimestamp;
    }

    /**
     * Sets the expiration timestamp for this test suite session.
     *
     * @param expirationTimestamp the instant at which this session expires, or {@code null} to clear the expiration timestamp
     * @return this test suite session
     */
    public TestSuiteSession setExpirationTimestamp(Instant expirationTimestamp) {
        this.expirationTimestamp = expirationTimestamp;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TestSuiteSession store)) {
            return false;
        }
        return Objects.equals(testSuiteRun, store.testSuiteRun)
                && Objects.equals(testRunReports, store.testRunReports)
                && Objects.equals(testRunCursor, store.testRunCursor)
                && Objects.equals(expirationTimestamp, store.expirationTimestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(testSuiteRun, testRunReports, testRunCursor, expirationTimestamp);
    }
}

