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

package net.ihe.gazelle.maestro.business.context;

import net.ihe.gazelle.maestro.api.business.testreport.TestRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testrun.TestRunCursor;
import net.ihe.gazelle.maestro.api.business.testrun.TestSuiteRun;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * TestSuiteSession is a structure used to store report builder from executed TestRun,
 * testSet's id and reference to this testSet.
 */
public class TestSuiteSession {

    private final String testSuiteId;
    private final TestSuiteRun testSuiteRun;
    private final List<TestRunReportBuilder> testRunReportBuilders;
    private TestRunCursor testRunCursor;

    public TestSuiteSession(String setId, TestSuiteRun testSuiteRun) {
        this(setId, testSuiteRun, TestRunCursor.from(testSuiteRun));
    }

    public TestSuiteSession(String testSuiteId, TestSuiteRun testSuiteRun, TestRunCursor testRunCursor) {
        this.testSuiteId = testSuiteId;
        this.testSuiteRun = testSuiteRun;
        this.testRunReportBuilders = new ArrayList<>();
        this.testRunCursor = testRunCursor;
    }

    public String getTestSuiteId() {
        return testSuiteId;
    }

    public TestSuiteRun getTestSuiteRun() {
        return testSuiteRun;
    }

    public List<TestRunReportBuilder> getTestRunReportBuilders() {
        return new ArrayList<>(testRunReportBuilders);
    }

    public void addTestRunReportBuilder(TestRunReportBuilder reportBuilder) {
        testRunReportBuilders.add(reportBuilder);
    }

    public TestRunCursor getTestRunCursor() {
        return testRunCursor;
    }

    public void setTestRunCursor(TestRunCursor testRunCursor) {
        this.testRunCursor = testRunCursor;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TestSuiteSession store)) return false;
        return Objects.equals(testSuiteId, store.testSuiteId)
                && Objects.equals(testSuiteRun, store.testSuiteRun)
                && Objects.equals(testRunReportBuilders, store.testRunReportBuilders)
                && Objects.equals(testRunCursor, store.testRunCursor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(testSuiteId, testSuiteRun, testRunReportBuilders, testRunCursor);
    }
}
