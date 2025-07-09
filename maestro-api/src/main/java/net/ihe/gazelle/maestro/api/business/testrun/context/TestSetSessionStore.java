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

package net.ihe.gazelle.maestro.api.business.testrun.context;

import net.ihe.gazelle.maestro.api.business.testreport.TestRunReportBuilder;
import net.ihe.gazelle.maestro.api.business.testrun.TestSetRun;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * TestSetSessionStore is a structure used to store report builder from executed TestRun,
 * testSet's id and reference to this testSet.
 */
public class TestSetSessionStore {

    private final String setId;
    private final TestSetRun testSetRun;
    private final List<TestRunReportBuilder> testRunReportBuilders;

    public TestSetSessionStore(String setId, TestSetRun testRuns) {
        this.setId = setId;
        this.testSetRun = testRuns;
        this.testRunReportBuilders = new ArrayList<>();
    }

    public String getSetId() {
        return setId;
    }

    public TestSetRun getTestSetRun() {
        return testSetRun;
    }

    public List<TestRunReportBuilder> getTestRunReportBuilders() {
        return new ArrayList<>(testRunReportBuilders);
    }

    public void addTestRunReportBuilder(TestRunReportBuilder reportBuilder) {
        testRunReportBuilders.add(reportBuilder);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TestSetSessionStore store)) return false;
        return Objects.equals(setId, store.setId)
                && Objects.equals(testSetRun, store.testSetRun)
                && Objects.equals(testRunReportBuilders, store.testRunReportBuilders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(setId, testSetRun, testRunReportBuilders);
    }
}
